package it.petretiandrea.sw.core.utils

import java.net.URL

object Source {
    fun fromResource(fileName: String): URL? = javaClass.classLoader.getResource(fileName)
    fun readFromResource(fileName: String): String? = fromResource(fileName)?.readText()
}