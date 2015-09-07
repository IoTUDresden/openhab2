package org.openhab.io.semantic.core.internal.util;

/**
 * Constants for name prefixes in the semantic model
 * @author André Kühnert
 *
 */
public class SemanticConstants {
	
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
