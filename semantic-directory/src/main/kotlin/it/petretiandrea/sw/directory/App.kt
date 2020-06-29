package it.petretiandrea.sw.directory

import io.vertx.core.Vertx
import it.petretiandrea.sw.core.utils.Source
import it.petretiandrea.sw.directory.core.ThingDescriptionDirectory
import it.petretiandrea.sw.directory.parsing.TDParser
import it.petretiandrea.sw.directory.restapi.RestApiSemanticDiscovery
import org.json.JSONObject

fun testPopulate(tdParser: TDParser, tdd: ThingDescriptionDirectory) {
    val td = tdParser.parseRDF(JSONObject(Source.fromResource("customthing.json")!!.readText()))!!
    tdd.register(td)
}

fun main() {
    val vertx = Vertx.vertx()

    val parser = TDParser()
    val tdd = ThingDescriptionDirectory()
    val restVerticle = RestApiSemanticDiscovery(parser, tdd)

    vertx.deployVerticle(restVerticle)

    testPopulate(parser, tdd)
}