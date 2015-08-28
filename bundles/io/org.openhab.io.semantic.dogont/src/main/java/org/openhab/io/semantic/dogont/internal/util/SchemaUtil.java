package org.openhab.io.semantic.dogont.internal.util;

import com.hp.hpl.jena.rdf.model.Model;

public class SchemaUtil {
	
	/**
	 * Adds the NS prefix for all imports to the instance model
	 * 
	 * @param model
	 */
	//TODO check if this needed. at the moment this is read from an instance file
	public static void addRequiredNamespacePrefixToInstanceModel(Model model){
		model.setNsPrefix("dogont", "http://elite.polito.it/ontologies/dogont.owl#>");
		model.setNsPrefix("instance", "http://openhab-semantic/0.1/instance#");
		model.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
		model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
	}

}
