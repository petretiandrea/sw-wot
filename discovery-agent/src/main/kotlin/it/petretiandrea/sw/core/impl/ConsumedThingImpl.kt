package it.petretiandrea.sw.core.impl

import it.petretiandrea.sw.core.ConsumedThing
import it.petretiandrea.sw.core.Form
import it.petretiandrea.sw.core.Value


class ConsumedThingImpl(
    private val properties: Map<String, Form>) : ConsumedThing {

    override suspend fun readProperty(propertyName: String): Value? {
        return properties[propertyName]?.let {
            println("doing request using form: $it")
            return "read:$propertyName"
        }
    }

    override suspend fun readAllProperties(vararg propertyNames: String): List<Value> {
        TODO("Not yet implemented")
    }
}

fun main() {



}