package it.petretiandrea.sw

fun <T> T?.getOrElse(default: T) : T {
    return this ?: default
}