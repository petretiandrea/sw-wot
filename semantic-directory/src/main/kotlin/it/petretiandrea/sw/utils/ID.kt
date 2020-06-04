package it.petretiandrea.sw.utils

import java.util.*

object ID {
    fun generateUnique() : String = UUID.randomUUID().toString()
}