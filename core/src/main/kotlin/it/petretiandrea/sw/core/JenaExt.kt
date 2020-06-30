package it.petretiandrea.sw.core

import org.apache.jena.query.ResultSet

fun ResultSet.toSparqlResult()  = SparqlResult(this)