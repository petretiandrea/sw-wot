package it.petretiandrea.sw.directory.parsing.jsonld

import it.petretiandrea.sw.core.getAsString
import org.apache.jena.iri.IRI
import org.json.JSONObject
import java.io.PrintWriter

/**
 * JSONLD parser that convert JSON-LD 1.1 to N-QUADS representation
 */
interface JSONLDParser {
    fun toNQuads(td: JSONObject, baseIRI: IRI) : String
}

object JSONLDParserFactory {
    fun fromJson(json: JSONObject): JSONLDParser? = when(json.get("type")) {
        "ruby" -> {
            json.getAsString("ruby_exec")?.let { exec ->
                json.getAsString("script")?.let { script ->
                    RubyJSONLDParser(exec, script)
                }
            }
        }
        else -> null
    }
    fun ruby() = RubyJSONLDParser("ruby", System.getProperty("ruby.parser"))
}

class RubyJSONLDParser(val rubyPath: String, val rubyScript: String) : JSONLDParser {

    override fun toNQuads(td: JSONObject, baseIRI: IRI) : String {
        return convertToNQuads(td.toString(), baseIRI.toString())
    }

    private fun convertToNQuads(td: String, baseIri: String) : String {
        val tdNormalized = td.replace("\n", "")
        val process = ProcessBuilder(rubyPath, rubyScript, "\"${baseIri}\"").start()
        PrintWriter(process.outputStream).let {
            it.write(tdNormalized)
            it.flush()
            it.close()
        }
        return process.inputStream.bufferedReader().readText()
    }
}