package it.petretiandrea.sw.parsing

import it.petretiandrea.sw.ontology.Namespaces
import it.petretiandrea.sw.parsing.jsonld.JSONLDParser
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.json.JSONObject

class TDParserImpl(private val validator: TDValidator,
                   private val lifter: TDLifter,
                   private val jsonLdParser: JSONLDParser) : TDParser {

    companion object {
        const val TARGET_TYPE: String = "application/n-quads"
    }

    override fun parseRDF(thingDescription: JSONObject): Model? {
        return when(validator.validate(thingDescription)) {
            true -> {
                val model = ModelFactory.createDefaultModel()
                val lifted = lifter.lift(thingDescription)
                val nQuads = jsonLdParser.toNQuads(lifted, Namespaces.HOME_SEMANTIC)
                model.read(nQuads.reader(), Namespaces.HOME_SEMANTIC.toString(), TARGET_TYPE)
            }
            else -> null
        }
    }
}