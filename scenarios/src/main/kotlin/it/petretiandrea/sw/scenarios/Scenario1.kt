package it.petretiandrea.sw.scenarios

import it.petretiandrea.sw.core.DiscoverSystem
import it.petretiandrea.sw.core.ontology.FeatureProperty
import it.petretiandrea.sw.core.utils.Source
import it.petretiandrea.sw.core.utils.thingCollectQuery
import it.petretiandrea.sw.directory.Bootstrap


suspend fun main() {
    val things = Utils.loadThingsFromFolder(Source.fromResource("scenario1")!!.path)
    Bootstrap.bootSemanticDirectory(*things.toTypedArray())

    val discovery = DiscoverSystem.fromDirectory("localhost", 10000)

    val query = thingCollectQuery {
        filter {
            observes { FeatureProperty.Temperature }
        }
        collectOn { FeatureProperty.Temperature }
    }
    println(discovery.collectData(query).map { it.toDouble() }.average())
}