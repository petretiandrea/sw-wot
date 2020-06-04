package it.petretiandrea.sw.utils

import openllet.core.OpenlletOptions
import openllet.jena.PelletInfGraph
import org.apache.jena.graph.GraphExtract
import org.apache.jena.graph.Node
import org.apache.jena.graph.TripleBoundary
import org.apache.jena.iri.IRI
import org.apache.jena.iri.IRIFactory
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.Statement
import java.net.URI
import java.util.*

object JenaUtils {

    fun extractSubGraph(resource: Node, model: Model): Model? {
        val extractor = GraphExtract(TripleBoundary.stopNowhere)
        val extractedGraph = extractor.extract(resource, model.graph)
        return ModelFactory.createModelForGraph(extractedGraph)
    }

    object Debug {
        fun enable() {
            OpenlletOptions.USE_TRACING = true
        }
        fun explainReasoning(model: Model, filterStmt: (Statement) -> Boolean) {
            if (model.graph !is PelletInfGraph) return
            val graph = model as PelletInfGraph
            model.listStatements().filterKeep(filterStmt).forEach {
                val exp = graph.explain(it)
                println("Explanation: $exp")
            }
        }
        fun checkInconsistency(model: Model) {
            if (model.graph !is PelletInfGraph) return
            val infGraph = model.graph as PelletInfGraph
            if(!infGraph.isConsistent) {
                println("Incosistency: " + infGraph.explainInconsistency())
            }
        }
    }
}

object IRIUtils {
    private val iriFactory = IRIFactory.iriImplementation()

    fun fromString(s: String) : IRI = iriFactory.create(s)
    fun fromURI(uri: URI) : IRI = iriFactory.create(uri)

    fun createUrnUuid(): IRI = fromString("urn:uuid:${UUID.randomUUID()}")
}