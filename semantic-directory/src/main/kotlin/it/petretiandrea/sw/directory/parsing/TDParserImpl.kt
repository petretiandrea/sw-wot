package it.petretiandrea.sw.directory.parsing

import it.petretiandrea.sw.core.getAsString
import it.petretiandrea.sw.core.getOrElse
import it.petretiandrea.sw.core.ontology.Namespaces
import it.petretiandrea.sw.core.parsing.TDValidator
import it.petretiandrea.sw.core.utils.ID
import it.petretiandrea.sw.core.utils.IRIUtils
import it.petretiandrea.sw.core.ThingDescriptionRDF
import it.petretiandrea.sw.directory.parsing.jsonld.JSONLDParser
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.riot.RDFDataMgr
import org.apache.jena.riot.RDFFormat
import org.json.JSONObject
import java.io.StringWriter

class TDParserImpl(private val validator: TDValidator,
                   private val lifter: TDLifter,
                   private val jsonLdParser: JSONLDParser
) : TDParser {

    companion object {
        const val TARGET_TYPE: String = "application/n-quads"
    }

    override fun parseRDF(thingDescription: JSONObject): ThingDescriptionRDF? {
        return when(validator.validate(thingDescription)) {
            true -> {
                val model = ModelFactory.createDefaultModel()
                val lifted = lifter.lift(thingDescription)
                val id = IRIUtils.fromString(lifted.getString("id"))
                val nQuads = jsonLdParser.toNQuads(lifted, Namespaces.HOME_SEMANTIC)
                model.read(nQuads.reader(), Namespaces.HOME_SEMANTIC.toString(), TARGET_TYPE)
                ThingDescriptionRDF(id, model)
            }
            else -> null
        }
    }

    override fun fromRDF(thingDescriptionRDF: ThingDescriptionRDF): JSONObject? {
        val stringWriter = StringWriter()
        RDFDataMgr.write(stringWriter, thingDescriptionRDF.model, RDFFormat.JSONLD_PRETTY)
        stringWriter.close()
        return JSONObject(stringWriter.toString()).apply {
            put("id", getAsString("id").getOrElse(thingDescriptionRDF.resourceIdentifier.toString()))
        }
    }
}