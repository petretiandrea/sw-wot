package it.petretiandrea.sw.jena.extension

import org.apache.jena.query.Query
import org.apache.jena.query.QueryFactory
import org.apache.jena.query.QuerySolution
import org.apache.jena.rdf.model.RDFNode
import org.apache.jena.shared.PrefixMapping
import org.apache.jena.sparql.core.Prologue

fun QuerySolution.getOption(varName: String) : RDFNode? {
    if (this.contains(varName)) {
        return this.get(varName)
    }
    return null
}

object QueryFactory : QueryFactory() {
    fun createWithPrefix(query: String, prefix: PrefixMapping): Query {
        val prologue = Prologue(prefix)
        return parse(Query(prologue), query, null, null)
    }
}