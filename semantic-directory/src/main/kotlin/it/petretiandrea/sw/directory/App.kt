package it.petretiandrea.sw.directory

import io.vertx.core.Vertx
import it.petretiandrea.sw.core.utils.Source
import it.petretiandrea.sw.directory.core.ThingDescriptionDirectory
import it.petretiandrea.sw.directory.parsing.TDParser
import it.petretiandrea.sw.directory.parsing.jsonld.JSONLDParserFactory
import it.petretiandrea.sw.directory.restapi.RestApiSemanticDiscovery
import org.json.JSONObject

fun testPopulate(tdParser: TDParser, tdd: ThingDescriptionDirectory) {
    val td = tdParser.parseRDF(JSONObject(Source.fromResource("customthing.json")!!.readText()))!!
    tdd.register(td)
}

fun main() {
    val config = JSONObject(Source.readFromResource("directory-config.json"))
    val jsonLdParser = JSONLDParserFactory.fromJson(config.getJSONObject("jsonld"))!!

    val parser = TDParser(jsonLdParser = jsonLdParser)
    val tdd = ThingDescriptionDirectory()
    val restVerticle = RestApiSemanticDiscovery(
        tdParser = parser,
        thingDescriptionDirectory = tdd,
        host = config.getString("host"),
        port = config.getInt("port"))

    val vertx = Vertx.vertx()

    vertx.deployVerticle(restVerticle)

    testPopulate(parser, tdd)
}