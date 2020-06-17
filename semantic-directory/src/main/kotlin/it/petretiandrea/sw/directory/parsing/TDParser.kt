package it.petretiandrea.sw.directory.parsing

import it.petretiandrea.sw.core.parsing.TDValidator
import it.petretiandrea.sw.directory.parsing.jsonld.JSONLDParser
import it.petretiandrea.sw.directory.parsing.jsonld.JSONLDParserFactory
import org.apache.jena.rdf.model.Model
import org.json.JSONObject

interface TDParser {
    fun parseRDF(thingDescription: JSONObject): Model?

    companion object {
        operator fun invoke(validator: TDValidator = TDValidator(),
                            lifter: TDLifter = TDLifter(),
                            jsonLdParser: JSONLDParser = JSONLDParserFactory.ruby()) = TDParserImpl(validator, lifter, jsonLdParser)
    }
}