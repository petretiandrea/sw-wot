package it.petretiandrea.sw.directory.core

import it.petretiandrea.sw.core.ontology.WoT
import it.petretiandrea.sw.core.utils.ID
import it.petretiandrea.sw.core.utils.IRIUtils
import org.apache.jena.iri.IRI
import org.apache.jena.rdf.model.Model
import org.apache.jena.vocabulary.RDF

interface ThingDescriptionRDF {
    val resourceIdentifier: IRI
    val model: Model

    companion object {
        private data class ThingDescriptionRDFImpl(override val resourceIdentifier: IRI, override val model: Model) : ThingDescriptionRDF
        operator fun invoke(resourceIdentifier: IRI, model: Model): ThingDescriptionRDF = ThingDescriptionRDFImpl(resourceIdentifier, model)
    }
}