package it.petretiandrea.sw.core.runtime

import it.petretiandrea.sw.core.ConsumedThing
import it.petretiandrea.sw.core.Value
import kotlinx.coroutines.*
import java.util.concurrent.Executors

interface ThingFetchRuntime {

    suspend fun fetchValues(things: List<ConsumedThing>, properties: List<String>): List<Value>

    companion object {

        fun withFixedPool(): ThingFetchRuntime = object : ThingFetchRuntime {

            private val executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 3).asCoroutineDispatcher()
            private val dispatcher = CoroutineScope(executor)

            override suspend fun fetchValues(things: List<ConsumedThing>, properties: List<String>): List<Value> {
                return things.zip(properties)
                    .map { (thing, property) -> dispatcher.async { thing.readProperty(property) } }
                    .mapNotNull { it.await() }
            }
        }
    }
}
