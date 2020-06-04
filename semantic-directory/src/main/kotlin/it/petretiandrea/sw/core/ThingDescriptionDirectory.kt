package it.petretiandrea.sw.core

import it.petretiandrea.sw.core.impl.ThingDescriptionDirectoryImpl
import org.apache.jena.iri.IRI
import org.json.JSONObject

interface ThingDescriptionDirectory {
    fun register(td: ThingDescriptionRDF): Boolean
    fun get(thingIdentifier: IRI) : ThingDescriptionRDF?

    fun querySparql(query: String) : JSONObject

    companion object {
        operator fun invoke(): ThingDescriptionDirectory = ThingDescriptionDirectoryImpl()
    }
}