package it.petretiandrea.sw.agent

import it.petretiandrea.sw.core.DiscoverSystem
import it.petretiandrea.sw.core.ontology.DeviceType
import it.petretiandrea.sw.core.ontology.FeatureProperty
import it.petretiandrea.sw.core.ontology.LocationType


suspend fun main() {

    val discover = DiscoverSystem.fromDirectory("localhost", 10000)

    println("Thermometer temperature")
    discover.collectData(DeviceType.Thermometer, FeatureProperty.AmbientTemperature).forEach {
        println(it)
    }

    println("All ambient temperature")
    discover.collectData(FeatureProperty.AmbientTemperature).forEach {
        println(it)
    }

    discover.collectData(LocationType.GPS).forEach {
        println(it)
    }
}