package it.petretiandrea.sw.directory.parsing


import it.petretiandrea.sw.core.getArrayOfJSONObject
import it.petretiandrea.sw.core.getAsJSONArray
import it.petretiandrea.sw.core.getAsString
import it.petretiandrea.sw.core.getOrElse
import it.petretiandrea.sw.core.ontology.Namespaces
import it.petretiandrea.sw.core.utils.ID
import it.petretiandrea.sw.core.utils.IRIUtils
import org.json.JSONObject

class TDLifter {

    fun lift(thingDescription: JSONObject) : JSONObject {
        if(thingDescription.getAsString("id").isNullOrBlank()) thingDescription.put("id", IRIUtils.createUrnUuid().toString())
        liftProperties(thingDescription)
        liftActions(thingDescription)
        liftEvents(thingDescription)
        return thingDescription
    }

    private fun liftProperties(thingDescription: JSONObject) {
        thingDescription.getArrayOfJSONObject("properties").forEach {
            // FOR TEST PURPOSE USE "b5d2b153-665a-4bd9-a3b9-627292af208f"
            it.value.put("@id", Namespaces.HOME_SEMANTIC.toString() + ID.generateUnique())//
            it.value.put("name", it.key)
        }
    }

    private fun liftActions(thingDescription: JSONObject) {
        thingDescription.getArrayOfJSONObject("actions").forEach {
            it.value.put("@id", Namespaces.HOME_SEMANTIC.toString() + ID.generateUnique())
            it.value.put("name", it.key)
        }
    }

    private fun liftEvents(thingDescription: JSONObject) {
        thingDescription.getArrayOfJSONObject("events").forEach {
            it.value.put("@id", Namespaces.HOME_SEMANTIC.toString() + ID.generateUnique())
            it.value.put("name", it.key)
        }
    }
}