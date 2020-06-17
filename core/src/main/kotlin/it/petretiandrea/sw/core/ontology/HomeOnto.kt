package it.petretiandrea.sw.core.ontology

import it.petretiandrea.sw.core.utils.IRIUtils
import it.petretiandrea.sw.core.utils.Source
import org.apache.jena.rdf.model.*
import org.apache.jena.riot.web.HttpOp
import java.io.File
import java.io.Reader

object HomeOnto {
    val NAMESPACE = IRIUtils.fromString("http://www.sw.org/andreapetreti/home#")
    private val HOME_ONTO_PATH = Source.fromResource("ontologies/home-wot.owl")?.path

    fun getModel(): Model {
        val homeOnto = loadModel(File(HOME_ONTO_PATH!!).reader())
        return ModelFactory.createDefaultModel().apply {
            read(wotOntologyReader(), Namespaces.WOT.toString(), "TURTLE")
            //read(Namespaces.SSN.toString())
            //read(Namespaces.SOSA.toString())
            add(homeOnto)
            setNsPrefix("td", Namespaces.WOT.toString())
        }
    }

    private fun wotOntologyReader() : Reader {
        return HttpOp.execHttpGet(Namespaces.WOT.toString(), "text/turtle").reader()
            .readText()
            .replace("schema:domainIncludes", "rdfs:domain")
            .replace("schema:rangeIncludes", "rdfs:range")
            .replace("schema:Text", "xsd:string")
            .replace("schema:Boolean", "xsd:boolean")
            .reader()
    }

    private fun loadModel(reader: Reader) : Model {
        return ModelFactory.createDefaultModel().apply {
            read(reader, NAMESPACE.toString(), "TURTLE")
        }
    }
}