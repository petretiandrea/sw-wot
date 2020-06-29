package it.petretiandrea.sw.directory.restapi

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import it.petretiandrea.sw.directory.core.ThingDescriptionDirectory
import it.petretiandrea.sw.directory.parsing.TDParser
import it.petretiandrea.sw.core.getOrElse
import org.json.JSONArray
import org.json.JSONObject

fun JSONObject.toVertxJson() = JsonObject(this.toString())
fun JSONArray.toVertxJson() = JsonArray(this.toString())
fun JsonObject.toJSON() = JSONObject(this.toString())

class RestApiSemanticDiscovery(private val tdParser: TDParser,
                               private val thingDescriptionDirectory: ThingDescriptionDirectory,
                               port: Int = 8080,
                               host: String = "0.0.0.0") : RestApiVerticle(port, host) {

    companion object {
        private const val THING_ENDPOINT = "/td"
        private const val READ_THING_ENDPOINT = "/td/:id"
        private const val SPARQL_ENDPOINT = "/sparql"
    }


    override fun createRouter(): Router {
        val router = Router.router(vertx)

        router.get(THING_ENDPOINT)
            .handler { handlerGetThings(it) }

        router.post(THING_ENDPOINT)
            .handler(BodyHandler.create())
            .handler { handleRegistration(it) }

        router.get(READ_THING_ENDPOINT)
            .handler { handleGetThing(it) }

        router.post(SPARQL_ENDPOINT)
            .handler(BodyHandler.create())
            .handler { handleSparql(it) }

        return router
    }

    private fun handlerGetThings(context: RoutingContext) {
        val graphPatternQuery = context.queryParam("query").firstOrNull()
        val limit = context.queryParam("limit").firstOrNull().getOrElse("1").toInt()

        thingDescriptionDirectory.searchThing(graphPatternQuery, limit)
            .map { tdParser.fromRDF(it) }
            .fold(JSONArray(), { array, obj -> array.put(obj) })
            .let {
                context.response().setStatusCode(200).end(it.toVertxJson().encode())
            }
    }

    private fun handleSparql(context: RoutingContext) {
        val query = context.bodyAsString
        val response = query?.let { thingDescriptionDirectory.querySparql(it) }
        context.response().setStatusCode(200).end(response?.toVertxJson()?.encode().getOrElse(""))
    }

    private fun handleRegistration(context: RoutingContext) {
        val thingJson: JsonObject? = context.bodyAsJson
        val thingDescriptionRDF = thingJson?.let { tdParser.parseRDF(it.toJSON()) }

        if (thingDescriptionRDF != null) {
            thingDescriptionDirectory.register(thingDescriptionRDF)
            context.response().setStatusCode(201).end()
        } else
            context.response().setStatusCode(400).end()
    }


    private fun handleGetThing(it: RoutingContext) {
        // TODO: implement()
    }
}