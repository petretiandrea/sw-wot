package it.petretiandrea.sw.core

import org.apache.jena.query.Dataset
import org.apache.jena.query.DatasetFactory
import org.apache.jena.query.ResultSet
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.reasoner.Reasoner

fun ResultSet.toSparqlResult()  = SparqlResult(this)

fun Model.getInferredModel(reasoner: Reasoner): Model {
    val infModel =  ModelFactory.createInfModel(reasoner, this)
    infModel.prepare()
    return infModel
}

inline fun <R> Model.use(block: (Model) -> R) : R {
    try {
        return block(this)
    } finally {
        this.close()
    }
}