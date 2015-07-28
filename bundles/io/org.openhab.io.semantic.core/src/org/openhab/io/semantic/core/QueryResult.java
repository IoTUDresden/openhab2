package org.openhab.io.semantic.core;

/**
 * The Result from a semantic query
 * 
 * @author André Kühnert
 */
public interface QueryResult {
	@Deprecated
	String getAsString();

	/**
	 * Gets the result from a semantic query as a string in json format
	 * 
	 * @return String in Json-format
	 */
	String getAsJsonString();

}
