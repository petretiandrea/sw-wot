package it.petretiandrea.sw.core.ontology

import it.petretiandrea.sw.core.utils.IRIUtils

object WoT {
    val NAMESPACE = IRIUtils.fromString("https://www.w3.org/2019/wot/td#")

    val THING = OntologyUtils.createResource(NAMESPACE.toString(), "Thing")
}