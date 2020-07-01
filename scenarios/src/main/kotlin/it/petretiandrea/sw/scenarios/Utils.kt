package it.petretiandrea.sw.scenarios

import it.petretiandrea.sw.directory.core.ThingDescriptionDirectory
import it.petretiandrea.sw.directory.parsing.TDParser
import org.json.JSONObject
import java.io.File

object Utils {
    fun loadThingsFromFolder(path: String): List<JSONObject> {
        val files = File(path).listFiles()
        return files?.mapNotNull { JSONObject(it.readText()) }.orEmpty()
    }
}