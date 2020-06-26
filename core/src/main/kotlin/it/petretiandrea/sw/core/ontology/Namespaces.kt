package it.petretiandrea.sw.core.ontology

import it.petretiandrea.sw.core.utils.IRIUtils
import org.apache.jena.shared.PrefixMapping
import org.apache.jena.vocabulary.OWL
import org.apache.jena.vocabulary.RDF
import org.apache.jena.vocabulary.RDFS

object Namespaces {
    val HOME_SEMANTIC = HomeOnto.NAMESPACE
    val WOT = WoT.NAMESPACE
    val SSN = IRIUtils.fromString("http://www.w3.org/ns/ssn/")
    val SOSA = IRIUtils.fromString("http://www.w3.org/ns/sosa/")


    fun getDefaultPrefixMapping(): PrefixMapping {
        return PrefixMapping.Factory.create().setNsPrefixes(PrefixMapping.Standard).setNsPrefixes(prefixMap)
    }

    private val prefixMap = mapOf(
        "schema" to "http://schema.org/",
        "rdf" to RDF.getURI().toString(),
        "owl" to OWL.NS,
        "rdfs" to RDFS.getURI().toString(),
        "td" to WoT.NAMESPACE.toString(),
        "ssn" to SSN.toString(),
        "sosa" to SOSA.toString(),
        "home" to HomeOnto.NAMESPACE.toString()
    )
}