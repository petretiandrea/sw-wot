package it.petretiandrea.sw.jena.extension

import org.apache.jena.query.Dataset
import org.apache.jena.query.ReadWrite


fun <T> Dataset.doTransaction(operation: ReadWrite, map: (Dataset) -> T) : T? {
    try {
        this.begin(operation)
        val mapped = map(this)
        this.commit()
        return mapped
    } finally {
        this.end()
    }
}

fun Dataset.doWriteTransaction(consumer: (Dataset) -> Unit) = doTransaction(ReadWrite.WRITE, consumer)
fun <T> Dataset.doReadTransaction(map: (Dataset) -> T): T? = doTransaction(ReadWrite.READ, map)