package it.petretiandrea.sw.restapi

import io.vertx.core.Vertx
import it.petretiandrea.sw.Main
import it.petretiandrea.sw.core.ThingDescriptionDirectory
import it.petretiandrea.sw.core.ThingDescriptionRDF
import it.petretiandrea.sw.parsing.TDParser
import it.petretiandrea.sw.utils.IRIUtils
import org.json.JSONObject

fun testPopulate(tdParser: TDParser, tdd: ThingDescriptionDirectory) {
    val model = tdParser.parseRDF(JSONObject(Main.readFromFile("customthing.json")))!!
    val td = ThingDescriptionRDF(IRIUtils.createUrnUuid(), model)
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