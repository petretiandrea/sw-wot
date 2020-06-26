package it.petretiandrea.sw.directory.core.impl

import it.petretiandrea.sw.core.getOrElse
import it.petretiandrea.sw.core.ontology.HomeOnto
import it.petretiandrea.sw.core.ontology.Namespaces
import it.petretiandrea.sw.core.ontology.WoT
import it.petretiandrea.sw.core.utils.IRIUtils
import it.petretiandrea.sw.core.utils.JenaUtils
import it.petretiandrea.sw.directory.core.ThingDescriptionDirectory
import it.petretiandrea.sw.directory.core.ThingDescriptionRDF
import it.petretiandrea.sw.directory.extension.createCopy
import it.petretiandrea.sw.directory.extension.doReadTransaction
import it.petretiandrea.sw.directory.extension.doWriteTransaction
import it.petretiandrea.sw.jena.extension.QueryFactory
import org.apache.jena.iri.IRI
import org.apache.jena.query.*
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.reasoner.Reasoner
import org.apache.jena.reasoner.ReasonerRegistry
import org.apache.jena.shared.PrefixMapping
import org.apache.jena.sparql.resultset.RDFOutput
import org.apache.jena.sparql.util.PrefixMapping2
import org.apache.jena.tdb2.TDB2
import org.apache.jena.tdb2.TDB2Factory
import org.apache.jena.util.PrefixMappingUtils
import org.apache.jena.vocabulary.RDF
import org.json.JSONObject
import java.io.ByteArrayOutputStream

/// exist def q = "ASK WHERE { <${iri}> a <${DCAT.DATASET}> . FILTER EXISTS { GRAPH <${iri}> { ?s ?p ?o }}}"
internal class ThingDescriptionDirectoryImpl : ThingDescriptionDirectory {

    private val reasoner: Reasoner
    private val aBoxDataset: Dataset

    init {
        JenaUtils.Debug.enable()

        reasoner = ReasonerRegistry.getOWLReasoner() // PelletReasonerFactory.theInstance().create()
        aBoxDataset = TDB2Factory.createDataset().apply {
            context.set(TDB2.symUnionDefaultGraph, true)
            doWriteTransaction {
                it.addNamedModel(Namespaces.HOME_SEMANTIC.toString(), HomeOnto.getModel())
            }
        } // TODO: is only in memory for now
    }

    override fun register(td: ThingDescriptionRDF): Boolean {
        aBoxDataset.apply {
            val exist = doReadTransaction { it.containsNamedModel(td.resourceIdentifier.toString()) }.getOrElse(true)
            if(!exist) doWriteTransaction { it.addNamedModel(td.resourceIdentifier.toString(), td.model) }
        }
        return true
    }

    override fun querySparql(query: String): JSONObject {
        val json = aBoxDataset.doReadTransaction {
            val jsonStream = ByteArrayOutputStream()
            val infModel = ModelFactory.createInfModel(reasoner, it.getNamedModel("urn:x-arq:UnionGraph"))
            //JenaUtils.Debug.checkInconsistency(infModel)
            infModel.prepare()
            QueryExecutionFactory.create(query, infModel)
                .execSelect()
                .let { ResultSetFormatter.outputAsJSON(jsonStream, it) }

            JSONObject(jsonStream.toString("UTF-8"))
        }
        return json.getOrElse(JSONObject())
    }

    override fun searchThing(graphPatternQuery: String?, limit: Int?): List<ThingDescriptionRDF> {
        val queryString = when(graphPatternQuery.getOrElse("")) {
            "" -> "DESCRIBE * WHERE { ?t a td:Thing } " + limit?.let { " LIMIT $it" }
            else -> "DESCRIBE * WHERE { $graphPatternQuery } " + limit?.let { " LIMIT $it" }
        }

        val query = QueryFactory.createWithPrefix(queryString, Namespaces.getDefaultPrefixMapping())
        //query.
        query.havingExprs.forEach { println(it) }
        val thingIds = aBoxDataset.doReadTransaction {
            val infModel = ModelFactory.createInfModel(reasoner, it.getNamedModel("urn:x-arq:UnionGraph"))
            //JenaUtils.Debug.checkInconsistency(infModel)
            infModel.prepare()
            val solution = QueryExecutionFactory.create(query, infModel).execDescribe()
            solution.listStatements().forEach { println(it) }
            getThingIds(solution)
        }.getOrElse(emptyList())


        return retrieveThings(*thingIds.toTypedArray())
    }

    private fun retrieveThings(vararg thingIds: String): List<ThingDescriptionRDF> {
        return aBoxDataset.doReadTransaction { dataset ->
            thingIds.map {
                ThingDescriptionRDF(IRIUtils.fromString(it), dataset.getNamedModel(it).createCopy())
            }
        }.getOrElse(emptyList())
    }

    private fun getThingIds(model: Model): List<String> {
        return model.listStatements(null, RDF.type, WoT.THING).toSet()
            .map { stmt -> stmt.subject.uri }
    }

    override fun get(thingIdentifier: IRI): ThingDescriptionRDF? {
        val query = "CONSTRUCT { ?s ?p ?o } WHERE { GRAPH <$thingIdentifier> { ?s ?p ?o }}"
        return aBoxDataset.doReadTransaction {
            QueryExecutionFactory.create(query, it)
                .apply { context.set(TDB2.symUnionDefaultGraph, true) }
                .let { ThingDescriptionRDF(thingIdentifier, it.execConstruct()) }
        }
    }

    private fun countStatements(model: Model): Int {
        return model.listStatements().toSet().size
    }
}