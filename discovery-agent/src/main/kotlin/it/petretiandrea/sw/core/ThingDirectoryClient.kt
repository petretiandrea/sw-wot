package it.petretiandrea.sw.core

import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.HttpRequest
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.ext.web.client.sendBufferAwait
import org.json.JSONObject

interface ThingDirectoryClient {
    suspend fun querySparql(query: String) : JSONObject

    companion object {
        operator fun invoke(host: String, port: Int): ThingDirectoryClient = ThingDirectoryClientImpl(host, port)
    }
}

suspend fun HttpRequest<Buffer>.sendTextAwait(text: String): HttpResponse<Buffer> {
    val buffer = Buffer.buffer(text)
    return this.sendBufferAwait(buffer)
}

class ThingDirectoryClientImpl(val host: String, val port: Int) : ThingDirectoryClient {

    companion object {
        private const val SPARQL_ENDPOINT = ""
    }

    private val vertx = Vertx.vertx()
    private val web = WebClient.create(vertx)

    override suspend fun querySparql(query: String) : JSONObject {
        return JSONObject(web.post(port, host, SPARQL_ENDPOINT).sendTextAwait(query).bodyAsString())
    }

}