package it.petretiandrea.sw.core.impl

import it.petretiandrea.sw.core.*
import it.petretiandrea.sw.core.ontology.*
import it.petretiandrea.sw.core.runtime.ThingFetchRuntime
import it.petretiandrea.sw.core.utils.ThingCollectQuery
import it.petretiandrea.sw.core.utils.ThingQuery
import org.apache.jena.query.QueryExecutionFactory
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.Resource

class DiscoveryGatewayImpl(
    private val directoryClient: ThingDirectoryClient,
    private val fetchRuntime: ThingFetchRuntime) : DiscoveryGateway {

    override suspend fun searchThings(query: ThingQuery): List<ConsumedThing> = search(buildGraphPatternFromQuery(query))

    override suspend fun search(graphPattern: String): List<ConsumedThing> {
        return directoryClient.searchThings(graphPattern).map { ConsumedThingFactory.fromDescriptionModel(it) }
    }

    override suspend fun collectData(query: ThingCollectQuery): List<Value> {
        val queryPattern = query.filter?.let { buildGraphPatternFromQuery(it) }.orEmpty().let {
            it +  """
                ?t td:hasPropertyAffordance ?collectProperty.
                ?collectProperty ssn:forProperty ?fp.
                ?fp a home:${query.collectOn.name}.
                ?collectProperty td:hasForm ?form.
            """.trimIndent()
        }
        return collectData(queryPattern, "collectProperty")
    }

    override suspend fun collectData(graphPattern: String, propertyMarker: String): List<Value> {
        val things = directoryClient.searchThings(graphPattern)
        val consumedThings = things.map { ConsumedThingFactory.fromDescriptionModel(it) }
        val propertiesName = propertiesNameFromQuery(things, graphPattern, propertyMarker)
        return fetchRuntime.fetchValues(consumedThings, propertiesName)
    }

    private fun buildGraphPatternFromQuery(query: ThingQuery): String {
        val patternBuilder = StringBuilder()
        query.deviceType?.let { patternBuilder.appendln("?t a home:${it.name}.") }
        query.observeProperties.forEachIndexed { index, prop ->
            patternBuilder.appendln("?t td:hasPropertyAffordance ?p$index.")
            patternBuilder.appendln("?p$index ssn:forProperty ?fp$index.")
            patternBuilder.appendln("?fp$index a home:${prop.name}.")
        }
        query.actsProperties.forEachIndexed { index, prop ->
            patternBuilder.appendln("?t td:hasActionAffordance ?a$index.")
            patternBuilder.appendln("?a$index ssn:forProperty ?afp$index.")
            patternBuilder.appendln("?afp$index a home:${prop.name}.")
        }
        return patternBuilder.toString()
    }

    private fun propertiesNameFromQuery(things: List<ThingDescriptionRDF>, graphPattern: String, markerTag: String): List<String> {
        return things.fold(ModelFactory.createDefaultModel(), { model, thing -> model.add(thing.model) })
            .getInferredModel(HomeOnto.getReasoner())
            .use { inferred ->
                val localQuery = QueryFactory.createWithPrefix("SELECT * WHERE { $graphPattern }", Namespaces.getDefaultPrefixMapping())
                QueryExecutionFactory.create(localQuery, inferred).execSelect().toSparqlResult()
                    .map {
                        val property = it.getResource(markerTag)
                        extractPropertyName(property, inferred)
                    }
            }
    }

    private fun extractPropertyName(property: Resource, thingModel: Model): String {
        return thingModel.listObjectsOfProperty(property, WoT.name()).toSet().first().asLiteral().string
    }
}

