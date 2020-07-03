package it.petretiandrea.sw.directory

import io.vertx.core.Vertx
import it.petretiandrea.sw.core.utils.Source
import it.petretiandrea.sw.directory.core.ThingDescriptionDirectory
import it.petretiandrea.sw.directory.parsing.TDParser
import it.petretiandrea.sw.directory.parsing.jsonld.JSONLDParserFactory
import it.petretiandrea.sw.directory.restapi.RestApiSemanticDiscovery
import org.json.JSONObject
import java.io.File

fun populateFromFolder(path: String, tdParser: TDParser, tdd: ThingDescriptionDirectory) {
    val files = File(path).listFiles()
    files?.mapNotNull { tdParser.parseRDF(JSONObject(it.readText())) }
        ?.forEach { tdd.register(it) }
}

fun main() {
    val config = JSONObject(Source.readFromResource("directory-config.json"))
    val jsonLdParser = JSONLDParserFactory.fromJson(config.getJSONObject("jsonld"))!!

    val parser = TDParser(jsonLdParser = jsonLdParser)
    val tdd = ThingDescriptionDirectory()
    populateFromFolder(config.getString("thing_folder"), parser, tdd)

    val restVerticle = RestApiSemanticDiscovery(
        tdParser = parser,
        thingDescriptionDirectory = tdd,
        host = config.getString("host"),
        port = config.getInt("port"))

    val vertx = Vertx.vertx()
    vertx.deployVerticle(restVerticle)
}