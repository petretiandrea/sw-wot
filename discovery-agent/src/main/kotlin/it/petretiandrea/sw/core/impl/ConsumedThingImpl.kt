package it.petretiandrea.sw.core.impl

import it.petretiandrea.sw.core.ConsumedThing
import it.petretiandrea.sw.core.ThingDescriptionRDF
import it.petretiandrea.sw.core.Value

class ConsumedThingImpl(thingDescriptionRDF: ThingDescriptionRDF) : ConsumedThing {
    override suspend fun readProperty(propertyName: String): Value {
        println("readProperty $propertyName")
        return "read:$propertyName"
    }

    override suspend fun readAllProperties(vararg propertyNames: String): List<Value> {
        TODO("Not yet implemented")
    }

}