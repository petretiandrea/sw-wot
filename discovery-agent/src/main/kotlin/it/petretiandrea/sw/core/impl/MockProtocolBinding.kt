package it.petretiandrea.sw.core.impl

import it.petretiandrea.sw.core.ConsumedThing
import it.petretiandrea.sw.core.Form
import it.petretiandrea.sw.core.Value
import kotlin.random.Random

typealias GenFunction = (String) -> Value

object DefaultGenerator : GenFunction {
    override fun invoke(name: String): Value = when {
        name.contains("temperature") -> Random.nextDouble(20.0, 30.0).toString()
        name.contains("bodyTemp") -> Random.nextDouble(36.0, 37.5).toString()
        name.contains("location") -> "bedroom"
        else -> ""
    }
}

class MockBindingProtocolHttp(val genFunction: GenFunction) {
    fun read(form: Form): Value? {
        return try {
            println("Doing request to: ${form.href}")
            return genFunction(form.href)
            //TODO: in real case web.get(form.href).sendAwait().bodyAsString()
        }catch (e: Exception) {
            null
        }
    }
}