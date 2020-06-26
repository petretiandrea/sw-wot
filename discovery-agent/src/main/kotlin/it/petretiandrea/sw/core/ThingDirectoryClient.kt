package it.petretiandrea.sw.core

import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.HttpRequest
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.ext.web.client.sendBufferAwait
import org.apache.jena.query.QuerySolution
import org.apache.jena.query.ResultSet
import org.apache.jena.query.ResultSetFactory
import org.json.JSONArray
import org.json.JSONObject

data class SparqlResult(val resultSet: ResultSet) : Iterable<QuerySolution> {
    fun asModel() = resultSet.resourceModel
    override fun iterator(): Iterator<QuerySolution> = resultSet.iterator()
}

interface ThingDirectoryClient {
    suspend fun querySparql(query: String) : SparqlResult

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
        private const val SPARQL_ENDPOINT = "/sparql"
    }

    private val vertx = Vertx.vertx()
    private val web = WebClient.create(vertx)

    override suspend fun querySparql(query: String) : SparqlResult {
        return toSparqlResult(
            JSONObject(web.post(port, host, SPARQL_ENDPOINT).sendTextAwait(query).bodyAsString())
        )
    }

    private fun toSparqlResult(jsonResponse: JSONObject) : SparqlResult = SparqlResult(ResultSetFactory.fromJSON(jsonResponse.toString().byteInputStream()))
}
fun JSONObject.getAsJSONObject(key: String) = getNullableFromJson<JSONObject>(this, key)
fun JSONObject.getAsJSONArray(key: String) = getNullableFromJson<JSONArray>(this, key)
fun JSONArray.asJSONObjectArray() = this.map { JSONObject(it) }

fun <T>getNullableFromJson(jsonObject: JSONObject, key: String): T? {
    try {
        return jsonObject.get(key) as T
    } catch (e: Exception) {
        return null
    }
}
