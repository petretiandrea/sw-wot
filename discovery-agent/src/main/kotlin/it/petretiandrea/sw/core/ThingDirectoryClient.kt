package it.petretiandrea.sw.core

import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.HttpRequest
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.ext.web.client.sendAwait
import io.vertx.kotlin.ext.web.client.sendBufferAwait
import it.petretiandrea.sw.core.utils.IRIUtils
import org.apache.jena.assembler.Mode
import org.apache.jena.query.QuerySolution
import org.apache.jena.query.ResultSet
import org.apache.jena.query.ResultSetFactory
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.riot.Lang
import org.apache.jena.riot.RDFDataMgr
import org.json.JSONArray
import org.json.JSONObject


interface ThingDirectoryClient {

    suspend fun searchThings(graphPatternQuery: String?, limit: Int? = null): List<ThingDescriptionRDF>
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
        private const val THINGS_ENDPOINT = "/td"
        private const val SPARQL_ENDPOINT = "/sparql"
    }

    private val vertx = Vertx.vertx()
    private val web = WebClient.create(vertx)

    // TODO: use TD parser instead create the model manually
    override suspend fun searchThings(graphPatternQuery: String?, limit: Int?): List<ThingDescriptionRDF> {
        val queries = buildQueryParams("query" to graphPatternQuery, "limit" to limit)
        val response = web.get(port, host, THINGS_ENDPOINT).apply { queryParams().addAll(queries) }.sendAwait().bodyAsString()
        return JSONArray(response).asJSONObjectArray().map {
            val iri = IRIUtils.fromString(it.getString("id"))
            val model = ModelFactory.createDefaultModel().read(it.toString().reader(), "", "application/ld+json")
            ThingDescriptionRDF(iri, model)
        }
    }

    override suspend fun querySparql(query: String) : SparqlResult {
        return toSparqlResult(
            JSONObject(web.post(port, host, SPARQL_ENDPOINT).sendTextAwait(query).bodyAsString())
        )
    }

    private fun toSparqlResult(jsonResponse: JSONObject) : SparqlResult = SparqlResult(ResultSetFactory.fromJSON(jsonResponse.toString().byteInputStream()))

    private fun buildQueryParams(vararg queries: Pair<String, Any?>): Map<String, String> {
        return queries.filter { it.second != null }
            .foldRight(emptyMap(), { pair, map -> map + (pair.first to pair.second.toString()) })
    }
}
