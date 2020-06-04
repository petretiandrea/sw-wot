package it.petretiandrea.sw.parsing

import it.petretiandrea.sw.parsing.jsonld.JSONLDParser
import it.petretiandrea.sw.parsing.jsonld.JSONLDParserFactory
import org.apache.jena.rdf.model.Model
import org.json.JSONObject

interface TDParser {
    fun parseRDF(thingDescription: JSONObject): Model?

    companion object {
        operator fun invoke(validator: TDValidator = TDValidator(TDParser::class.java.classLoader.getResource("validation/wot-schema.json").toURI()),
                            lifter: TDLifter = TDLifter(),
                            jsonLdParser: JSONLDParser = JSONLDParserFactory.ruby()) = TDParserImpl(validator, lifter, jsonLdParser)
    }
}