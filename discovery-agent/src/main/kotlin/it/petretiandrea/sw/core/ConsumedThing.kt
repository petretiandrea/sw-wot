package it.petretiandrea.sw.core

import it.petretiandrea.sw.core.impl.ConsumedThingImpl
import it.petretiandrea.sw.core.ontology.Hypermedia
import it.petretiandrea.sw.core.ontology.WoT
import it.petretiandrea.sw.core.utils.JenaUtils
import org.apache.jena.rdf.model.Model

// could be a more complex structure that know the real concrete type
typealias Value = String

interface ConsumedThing {
    suspend fun readProperty(propertyName: String) : Value?
    suspend fun readAllProperties(vararg propertyNames: String): List<Value>
}

// https://www.w3.org/2019/wot/hypermedia#form
data class Form(val href: String, val operationType: String)

object ConsumedThingFactory {

    fun fromDescriptionModel(thingDescriptionRDF: ThingDescriptionRDF) : ConsumedThing {
        val properties = thingDescriptionRDF.model.listObjectsOfProperty(WoT.hasPropertyAffordance())
            .mapWith { it.asResource() }
            .toSet()
            .fold(emptyMap<String, Form>(), { acc, resource ->
                val name = thingDescriptionRDF.model.listObjectsOfProperty(resource, WoT.name()).next().asLiteral().string
                val form = FormFactory.formModel(JenaUtils.extractSubGraph(resource.asNode(), thingDescriptionRDF.model))
                if(form != null) acc + (name to form) else acc
            })


        return ConsumedThingImpl(properties)
    }
    //fun fromDescriptionJson(thingDescription: JSONObject)
}

object FormFactory {
    fun formModel(form: Model?): Form? = form?.let {
        val hrefNode = form.listObjectsOfProperty(Hypermedia.hasTarget()).next()
        val operationNode = form.listObjectsOfProperty(Hypermedia.hasOperationType()).nextOptional()
        Form(
            if(hrefNode.isLiteral) hrefNode.asLiteral().string else hrefNode.toString(),
            operationNode.map { it.asLiteral().string }.orElse("")
        )
    }
}