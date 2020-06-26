package it.petretiandrea.sw.core

import org.apache.jena.atlas.json.JsonArray
import org.json.JSONArray
import org.json.JSONObject


fun <T>getNullableFromJson(jsonObject: JSONObject, key: String): T? {
    try {
        return jsonObject.get(key) as T
    } catch (e: Exception) {
        return null
    }
}

fun JSONObject.getAsJSONObject(key: String) = getNullableFromJson<JSONObject>(this, key)
fun JSONObject.getAsJSONArray(key: String) = getNullableFromJson<JSONArray>(this, key)
fun JSONObject.getAsString(key: String) = getNullableFromJson<String>(this, key)
fun JSONArray.asJSONObjectArray() = this.map { JSONObject(it) }
fun JSONObject.getArrayOfJSONObject(key: String) =
    this.getAsJSONObject(key)?.let { obj -> obj.keySet().map { Pair(it, obj.getJSONObject(it)) }.toMap() }