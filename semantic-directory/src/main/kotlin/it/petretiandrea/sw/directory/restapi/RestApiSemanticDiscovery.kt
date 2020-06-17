package it.petretiandrea.sw.directory.restapi

import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import it.petretiandrea.sw.directory.core.ThingDescriptionDirectory
import it.petretiandrea.sw.directory.core.ThingDescriptionRDF
import it.petretiandrea.sw.directory.parsing.TDParser
import it.petretiandrea.sw.core.getOrElse
import org.json.JSONObject

fun JSONObject.toVertxJson() = JsonObject(this.toString())
fun JsonObject.toJSON() = JSONObject(this.toString())

class RestApiSemanticDiscovery(private val TDParser: TDParser,
                               private val thingDescriptionDirectory: ThingDescriptionDirectory,
                               port: Int = 8080,
                               host: String = "localhost") : RestApiVerticle(port, host) {

    companion object {
        private const val REGISTER_THING_ENDPOINT = "/td"
        private const val READ_THING_ENDPOINT = "/td/:id"
        private const val SPARQL_ENDPOINT = "/sparql"
    }


    override fun createRouter(): Router {
        val router = Router.router(vertx)

        router.post(REGISTER_THING_ENDPOINT)
            .handler(BodyHandler.create())
            .handler { handleRegistration(it) }

        router.get(READ_THING_ENDPOINT)
            .handler { handleGetThing(it) }

        router.post(SPARQL_ENDPOINT)
            .handler(BodyHandler.create())
            .handler { handleSparql(it) }

        return router
    }

    private fun handleSparql(context: RoutingContext) {
        val query = context.bodyAsString
        val response = query?.let { thingDescriptionDirectory.querySparql(it) }

        context.response().setStatusCode(200).end(response?.toVertxJson()?.encode().getOrElse(""))
    }

    private fun handleRegistration(context: RoutingContext) {
        val thingJson: JsonObject? = context.bodyAsJson
        val rdf = thingJson?.let { TDParser.parseRDF(it.toJSON()) }

        if (rdf != null) {
            thingDescriptionDirectory.register(ThingDescriptionRDF.fromRDF(rdf))
            context.response().setStatusCode(201).end()
        } else
            context.response().setStatusCode(400).end()
    }


    private fun handleGetThing(it: RoutingContext) {
        // TODO: implement()
    }
}