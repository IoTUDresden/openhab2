package org.openhab.io.semantic.dogont.internal.util;

import com.hp.hpl.jena.util.LocationMapper;

/**
 * Custom LocationMapper. Contains alternative mappings for the imports.
 * Necessary, if openhab has no internet connection, else the execution may fail.
 *
 * @author André Kühnert
 */
public class LocationMapperCustom extends LocationMapper {

    public LocationMapperCustom() {
        addLocalPathsToDefaultLocationMapper();
    }

    /**
     * Add the local paths to the default location mapper
     */
    public void addLocalPathsToDefaultLocationMapper() {
        // seems that the dogont, ucum and muo-vocab is enough at this moment

        addAltEntry("http://elite.polito.it/ontologies/dogont.owl",
                SemanticConstants.DEFAULT_ONTOLOGY_PATH + "dogont.owl");
        addAltEntry("http://openhab-semantic/0.1/dogont-vicci-extension", SemanticConstants.VICCI_EXTENSION_FILE);
        addAltEntry("http://purl.org/NET/muo/ucum/", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "ucum.ttl");
        addAltEntry("http://purl.org/NET/muo/muo-vocab.owl/",
                SemanticConstants.DEFAULT_ONTOLOGY_PATH + "muo-vocab.owl.ttl");
        addAltEntry("http://www.w3.org/2000/01/rdf-schema", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "rdfs.ttl");
        addAltEntry("http://www.w3.org/1999/02/22-rdf-syntax-ns", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "rdf.ttl");

        // addAltEntry("http://creativecommons.org/ns", SemanticConstants.DEFAULT_ONTOLOGY_PATH +
        // "creativecommons-ns.rdf");
        // addAltEntry("http://protege.stanford.edu/plugins/owl/protege", SemanticConstants.DEFAULT_ONTOLOGY_PATH +
        // "protege.owl");
        // addAltEntry("http://xmlns.com/foaf/0.1/", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "xmlns-foaf-rdf");
        // addAltEntry("http://www.owl-ontologies.com/2005/08/07/xsp.owl", SemanticConstants.DEFAULT_ONTOLOGY_PATH +
        // "xsp.owl");
        // addAltEntry("http://purl.org/dc/terms/", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "");
        // addAltEntry("http://purl.org/vocab/vann/", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "");
        // addAltEntry("http://purl.org/goodrelations/v1", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "");
        // addAltEntry("http://www.w3.org/2003/11/swrl", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "");
        // addAltEntry("http://www.w3.org/2006/12/owl2-xml", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "");
        // addAltEntry("http://www.w3.org/2002/07/owl", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "");
        // addAltEntry("http://www.w3.org/2003/11/swrlb", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "");
        // addAltEntry("http://www.w3.org/2001/XMLSchema", SemanticConstants.DEFAULT_ONTOLOGY_PATH + "");

    }

}
