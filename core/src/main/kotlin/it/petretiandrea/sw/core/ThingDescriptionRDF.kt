package it.petretiandrea.sw.core

import org.apache.jena.iri.IRI
import org.apache.jena.rdf.model.Model

interface ThingDescriptionRDF {
    val resourceIdentifier: IRI
    val model: Model

    companion object {
        private data class ThingDescriptionRDFImpl(override val resourceIdentifier: IRI, override val model: Model) :
            ThingDescriptionRDF
        operator fun invoke(resourceIdentifier: IRI, model: Model): ThingDescriptionRDF =
            ThingDescriptionRDFImpl(
                resourceIdentifier,
                model
            )
    }
}