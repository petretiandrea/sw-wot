package it.petretiandrea.sw

import io.vertx.core.Vertx
import io.vertx.kotlin.core.deployVerticleAwait
import it.petretiandrea.sw.core.ThingDescriptionDirectory
import it.petretiandrea.sw.core.ThingDescriptionRDF
import it.petretiandrea.sw.parsing.TDParser
import it.petretiandrea.sw.restapi.RestApiSemanticDiscovery
import it.petretiandrea.sw.utils.Source
import org.apache.jena.query.ResultSet
import org.apache.jena.query.ResultSetFactory
import org.apache.jena.query.ResultSetFormatter
import org.json.JSONObject

object Test {
    val commonPrefix = """
        PREFIX schema: <http://schema.org/>
        PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        PREFIX owl: <http://www.w3.org/2002/07/owl#>
        PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
        PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
        PREFIX td: <https://www.w3.org/2019/wot/td#>
        PREFIX home: <http://www.sw.org/andreapetreti/home-wot#>
        PREFIX ssn: <http://www.w3.org/ns/ssn/>
        PREFIX sosa: <http://www.w3.org/ns/sosa/>
        %s
    """.trimIndent()

    private fun createQuery(query: String) = commonPrefix.format(query)

    val sparqlExample1 = createQuery("SELECT * WHERE { ?b sosa:observes home:Ambient_Temperature. }")
    val sparqlExample2 = createQuery("""
        SELECT * WHERE { ?property sosa:actsOnProperty home:Ambient_Temperature. ?property td:name ?name. }
    """.trimIndent())

}

fun main() {
    val vertx = Vertx.vertx()

    val parser = TDParser()
    val tdd = ThingDescriptionDirectory()
    val restVerticle = RestApiSemanticDiscovery(parser, tdd)

    vertx.deployVerticle(restVerticle)

    val jsonThing = JSONObject(Source.fromResource("thermometer.json")!!.readText())
    val jsonThing2 = JSONObject(Source.fromResource("thermostat.json")!!.readText())
    tdd.register(ThingDescriptionRDF.fromRDF(parser.parseRDF(jsonThing)!!))
    tdd.register(ThingDescriptionRDF.fromRDF(parser.parseRDF(jsonThing2)!!))

    tdd.querySparql(Test.sparqlExample1).let { ResultSetFactory.fromJSON(it.toString().byteInputStream()) }.let { ResultSetFormatter.out(it) }
    tdd.querySparql(Test.sparqlExample2).let { ResultSetFactory.fromJSON(it.toString().byteInputStream()) }.let { ResultSetFormatter.out(it) }
}