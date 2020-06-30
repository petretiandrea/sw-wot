package it.petretiandrea.sw.agent

import it.petretiandrea.sw.core.DiscoverSystem
import it.petretiandrea.sw.core.ontology.DeviceType
import it.petretiandrea.sw.core.ontology.FeatureProperty
import it.petretiandrea.sw.core.ontology.LocationType
import it.petretiandrea.sw.core.utils.thingCollectQuery
import it.petretiandrea.sw.core.utils.thingQuery


suspend fun main() {

    val discover = DiscoverSystem.fromDirectory("localhost", 10000)

    discover.searchThings(thingQuery {
        observes {
            feature { FeatureProperty.AmbientTemperature }
        }
        actsOn { feature { FeatureProperty.AmbientTemperature } }
    }).forEach { println(it) }

    println("All ambient temperature")
    discover.collectData(thingCollectQuery {
        filter {
            deviceType { DeviceType.Thermostat }
        }
        collectOn { FeatureProperty.AmbientTemperature }
    })
}


