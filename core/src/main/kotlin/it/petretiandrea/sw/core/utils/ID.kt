package it.petretiandrea.sw.core.utils

import java.util.*

object ID {
    fun generateUnique() : String = UUID.randomUUID().toString()
}