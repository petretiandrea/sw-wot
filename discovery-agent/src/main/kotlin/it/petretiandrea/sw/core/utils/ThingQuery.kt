package it.petretiandrea.sw.core.utils

import it.petretiandrea.sw.core.ontology.DeviceType
import it.petretiandrea.sw.core.ontology.FeatureProperty

interface ThingQuery {
    val deviceType: DeviceType?
    val observeProperties: List<FeatureProperty>
    val actsProperties: List<FeatureProperty>

    companion object {
        data class ThingQueryImpl(override val deviceType: DeviceType?,
                                  override val observeProperties: List<FeatureProperty>,
                                  override val actsProperties: List<FeatureProperty>) : ThingQuery
        operator fun invoke(deviceType: DeviceType?, observeProperties: List<FeatureProperty>, actsProperties: List<FeatureProperty>): ThingQuery {
            return ThingQueryImpl(deviceType, observeProperties, actsProperties)
        }
    }
}

interface ThingCollectQuery {
    val filter: ThingQuery?
    val collectOn: FeatureProperty

    companion object {
        operator fun invoke(collectOn: FeatureProperty, filter: ThingQuery? = null): ThingCollectQuery {
            return object : ThingCollectQuery {
                override val filter: ThingQuery? = filter
                override val collectOn: FeatureProperty = collectOn
            }
        }
    }
}

fun thingQuery(builder: ThingQueryBuilder.() -> Unit): ThingQuery {
    return ThingQueryBuilder().apply(builder).build()
}

fun thingCollectQuery(builder: ThingCollectQueryBuilder.() -> Unit) : ThingCollectQuery {
    return ThingCollectQueryBuilder().apply(builder).build()
}

class ThingQueryBuilder {

    private var thingQuery = ThingQuery(null, emptyList(), emptyList())
    private var collectOn: FeatureProperty? = null

    fun deviceType(block: () -> DeviceType) {
        thingQuery = ThingQuery(block(), thingQuery.observeProperties, thingQuery.actsProperties)
    }

    fun canSense(block: FeatureProperties.() -> Unit) {
        val observes = FeatureProperties().apply(block)
        thingQuery = ThingQuery(thingQuery.deviceType, observes, thingQuery.actsProperties)
    }

    fun canActOn(block: FeatureProperties.() -> Unit) {
        val acts = FeatureProperties().apply(block)
        thingQuery = ThingQuery(thingQuery.deviceType, thingQuery.observeProperties, acts)
    }

    fun collectOn(block: () -> FeatureProperty) {
        this.collectOn = block()
    }

    fun build(): ThingQuery {
        return thingQuery
    }

    class FeatureProperties: ArrayList<FeatureProperty>() {
        fun feature(builder: () -> FeatureProperty) {
            add(builder())
        }
    }
}
class ThingCollectQueryBuilder {
    private var collectOn: FeatureProperty? = null
    private var filter: ThingQuery? = null

    fun filter(block: ThingQueryBuilder.() -> Unit) {
        this.filter = thingQuery(block)
    }

    fun collectOn(block: () -> FeatureProperty) {
        this.collectOn = block()
    }

    fun build(): ThingCollectQuery {
        return ThingCollectQuery(this.collectOn!!, this.filter)
    }
}
