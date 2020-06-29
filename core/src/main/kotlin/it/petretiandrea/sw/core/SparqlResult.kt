package it.petretiandrea.sw.core

import org.apache.jena.query.QuerySolution
import org.apache.jena.query.ResultSet
import org.apache.jena.rdf.model.Model

data class SparqlResult(val resultSet: ResultSet) : Iterable<QuerySolution> {
    val sourceModel : Model = resultSet.resourceModel
    override fun iterator(): Iterator<QuerySolution> = resultSet.iterator()
}