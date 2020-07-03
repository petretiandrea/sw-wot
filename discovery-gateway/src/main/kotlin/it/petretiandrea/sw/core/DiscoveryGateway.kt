package it.petretiandrea.sw.core

import it.petretiandrea.sw.core.impl.DiscoveryGatewayImpl
import it.petretiandrea.sw.core.runtime.ThingFetchRuntime
import it.petretiandrea.sw.core.utils.ThingCollectQuery
import it.petretiandrea.sw.core.utils.ThingQuery

// TODO: improve the interface definition: the main functionality are provided by search and collectData.
// Other methods can be expressed using the two main method search and collectData. e.g. a DSL that map into search or collect call.
interface DiscoveryGateway {
    suspend fun searchThings(query: ThingQuery): List<ConsumedThing>
    suspend fun search(graphPattern: String): List<ConsumedThing>

    suspend fun collectData(query: ThingCollectQuery): List<Value>
    suspend fun collectData(graphPattern: String, propertyMarker: String): List<Value>

    companion object {
        fun fromDirectory(host: String, port: Int): DiscoveryGateway = DiscoveryGatewayImpl(
            ThingDirectoryClient(host, port),
            ThingFetchRuntime.withFixedPool()
        )
    }
}


