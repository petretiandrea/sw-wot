package it.petretiandrea.sw.parsing

import it.petretiandrea.sw.Namespaces
import it.petretiandrea.sw.utils.ID
import org.json.JSONObject

class TDLifter {

    fun lift(thingDescription: JSONObject) : JSONObject {
        liftProperties(thingDescription)
        liftActions(thingDescription)
        liftEvents(thingDescription)
        return thingDescription
    }

    private fun liftProperties(thingDescription: JSONObject) {
        thingDescription.getArrayOfJSONObject("properties").forEach {
            it.value.put("@id", Namespaces.SEMANTIC_DISCOVERY.toString() + "b5d2b153-665a-4bd9-a3b9-627292af208f")//ID.generateUnique())
            it.value.put("name", it.key)
        }
    }

    private fun liftActions(thingDescription: JSONObject) {
        thingDescription.getArrayOfJSONObject("actions").forEach {
            it.value.put("@id", Namespaces.SEMANTIC_DISCOVERY.toString() + ID.generateUnique())
            it.value.put("name", it.key)
        }
    }

    private fun liftEvents(thingDescription: JSONObject) {
        thingDescription.getArrayOfJSONObject("events").forEach {
            it.value.put("@id", Namespaces.SEMANTIC_DISCOVERY.toString() + ID.generateUnique())
            it.value.put("name", it.key)
        }
    }

    fun JSONObject.getArrayOfJSONObject(key: String) =
        this.getJSONObject(key).let { obj -> obj.keySet().map { Pair(it, obj.getJSONObject(it)) }.toMap() }
}