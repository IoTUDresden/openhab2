package org.openhab.io.semantic.internal.util;

import com.hp.hpl.jena.util.LocationMapper;

/**
 * Custom LocationMapper. Contains alternative mappings for the imports.
 * Necessary, if openhab has no internet connection, else the execution may fail.
 * 
 * @author André Kühnert
 */
public class LocationMapperCustom extends LocationMapper{
	
	public LocationMapperCustom() {
		addLocalPathsToDefaultLocationMapper();
	}
	
	/**
	 * Add the local paths to the default location mapper
	 */
	public void addLocalPathsToDefaultLocationMapper(){		
		//semiwa mapping
//		altMapping("http://qudt.org/1.1/vocab/dimension", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "qudt-dimension.xml");
//		altMapping("http://qudt.org/1.1/vocab/dimensionalunit", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "qudt-dimensionalunit.xml");
//		altMapping("http://qudt.org/1.1/vocab/quantity", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "qudt-quantity.xml");
//		altMapping("http://qudt.org/1.1/vocab/unit", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "qudt-unit.xml");
//		altMapping("http://www.ai.sri.com/daml/services/owl-s/1.2/Process.owl", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "daml-Process.owl");
//		altMapping("http://www.ai.sri.com/daml/services/owl-s/1.2/Profile.owl", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "daml-Profile.owl");
//		altMapping("http://www.ai.sri.com/daml/services/owl-s/1.2/Service.owl", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "daml-Service.owl");
//		altMapping("http://www.geonames.org/ontology/ontology_v3.1.rdf", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "ontology_v3.1.rdf");
//		altMapping("http://www.linkedmodel.org/schema/dtype", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "linkedmodel-dtype.xml");
//		altMapping("http://www.linkedmodel.org/schema/vaem", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "linkedmodel-vaem.xml");
//		altMapping("http://semiwa.org/0.1/schema", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "semiwa.ttl");
		
		//TODO dogont alternative mapping
		altMapping("http://elite.polito.it/ontologies/dogont.owl", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "dogont.owl");
		altMapping("http://creativecommons.org/ns", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "creativecommons-ns.rdf");
		altMapping("http://protege.stanford.edu/plugins/owl/protege", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "protege.owl");
		altMapping("http://xmlns.com/foaf/0.1/", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "xmlns-foaf-rdf");
		altMapping("http://www.owl-ontologies.com/2005/08/07/xsp.owl", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "");
		altMapping("http://purl.org/dc/terms/", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "");
		altMapping("http://purl.org/vocab/vann/", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "");
		altMapping("http://purl.org/NET/muo/ucum/", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "ucum.ttl");
		altMapping("http://purl.org/NET/muo/muo-vocab.owl/", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "muo-vocab.owl.ttl");	
		altMapping("http://purl.org/goodrelations/v1", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "");
		altMapping("http://www.w3.org/2000/01/rdf-schema", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "");
		altMapping("http://www.w3.org/2003/11/swrl", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "");
		altMapping("http://www.w3.org/2006/12/owl2-xml", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "");
		altMapping("http://www.w3.org/2002/07/owl", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "");
		altMapping("http://www.w3.org/2003/11/swrlb", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "");
		altMapping("http://www.w3.org/2001/XMLSchema", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "");
		altMapping("http://www.w3.org/1999/02/22-rdf-syntax-ns", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "");
	}

}
