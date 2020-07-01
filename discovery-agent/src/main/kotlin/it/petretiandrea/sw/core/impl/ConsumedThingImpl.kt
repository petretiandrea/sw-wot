package it.petretiandrea.sw.core.impl

import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.ext.web.client.sendAwait
import it.petretiandrea.sw.core.ConsumedThing
import it.petretiandrea.sw.core.Form
import it.petretiandrea.sw.core.Value
import kotlin.random.Random

class ConsumedThingImpl(
    override val properties: Map<String, Form>) : ConsumedThing {

    private val formClient = MockBindingProtocolHttp(DefaultGenerator) //HttpFormClient

    // in real implementation, need to choose the right client based on Form, using method like getClientFor
    // https://github.com/eclipse/thingweb.node-wot/blob/aab80bfa0fa15eb0aff293273f998333e8b7df28/packages/core/src/consumed-thing.ts#L200
    override suspend fun readProperty(propertyName: String): Value? {
        return properties[propertyName]?.let {
            formClient.read(it)
        }
    }

    override suspend fun readAllProperties(vararg propertyNames: String): List<Value> {
        TODO("Not yet implemented")
    }
}

object HttpFormClient {
    private val vertx = Vertx.vertx()
    private val web = WebClient.create(vertx)

    suspend fun read(form: Form): Value? {
        return try {
            println("Doing request to: ${form.href}")
            web.get(form.href).sendAwait().bodyAsString()
        }catch (e: Exception) {
            null
        }
    }
}