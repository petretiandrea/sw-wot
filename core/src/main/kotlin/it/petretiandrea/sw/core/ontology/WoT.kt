package it.petretiandrea.sw.core.ontology

import it.petretiandrea.sw.core.utils.IRIUtils
import org.apache.jena.rdf.model.Property

object WoT {
    val NAMESPACE = IRIUtils.fromString("https://www.w3.org/2019/wot/td#")

    val THING = OntologyUtils.createResource(NAMESPACE.toString(), "Thing")

    fun hasPropertyAffordance() : Property = OntologyUtils.createProperty(NAMESPACE.toString(), "hasPropertyAffordance")
    fun name() : Property = OntologyUtils.createProperty(NAMESPACE.toString(), "name")
    fun hasForm() : Property = OntologyUtils.createProperty(NAMESPACE.toString(), "hasForm")

}

object Hypermedia {
    val NAMESPACE = IRIUtils.fromString("https://www.w3.org/2019/wot/hypermedia#")

    fun hasTarget() : Property = OntologyUtils.createProperty(NAMESPACE.toString(), "hasTarget")
    fun hasOperationType() : Property = OntologyUtils.createProperty(NAMESPACE.toString(), "hasOperationType")
}