package it.petretiandrea.sw.ontology

import it.petretiandrea.sw.utils.IRIUtils

object Namespaces {
    val HOME_SEMANTIC = HomeOnto.NAMESPACE
    val WOT = IRIUtils.fromString("https://www.w3.org/2019/wot/td#")
    val SSN = IRIUtils.fromString("http://www.w3.org/ns/ssn/")
    val SOSA = IRIUtils.fromString("http://www.w3.org/ns/sosa/")
}