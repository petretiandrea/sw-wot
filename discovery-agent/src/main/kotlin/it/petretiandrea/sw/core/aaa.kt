package it.petretiandrea.sw.core

import io.vertx.core.json.JsonObject

/*
- at high level we can search thing by tipology
- we can search thing by featureproperty
- collect data of thing by featureproperty
 */

interface SearchEngine {
    suspend fun searchThingsByType(deviceType: HomeOnto.DeviceType): List<JsonObject>
}

object MyEngine : SearchEngine {

    private val directoryClient: ThingDirectoryClient = ThingDirectoryClient("localhost", 8080)

    val commonPrefix = """
        PREFIX schema: <http://schema.org/>
        PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        PREFIX owl: <http://www.w3.org/2002/07/owl#>
        PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
        PREFIX td: <https://www.w3.org/2019/wot/td#>
        PREFIX home: <http://www.sw.org/andreapetreti/home-wot#>
        PREFIX ssn: <http://www.w3.org/ns/ssn/>
        PREFIX sosa: <http://www.w3.org/ns/sosa/>
        %s
    """.trimIndent()

    override suspend fun searchThingsByType(deviceType: HomeOnto.DeviceType): List<JsonObject> {
        val query = commonPrefix.format(
            "SELECT ?thingId WHERE { ?thingId a home:${deviceType.typeName}. }"
        )
        val response = directoryClient.querySparql(query)
            .map { solution -> solution.getResource("thingId").uri }



         return emptyList()
    }
}

object HomeOnto {
    sealed class DeviceType(val typeName: String) {
        object Thermostat : DeviceType("Thermostat")
        object Thermometer : DeviceType("Thermometer")
    }
}


suspend fun main() {

    MyEngine.searchThingsByType(HomeOnto.DeviceType.Thermostat)

}