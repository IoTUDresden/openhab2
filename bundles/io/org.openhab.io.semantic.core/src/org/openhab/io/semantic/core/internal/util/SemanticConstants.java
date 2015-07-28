package org.openhab.io.semantic.core.internal.util;

public class SemanticConstants {
	//TODO the files should be located in a openhab config folder, not in this bundle
	
	private SemanticConstants() {
		// no need for a instance of this
	}
	
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
}
