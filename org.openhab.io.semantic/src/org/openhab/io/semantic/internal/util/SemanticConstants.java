package org.openhab.io.semantic.internal.util;

public class SemanticConstants {
	//TODO the files should be located in a openhab config folder, not in this bundle	
	
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
	public static final String INSTANCE_SKELLETON = "resources/instance_skeleton.ttl";
	
	/**
	 * path to the semiwa structure
	 */
	public static final String STRUCTURE = "resources/semiwa.ttl";
	
	/**
	 * namespace for the semiwa instances 
	 */
	public static final String NS_INSTANCE = "http://semiwa.org/0.1/instances#";
	
	/**
	 * namepsace for the semiwa schema
	 */
	public static final String NS_SCHEMA = "http://semiwa.org/0.1/schema#";
	
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

}
