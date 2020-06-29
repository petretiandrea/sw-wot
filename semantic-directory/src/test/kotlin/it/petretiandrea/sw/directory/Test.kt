package it.petretiandrea.sw.directory

import io.vertx.core.Vertx
import it.petretiandrea.sw.core.utils.Source
import it.petretiandrea.sw.directory.core.ThingDescriptionDirectory
import it.petretiandrea.sw.directory.parsing.TDParser
import it.petretiandrea.sw.directory.restapi.RestApiSemanticDiscovery
import org.json.JSONObject

fun main() {
    val vertx = Vertx.vertx()

    val parser = TDParser()
    val tdd = ThingDescriptionDirectory()
    val restVerticle = RestApiSemanticDiscovery(parser, tdd, 10000)

    vertx.deployVerticle(restVerticle)

    val jsonThing = JSONObject(Source.fromResource("thermometer.json")!!.readText())
    val jsonThing2 = JSONObject(Source.fromResource("thermostat.json")!!.readText())
    tdd.register(parser.parseRDF(jsonThing)!!)
    tdd.register(parser.parseRDF(jsonThing2)!!)

   // tdd.querySparql(Test.sparqlExample1).let { ResultSetFactory.fromJSON(it.toString().byteInputStream()) }.let { ResultSetFormatter.out(it) }
   // tdd.querySparql(Test.sparqlExample2).let { ResultSetFactory.fromJSON(it.toString().byteInputStream()) }.let { ResultSetFormatter.out(it) }
    //tdd.querySparql(Test.sparqlExample3).let { ResultSetFactory.fromJSON(it.toString().byteInputStream()) }.let { ResultSetFormatter.out(it) }

    /*val things = tdd.searchThing("?thing td:hasInteractionAffordance ?pf. ?pf sosa:observes home:Ambient_Temperature.", 10)
    things.forEach {
        println(parser.fromRDF(it))
    }*/
}