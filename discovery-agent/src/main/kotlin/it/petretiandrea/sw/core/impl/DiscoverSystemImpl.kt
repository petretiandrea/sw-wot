package it.petretiandrea.sw.core.impl

import it.petretiandrea.sw.core.*
import it.petretiandrea.sw.core.ontology.DeviceType
import it.petretiandrea.sw.core.ontology.FeatureProperty
import it.petretiandrea.sw.core.ontology.Namespaces
import it.petretiandrea.sw.core.ontology.WoT
import it.petretiandrea.sw.core.runtime.ThingFetchRuntime
import org.apache.jena.query.QueryExecutionFactory
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.Resource

class DiscoverSystemImpl(
    private val directoryClient: ThingDirectoryClient,
    private val fetchRuntime: ThingFetchRuntime) : DiscoverSystem {

    override suspend fun searchThings(deviceType: DeviceType): List<ConsumedThing> {
        val graphPattern = "?t a ${deviceType.name}."
        return search(graphPattern)
    }

    override suspend fun search(graphPattern: String): List<ConsumedThing> {
        return directoryClient.searchThings(graphPattern).map { ConsumedThingFactory.fromDescriptionModel(it) }
    }

    override suspend fun collectData(featureProperty: FeatureProperty) : List<Value> {
        val graphPattern = """
            ?t td:hasPropertyAffordance ?p.
            ?p sosa:observes home:${featureProperty.name}.
            ?p td:hasForm ?form.
        """.trimIndent()

        return collectData(graphPattern, "p")
    }

    override suspend fun collectData(deviceType: DeviceType, featureProperty: FeatureProperty) : List<Value> {
        val graphPattern = """
            ?t a home:${deviceType.name}.
            ?t td:hasPropertyAffordance ?p.
            ?p sosa:observes home:${featureProperty.name}.
            ?p td:hasForm ?form.
        """.trimIndent()
        return collectData(graphPattern, "p")
    }

    override suspend fun collectData(graphPattern: String, propertyMarker: String): List<Value> {
        val things = directoryClient.searchThings(graphPattern)
        val consumedThings = things.map { ConsumedThingFactory.fromDescriptionModel(it) }
        val propertiesName = propertiesNameFromQuery(things, graphPattern, propertyMarker)
        return fetchRuntime.fetchValues(consumedThings, propertiesName)
    }

    private fun propertiesNameFromQuery(things: List<ThingDescriptionRDF>, graphPattern: String, markerTag: String): List<String> {
        val localQuery = QueryFactory.createWithPrefix("SELECT * WHERE { $graphPattern }", Namespaces.getDefaultPrefixMapping())
        return things.map { QueryExecutionFactory.create(localQuery, it.model).execSelect().toSparqlResult() }
            .map {
                val property = it.first().getResource(markerTag)
                extractPropertyName(property, it.sourceModel)
            }
    }

    private fun extractPropertyName(property: Resource, thingModel: Model): String {
        return thingModel.listObjectsOfProperty(property, WoT.name()).toSet().first().asLiteral().string
    }
}

