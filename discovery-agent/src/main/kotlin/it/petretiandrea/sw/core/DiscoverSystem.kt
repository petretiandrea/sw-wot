package it.petretiandrea.sw.core

import it.petretiandrea.sw.core.impl.DiscoverSystemImpl
import it.petretiandrea.sw.core.ontology.*
import it.petretiandrea.sw.core.runtime.ThingFetchRuntime
import org.apache.jena.query.QueryExecutionFactory
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.Resource

interface DiscoverSystem {
    suspend fun searchThings(deviceType: DeviceType): List<ConsumedThing>
    suspend fun search(graphPattern: String): List<ConsumedThing>
    suspend fun collectData(featureProperty: FeatureProperty) : List<Value>
    suspend fun collectData(deviceType: DeviceType, featureProperty: FeatureProperty) : List<Value>
    suspend fun collectData(graphPattern: String, propertyMarker: String): List<Value>

    companion object {
        fun fromDirectory(host: String, port: Int): DiscoverSystem = DiscoverSystemImpl(
            ThingDirectoryClient("localhost", 10000),
            ThingFetchRuntime.withFixedPool()
        )
    }
}


