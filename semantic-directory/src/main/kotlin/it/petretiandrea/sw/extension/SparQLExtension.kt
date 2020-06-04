package it.petretiandrea.sw.jena.extension

import org.apache.jena.query.QuerySolution
import org.apache.jena.rdf.model.RDFNode

fun QuerySolution.getOption(varName: String) : RDFNode? {
    if (this.contains(varName)) {
        return this.get(varName)
    }
    return null
}