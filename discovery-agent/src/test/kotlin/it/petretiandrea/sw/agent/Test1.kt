package it.petretiandrea.sw.agent

import it.petretiandrea.sw.core.ConsumedThing
import it.petretiandrea.sw.core.DiscoverSystem
import it.petretiandrea.sw.core.Value
import it.petretiandrea.sw.core.ontology.DeviceType
import it.petretiandrea.sw.core.ontology.FeatureProperty
import it.petretiandrea.sw.core.utils.thingCollectQuery
import it.petretiandrea.sw.core.utils.thingQuery


suspend fun main() {
    val discover = DiscoverSystem.fromDirectory("localhost", 10000)

    //example1(discover)
    //example2(discover)
    example3(discover)
}

suspend fun example3(discover: DiscoverSystem) {
    val query = thingCollectQuery {
        filter {
            canSense { feature { FeatureProperty.Temperature } }
        }
        collectOn { FeatureProperty.Temperature }
    }
    prettyPrintValues(discover.collectData(query))
}

suspend fun example2(discover: DiscoverSystem) {
    val query = thingCollectQuery {
        filter {
            deviceType { DeviceType.Thermostat }
        }
        collectOn { FeatureProperty.AmbientTemperature }
    }
    prettyPrintValues(discover.collectData(query))
}

suspend fun example1(discoverSystem: DiscoverSystem) {
    val query = thingQuery {
        canSense {
            feature { FeatureProperty.AmbientTemperature }
        }
        canActOn { feature { FeatureProperty.AmbientTemperature } }
    }

    prettyPrintThings(discoverSystem.searchThings(query))
}

private fun prettyPrintThings(things: List<ConsumedThing>) {
    println("Thing found: ${things.size}")
    things.forEachIndexed { index, elem ->
        println("$index. With properties:")
        elem.properties.forEach { println("\t ${it.key}") }
    }
}

private fun prettyPrintValues(values: List<Value>) {
    println("Collected ${values.size} values")
    values.forEach { println(it) }
    println("===============================")
}

