package org.openhab.io.semantic.dogont.internal.util;

import org.openhab.io.semantic.dogont.internal.ontology.DogontSchema;
import org.openhab.io.semantic.dogont.internal.ontology.VicciExtensionSchema;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.Model;

public class SchemaUtil {

    /**
     * Adds the NS prefix for all imports to the instance model
     *
     * @param model
     */
    public static void addRequiredNamespacePrefixToInstanceModel(Model model) {
        model.setNsPrefix("base", "http://openhab-semantic/0.1/instance");
        model.setNsPrefix("vicci", "http://openhab-semantic/0.1/dogont-vicci-extension#");
        model.setNsPrefix("dogont", "http://elite.polito.it/ontologies/dogont.owl#");
        model.setNsPrefix("instance", "http://openhab-semantic/0.1/instance#");
        model.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
        model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
    }

    /**
     * Adds Ontology informations such as imports.
     *
     * @param model
     */
    public static void addOntologyInformation(OntModel model) {
        Ontology ont = model.createOntology("");
        ont.addImport(model.createResource(DogontSchema.BASE_URI));
        ont.addImport(model.createResource(VicciExtensionSchema.BASE_URI));
        ont.addVersionInfo("Created Automatically By OpenHAB SAL");
    }

}
