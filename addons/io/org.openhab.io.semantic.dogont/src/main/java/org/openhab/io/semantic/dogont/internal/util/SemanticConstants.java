package org.openhab.io.semantic.dogont.internal.util;

/**
 * Constants for the work with the semantic model.
 *
 * @author André Kühnert
 *
 */
public class SemanticConstants {
    // TODO the files should be located in a openhab config folder, not in this bundle

    private SemanticConstants() {
        // no need for a instance of this
    }

    /**
     * Base path to the tdb folder
     */
    public static final String TDB_PATH_BASE = "semantic/tdb/";

    /**
     * namespace for the openhab dogont instances
     */
    public static final String NS_INSTANCE = "http://openhab-semantic/0.1/instance#";

    /**
     * namespace for the openhab dogont templates
     */
    public static final String NS_TEMPLATE = "http://openhab-semantic/0.1/template#";

    /**
     * Graph name for the instance graph
     */
    public static final String GRAPH_NAME_INSTANCE = "http://openhab-semantic/0.1/instance";

    /**
     * Graph name for the template graph
     */
    public static final String GRAPH_NAME_TEMPLATE = "http://openhab-semantic/0.1/template";

    /**
     * namepsace for the dogont schema
     */
    public static final String NS_SCHEMA = "http://elite.polito.it/ontologies/dogont.owl#";

    /**
     * Thing_ prefix for the individual names, of the type 'BuildingThing'
     */
    public static final String THING_PREFIX = "Thing_";

    /**
     * State_ prefix for the individual names, of the type 'State'
     */
    public static final String STATE_PREFIX = "State_";

    /**
     * Function_ prefix for individual names, of the type 'Functionality'
     */
    public static final String FUNCTION_PREFIX = "Function_";

    /**
     * the complete prefix incl. dogont namespace and thing prefix for 'BuildingThings'
     */
    public static final String NS_AND_THING_PREFIX = NS_INSTANCE + THING_PREFIX;

    /**
     * the complete prefix incl. dogont namespace and state prefix.
     */
    public static final String NS_AND_STATE_PREFIX = NS_INSTANCE + STATE_PREFIX;

    /**
     * the complete prefix incl. dogont namespace and function prefix for.
     */
    public static final String NS_AND_FUNCTION_PREFIX = NS_INSTANCE + FUNCTION_PREFIX;

    /**
     * the complete prefix incl. template namespace and thing prefix for 'BuildingThings'
     */
    public static final String NS_AND_THING_PREFIX_TEMPLATE = NS_TEMPLATE + THING_PREFIX;

    /**
     * the complete prefix incl. template namespace and state prefix.
     */
    public static final String NS_AND_STATE_PREFIX_TEMPLATE = NS_TEMPLATE + STATE_PREFIX;

    /**
     * the complete prefix incl. template namespace and function prefix for.
     */
    public static final String NS_AND_FUNCTION_PREFIX_TEMPLATE = NS_TEMPLATE + FUNCTION_PREFIX;

    public static final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema#";

    /**
     * path to the templates
     */
    public static final String TEMPLATE_FILE = "semantic/resource/instance/openhab_templates.ttl";

    /**
     * path to the Vicci Extension File
     */
    public static final String VICCI_EXTENSION_FILE = "semantic/resource/models/dogont-vicci-extension.ttl";

    /**
     * base path to the local models
     */
    public static final String DEFAULT_ONTOLOGY_PATH = "file:semantic/resource/models/";

    /**
     * The Turtle language definition as string
     */
    public static final String TURTLE_STRING = "TURTLE";

    /**
     * Namespace for the rdfs (rdf-schema)
     */
    public static final String NS_RDFS_SCHEMA = "http://www.w3.org/2000/01/rdf-schema#";

    /**
     * Namespace for the rdf (rdf-syntax)
     */
    public static final String NS_RDF_SYNTAX = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    /**
     * Namespace for uomvocab
     */
    public static final String NS_UOMVOCAB = "http://purl.oclc.org/NET/muo/muo#";

}
