package it.petretiandrea.sw.core.impl

import it.petretiandrea.sw.core.ThingDescriptionDirectory
import it.petretiandrea.sw.core.ThingDescriptionRDF
import it.petretiandrea.sw.getOrElse
import it.petretiandrea.sw.jena.extension.doReadTransaction
import it.petretiandrea.sw.jena.extension.doWriteTransaction
import it.petretiandrea.sw.ontology.HomeOnto
import it.petretiandrea.sw.ontology.Namespaces
import it.petretiandrea.sw.utils.JenaUtils
import openllet.jena.PelletReasonerFactory
import org.apache.jena.iri.IRI
import org.apache.jena.query.Dataset
import org.apache.jena.query.QueryExecutionFactory
import org.apache.jena.query.ResultSetFormatter
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.reasoner.Reasoner
import org.apache.jena.tdb2.TDB2
import org.apache.jena.tdb2.TDB2Factory
import org.json.JSONObject
import java.io.ByteArrayOutputStream

/// exist def q = "ASK WHERE { <${iri}> a <${DCAT.DATASET}> . FILTER EXISTS { GRAPH <${iri}> { ?s ?p ?o }}}"
internal class ThingDescriptionDirectoryImpl : ThingDescriptionDirectory {

    private val reasoner: Reasoner
    private val aBoxDataset: Dataset

    init {
        JenaUtils.Debug.enable()

        reasoner = PelletReasonerFactory.theInstance().create()
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
            // TODO: check exist
            doWriteTransaction { it.addNamedModel(td.resourceIdentifier.toString(), td.model) }
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