package it.petretiandrea.sw.core

import io.vertx.core.json.JsonObject
import it.petretiandrea.sw.core.ontology.Namespaces
import it.petretiandrea.sw.core.ontology.OntologyUtils
import it.petretiandrea.sw.core.ontology.WoT
import org.apache.jena.assembler.Mode
import org.apache.jena.query.QueryExecutionFactory
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.Resource

/*
- at high level we can search thing by tipology
- we can search thing by featureproperty
- collect data of thing by featureproperty
 */

interface SearchEngine {

}

object MyEngine : SearchEngine {

    val directoryClient: ThingDirectoryClient = ThingDirectoryClient("localhost", 10000)

    suspend fun readValuesOf(deviceType: HomeOnto.DeviceType, valueType: String) : List<Value> {
        val graphPattern = """
            ?t td:hasPropertyAffordance ?p.
            ?p sosa:observes home:$valueType.
            ?p td:hasForm ?form.
        """.trimIndent()

        val localQuery = QueryFactory.createWithPrefix("SELECT * WHERE { $graphPattern }", Namespaces.getDefaultPrefixMapping())

        val things = directoryClient.searchThings(graphPattern)
        val consumedThings = things.map { ConsumedThingFactory.fromDescriptionModel(it) }
        val propertyName = things.map { QueryExecutionFactory.create(localQuery, it.model).execSelect().toSparqlResult() }
            .map {
                val property = it.first().getResource("p")
                extractPropertyName(property, it.sourceModel)
            }

        return consumedThings.zip(propertyName).map { (thing, property) -> thing.readProperty(property) }
    }

    private fun extractPropertyName(property: Resource, thingModel: Model): String {
        return thingModel.listObjectsOfProperty(property, WoT.name()).toSet().first().asLiteral().string
    }

}

object HomeOnto {
    sealed class DeviceType(val typeName: String) {
        object Thermostat : DeviceType("Thermostat")
        object Thermometer : DeviceType("Thermometer")
    }
}


suspend fun main() {

    MyEngine.readValuesOf(HomeOnto.DeviceType.Thermometer, "Ambient_Temperature").forEach {
        println(it)
    }

}