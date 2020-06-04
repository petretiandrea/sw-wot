package it.petretiandrea.sw.core.impl

import it.petretiandrea.sw.Namespaces
import it.petretiandrea.sw.core.ThingDescriptionDirectory
import it.petretiandrea.sw.core.ThingDescriptionRDF
import it.petretiandrea.sw.getOrElse
import it.petretiandrea.sw.jena.extension.doReadTransaction
import it.petretiandrea.sw.jena.extension.doWriteTransaction
import it.petretiandrea.sw.utils.JenaUtils
import openllet.jena.PelletReasonerFactory
import org.apache.jena.iri.IRI
import org.apache.jena.query.Dataset
import org.apache.jena.query.QueryExecutionFactory
import org.apache.jena.query.ResultSetFormatter
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.reasoner.Reasoner
import org.apache.jena.riot.web.HttpOp
import org.apache.jena.tdb2.TDB2
import org.apache.jena.tdb2.TDB2Factory
import org.apache.jena.vocabulary.RDFS
import org.json.JSONObject
import java.io.ByteArrayOutputStream

/// exist def q = "ASK WHERE { <${iri}> a <${DCAT.DATASET}> . FILTER EXISTS { GRAPH <${iri}> { ?s ?p ?o }}}"
internal class ThingDescriptionDirectoryImpl : ThingDescriptionDirectory {

    private val reasoner: Reasoner
    private val aBoxDataset: Dataset

    init {
        JenaUtils.Debug.enable()

        reasoner = PelletReasonerFactory.theInstance().create() //createReasoner(schema)
        aBoxDataset = TDB2Factory.createDataset().apply {
            context.set(TDB2.symUnionDefaultGraph, true)
            doWriteTransaction {
                it.addNamedModel(Namespaces.WOT.toString(), createWotSchema())
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
            JenaUtils.Debug.checkInconsistency(infModel)
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

    private fun createWotSchema() : Model {
        val ontology = HttpOp.execHttpGet(Namespaces.WOT.toString(), "text/turtle").reader()
            .readText()
            .replace("schema:domainIncludes", "rdfs:domain")
            .replace("schema:rangeIncludes", "rdfs:range")
            .replace("schema:Text", "xsd:string")
            .replace("schema:Boolean", "xsd:boolean")
            .reader()

        return ModelFactory.createDefaultModel().also {
            it.read(ontology, Namespaces.SEMANTIC_DISCOVERY.toString(), "TURTLE") // TODO: define base IRI namespace into common space, is used from parser
            it.setNsPrefix("wotd", Namespaces.SEMANTIC_DISCOVERY.toString())
        }
    }

    private fun countStatements(model: Model): Int {
        return model.listStatements().toSet().size
    }

    object Bla {
        const val testQuery = "PREFIX td: <https://www.w3.org/2019/wot/td#>\n" + //https://www.w3.org/2019/wot/td#
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX schema: <https://www.w3.org/2019/wot/json-schema#>\n" +
                "PREFIX iot: <http://iotschema.org/>\n" +
                "\n" +
                "SELECT ?x WHERE { ?x rdf:type td:Thing. }";

        const val testQuery2 = "PREFIX td: <https://www.w3.org/2019/wot/td#>\n" + //https://www.w3.org/2019/wot/td#
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX schema: <https://www.w3.org/2019/wot/json-schema#>\n" +
                "PREFIX iot: <http://iotschema.org/>\n" +
                "\n" +
                "SELECT DISTINCT ?x WHERE { GRAPH ?x { ?y rdf:type td:Thing. } }";
    }
}