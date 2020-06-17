package it.petretiandrea.sw.core

/*
- at high level we can search thing by tipology
- we can search thing by featureproperty
- collect data by thing by featureproperty
 */

object HomeOnto {

    sealed class DeviceType(val typeName: String) {
        object Thermostat : DeviceType("thermostat")
        object Thermometer : DeviceType("thermometer")
    }


    fun searchThingByTypology(deviceType: HomeOnto.DeviceType) {

    }
}


fun main() {
    
}