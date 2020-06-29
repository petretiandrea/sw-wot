package it.petretiandrea.sw.core

import it.petretiandrea.sw.core.impl.ConsumedThingImpl
import it.petretiandrea.sw.core.impl.MockConsumedThing
import org.json.JSONObject

// could be a more complex structure that know the real concrete type
typealias Value = String

interface ConsumedThing {
    suspend fun readProperty(propertyName: String) : Value
    suspend fun readAllProperties(vararg propertyNames: String): List<Value>
}

object ConsumedThingFactory {

    fun fromDescriptionModel(thingDescriptionRDF: ThingDescriptionRDF) = MockConsumedThing()//ConsumedThingImpl(thingDescriptionRDF)
    //fun fromDescriptionJson(thingDescription: JSONObject)
}