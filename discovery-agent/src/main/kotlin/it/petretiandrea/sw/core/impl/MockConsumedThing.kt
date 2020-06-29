package it.petretiandrea.sw.core.impl

import it.petretiandrea.sw.core.ConsumedThing
import it.petretiandrea.sw.core.Value
import kotlin.random.Random

typealias GenFunction = (String) -> Value

object DefaultGenerator : GenFunction {
    override fun invoke(name: String): Value = when (name) {
        "roomTemperature" -> Random.nextDouble(20.0, 30.0).toString()
        "location" -> "bedroom"
        else -> ""
    }
}

class MockConsumedThing(val genFunction: GenFunction) : ConsumedThing {

    companion object {
        operator fun invoke() = MockConsumedThing(DefaultGenerator)
    }

    override suspend fun readProperty(propertyName: String): Value {
        return genFunction(propertyName)
    }

    override suspend fun readAllProperties(vararg propertyNames: String): List<Value> {
        TODO("Not yet implemented")
    }

}