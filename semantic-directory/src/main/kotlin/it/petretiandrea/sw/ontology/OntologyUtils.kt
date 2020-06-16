package it.petretiandrea.sw.ontology

import org.apache.jena.rdf.model.Property
import org.apache.jena.rdf.model.Resource
import org.apache.jena.rdf.model.ResourceFactory

object OntologyUtils {
    fun createResource(namespace: String, resourceName: String) : Resource = ResourceFactory.createResource(concatNamespace(namespace, resourceName))
    fun createProperty(namespace: String, propertyName: String) : Property = ResourceFactory.createProperty(concatNamespace(namespace, propertyName))

    private fun concatNamespace(namespace: String, name: String) : String = namespace + name
}