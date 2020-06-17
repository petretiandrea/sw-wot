package it.petretiandrea.sw.core

fun <T> T?.getOrElse(default: T) : T {
    return this ?: default
}