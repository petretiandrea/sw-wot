package it.petretiandrea.sw.core.ontology

import it.petretiandrea.sw.core.utils.IRIUtils
import org.apache.jena.rdf.model.Property

object WoT {
    val NAMESPACE = IRIUtils.fromString("https://www.w3.org/2019/wot/td#")

    val THING = OntologyUtils.createResource(NAMESPACE.toString(), "Thing")

    fun name() : Property = OntologyUtils.createProperty(NAMESPACE.toString(), "name")
    fun hasForm() : Property = OntologyUtils.createProperty(NAMESPACE.toString(), "hasForm")
}