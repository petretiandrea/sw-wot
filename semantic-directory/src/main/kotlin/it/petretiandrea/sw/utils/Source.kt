package it.petretiandrea.sw.utils

import java.net.URL

object Source {
    fun fromResource(fileName: String): URL? = javaClass.classLoader.getResource(fileName)
}