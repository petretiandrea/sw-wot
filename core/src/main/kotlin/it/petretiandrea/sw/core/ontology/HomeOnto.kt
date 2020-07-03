package it.petretiandrea.sw.core.ontology

import it.petretiandrea.sw.core.utils.IRIUtils
import it.petretiandrea.sw.core.utils.Source
import openllet.jena.PelletReasonerFactory
import org.apache.jena.assembler.Mode
import org.apache.jena.rdf.model.*
import org.apache.jena.reasoner.Reasoner
import java.io.File
import java.io.Reader

sealed class DeviceType(val name: String)  {
    object Thermostat : DeviceType("Thermostat")
    object Thermometer : DeviceType("Thermometer")
}

sealed class LocationType(name: String) : FeatureProperty(name) {
    object GPS : LocationType("GPSLocation")
}

sealed class FeatureProperty(val name: String) {
    object Temperature : FeatureProperty("Temperature")
    object AmbientHumidity : FeatureProperty("AmbientHumidity")
    object AmbientTemperature : FeatureProperty("AmbientTemperature")
}

object HomeOnto {
    val NAMESPACE = IRIUtils.fromString("http://www.sw.org/andreapetreti/home-wot#")
    //private val HOME_ONTO_PATH = Source.fromResource("ontologies/home-wot.owl")?.path
    //private val WOT_NORM_PATH = Source.fromResource("ontologies/wot-normalized.owl")?.path
    private val HOME_ONTO_PATH =
        "core/src/main/resources/ontologies/home-wot.owl"
    private val WOT_NORM_PATH =
        "core/src/main/resources/ontologies/wot-normalized.owl"

    private var cachedModel : Model? = null

    private fun cacheModel(modelFunction: () -> Model): Model {
        if(cachedModel == null)
            cachedModel = modelFunction()
        return cachedModel!!
    }

    fun getModel(): Model {
        val homeOnto = loadModel(File(HOME_ONTO_PATH!!).reader())
        val wotOnto = loadModel(File(WOT_NORM_PATH!!).reader())
        return cacheModel { ModelFactory.createDefaultModel().apply {
            //read(wotOntologyReader(), Namespaces.WOT.toString(), "TURTLE")
            read(Namespaces.SSN.toString())
            read(Namespaces.SOSA.toString())
            add(wotOnto)
            add(homeOnto)
            setNsPrefix("td", Namespaces.WOT.toString())
        } }
    }

    fun getReasoner(): Reasoner {
        return PelletReasonerFactory.theInstance().create().bindFixedSchema(getModel())
    }

    private fun loadModel(reader: Reader) : Model {
        return ModelFactory.createDefaultModel().apply {
            read(reader, NAMESPACE.toString(), "TURTLE")
        }
    }
}