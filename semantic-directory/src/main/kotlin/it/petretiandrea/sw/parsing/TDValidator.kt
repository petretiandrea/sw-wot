package it.petretiandrea.sw.parsing


import org.everit.json.schema.ValidationException
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import java.io.File
import java.net.URI

interface TDValidator {
    fun validate(td: JSONObject): Boolean

    companion object {
        private class TDValidatorImpl(tdSchema: JSONObject) : TDValidator {
            private val schema = SchemaLoader.load(tdSchema)
            override fun validate(td: JSONObject): Boolean {
                return try {
                    schema.validate(td); true
                } catch (e: ValidationException) {
                    e.allMessages.forEach { println(it) }
                    false
                }
            }
        }
        operator fun invoke(tdSchema: JSONObject): TDValidator = TDValidatorImpl(tdSchema)
        operator fun invoke(tdSchemaPath: URI): TDValidator = TDValidatorImpl(JSONObject(File(tdSchemaPath).readText()))
    }
}

