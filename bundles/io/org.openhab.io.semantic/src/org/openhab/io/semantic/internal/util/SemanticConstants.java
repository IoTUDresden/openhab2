package org.openhab.io.semantic.internal.util;

public class SemanticConstants {
	//TODO the files should be located in a openhab config folder, not in this bundle
	
	private SemanticConstants() {
		// no need for a instance of this
	}
	
	/**
	 * Base path to the tdb folder
	 */
	public static final String TDB_PATH_BASE = "Data/";
	
	/**
	 * Path for the openhab item instances model
	 */
	public static final String TDB_PATH_OPENHAB = "OpenHABInstances";

	/**
	 * path to the instance skeleton
	 */

	public static final String INSTANCE_SKELETON = "resources/instance_skeleton.ttl";
	
	/**
	 * namespace for the semiwa instances 
	 */
	public static final String NS_INSTANCE = "http://openhab-semantic/0.1/instance#";
	
	/**
	 * namepsace for the semiwa schema
	 */
	public static final String NS_SCHEMA = "http://elite.polito.it/ontologies/dogont.owl#";

	
	/**
	 * path to the empty instances
	 */
	public static final String EMPTY_INSTANCE = "resources/empty_instances.ttl";
	
	/**
	 * base path to the local models 
	 */
	public static final String DEFAULT_ONTOLOGY_PATH = "file:resources/";
	
	/**
	 * The Turtle language definition as string
	 */
	public static final String TURTLE_STRING = "TURTLE";
	
	/**
	 * Namespace for the rdf schema
	 */
	public static final String NS_RDF_SCHEMA = "http://www.w3.org/2000/01/rdf-schema#";

}
