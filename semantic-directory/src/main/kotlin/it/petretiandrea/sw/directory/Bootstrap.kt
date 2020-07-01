package it.petretiandrea.sw.directory


import io.vertx.core.Vertx
import io.vertx.kotlin.core.deployVerticleAwait
import it.petretiandrea.sw.core.utils.Source
import it.petretiandrea.sw.directory.core.ThingDescriptionDirectory
import it.petretiandrea.sw.directory.parsing.TDParser
import it.petretiandrea.sw.directory.parsing.jsonld.JSONLDParserFactory
import it.petretiandrea.sw.directory.restapi.RestApiSemanticDiscovery
import org.json.JSONObject

object Bootstrap {

    suspend fun bootSemanticDirectory(vararg things: JSONObject) {
        val config = JSONObject(Source.readFromResource("directory-config.json"))
        val jsonLdParser = JSONLDParserFactory.fromJson(config.getJSONObject("jsonld"))!!

        val parser = TDParser(jsonLdParser = jsonLdParser)
        val tdd = ThingDescriptionDirectory()

        things.mapNotNull { parser.parseRDF(it) }.forEach { tdd.register(it) }

        val restVerticle = RestApiSemanticDiscovery(
            tdParser = parser,
            thingDescriptionDirectory = tdd,
            host = config.getString("host"),
            port = config.getInt("port"))

        val vertx = Vertx.vertx()
        vertx.deployVerticleAwait(restVerticle)
    }
}