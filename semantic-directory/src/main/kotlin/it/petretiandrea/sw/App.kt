package it.petretiandrea.sw

import it.petretiandrea.sw.core.ThingDescriptionDirectory
import it.petretiandrea.sw.core.ThingDescriptionRDF
import it.petretiandrea.sw.parsing.TDParser
import it.petretiandrea.sw.utils.IRIUtils
import org.apache.jena.rdf.model.*
import org.apache.jena.util.PrintUtil
import org.json.JSONObject

fun main() {
    val tdd = ThingDescriptionDirectory()

    val parser = TDParser()
    val model = parser.parseRDF(JSONObject(Main.readFromFile("richthing.json")))!!

    val td = ThingDescriptionRDF(IRIUtils.createUrnUuid(), model)
    tdd.register(td)

    val thing = tdd.get(td.resourceIdentifier)

}

object Main {

    fun printStatements(m: Model, s: Resource?, p: Property?, o: Resource?) {
        val i: StmtIterator = m.listStatements(s, p, o)
        while (i.hasNext()) {
            val stmt: Statement = i.nextStatement()
            println(" - " + PrintUtil.print(stmt))
        }
    }

    fun readFromFile(filename: String) : String = Main::class.java.classLoader.getResource(filename).readText()
}