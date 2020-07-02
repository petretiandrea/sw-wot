package it.petretiandrea.sw.scenarios

import it.petretiandrea.sw.core.DiscoveryGateway
import it.petretiandrea.sw.core.ontology.FeatureProperty
import it.petretiandrea.sw.core.utils.Source
import it.petretiandrea.sw.core.utils.thingCollectQuery
import it.petretiandrea.sw.directory.Bootstrap


suspend fun main() {
    val things = Utils.loadThingsFromFolder(Source.fromResource("scenario1")!!.path)
    Bootstrap.bootSemanticDirectory(*things.toTypedArray())

    val discovery = DiscoveryGateway.fromDirectory("localhost", 10000)

    val average = discovery.collectData(thingCollectQuery {
        filter {
            canSense { FeatureProperty.Temperature }
        }
        collectOn { FeatureProperty.Temperature }
    }).map { it.toDouble() }.average()

    println(average)
}