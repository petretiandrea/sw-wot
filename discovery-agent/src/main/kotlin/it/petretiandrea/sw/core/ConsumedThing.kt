package it.petretiandrea.sw.core

import it.petretiandrea.sw.core.impl.ConsumedThingImpl
import it.petretiandrea.sw.core.ontology.Hypermedia
import it.petretiandrea.sw.core.ontology.JsonSchema
import it.petretiandrea.sw.core.ontology.WoT
import it.petretiandrea.sw.core.utils.JenaUtils
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.Resource
import org.apache.jena.vocabulary.RDF
import kotlin.reflect.KClass


data class Value(val value: String, private val dataSchema: DataSchema) {
    fun asDouble() : Double? = value.toDoubleOrNull()
    fun asString() : String = value
    fun asInteger() : Int? = value.toIntOrNull()
}


data class DataSchema(val type: String, val constValue: String, val format: String, val oneOf: List<String>)

interface ConsumedThing {
    val properties: Map<String, Property>
    suspend fun readProperty(propertyName: String) : Value?
    suspend fun readAllProperties(vararg propertyNames: String): List<Value>
}

// https://www.w3.org/2019/wot/hypermedia#form
data class Property(val schema: DataSchema, val forms: List<Form>)
data class Form(val href: String, val operationType: String)

object ConsumedThingFactory {

    fun fromDescriptionModel(thingDescriptionRDF: ThingDescriptionRDF) : ConsumedThing {
        return ConsumedThingImpl(
            ThingParser.propertiesFromRdf(thingDescriptionRDF.model)
        )
    }
    //fun fromDescriptionJson(thingDescription: JSONObject)
}

object ThingParser {
    fun propertiesFromRdf(model: Model): Map<String, Property> {
        val properties = model.listObjectsOfProperty(WoT.hasPropertyAffordance())
            .mapWith {
                Pair(model.listObjectsOfProperty(it.asResource(), WoT.name()).nextNode().asLiteral().string, it.asResource())
            }
            .toList()
            .toMap()

        return properties.mapValues {
            val propertySubGraph = JenaUtils.extractSubGraph(it.value.asNode(), model)
            Property(
                DataSchemaFactory.fromRdf(propertySubGraph)!!,
                FormFactory.fromRdf(propertySubGraph)
            )
        }
    }
}

object DataSchemaFactory {
    fun fromRdf(dataSchema: Model?): DataSchema? = dataSchema?.let {
        findJsonSchema(dataSchema)?.let {
            // TODO: parse others things
            DataSchema(it.uri.toString(), "", "", emptyList())
        }
    }

    private fun findJsonSchema(model: Model): Resource? {
        return model.listObjectsOfProperty(RDF.type).filterKeep {
            it.isResource && it.asResource().uri.contains(JsonSchema.NAMESPACE.toString())
        }.toList().firstOrNull()?.asResource()
    }
}
object FormFactory {
    fun fromRdf(form: Model?): List<Form> = form?.let {
        form.listObjectsOfProperty(Hypermedia.hasTarget()).mapWith {
            val opType = form.listObjectsOfProperty(it.asResource(), Hypermedia.hasOperationType()).nextOptional()
            Form(
                if(it.isLiteral) it.asLiteral().string else it.toString(),
                opType.map { it.asLiteral().string }.orElse("")
            )
        }.toList()
    }.orEmpty()
}