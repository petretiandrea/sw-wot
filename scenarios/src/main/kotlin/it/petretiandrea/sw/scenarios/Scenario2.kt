package it.petretiandrea.sw.scenarios

import it.petretiandrea.sw.core.DiscoveryGateway
import it.petretiandrea.sw.core.ontology.FeatureProperty
import it.petretiandrea.sw.core.utils.Source
import it.petretiandrea.sw.core.utils.thingQuery
import it.petretiandrea.sw.directory.Bootstrap

suspend fun main() {
    val things = Utils.loadThingsFromFolder(Source.fromResource("scenario2")!!.path)
    Bootstrap.bootSemanticDirectory(*things.toTypedArray())

    val discovery = DiscoveryGateway.fromDirectory("localhost", 10000)

    val thingsDiscovered = discovery.searchThings(thingQuery {
        canSense {
            feature { FeatureProperty.AmbientTemperature }
            feature { FeatureProperty.AmbientHumidity }
        }
        canActOn { FeatureProperty.AmbientTemperature }
    })
    println("Found ${thingsDiscovered.size} things!")
}