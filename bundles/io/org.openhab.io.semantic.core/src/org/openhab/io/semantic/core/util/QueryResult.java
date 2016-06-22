package org.openhab.io.semantic.core.util;

/**
 * The Result from a semantic query
 * 
 * @author André Kühnert
 */
public interface QueryResult {

	/**
	 * Gets the result from a semantic query as a string in json format.<br>
	 * The SPARQL Query Result is serialize to Json. The output has the same format as described in
	 * <a href="http://www.w3.org/TR/rdf-sparql-json-res/"> Serializing SPARQL Query Results</a>.
	 * 
	 * @return String in Json-format
	 */
	String getAsJsonString();

}
