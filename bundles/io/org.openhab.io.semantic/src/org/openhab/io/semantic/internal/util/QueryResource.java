package org.openhab.io.semantic.internal.util;

/**
 * Contains all required SPARQL querys as string
 * 
 * @author André Kühnert
 */
public class QueryResource {	
	
	/**
	 * Use String.format: Namespace, Property, Value
	 */
	public static final String SubjectByPropertyValueUncertainty = "PREFIX rdfs: <" + SemanticConstants.NS_RDF_SCHEMA +"> "
			+ "PREFIX : <%s> "
			+ "SELECT * "
			+ "WHERE {"
			+ "       ?subject : %s"
			+ " ?value . "
			+ "FILTER regex(?value, \"%s"
			+ "\", \"i\") ."
			+ "      }";
	
	/**
	 * Use String.format: namespace, property, value
	 */
	public static final String SubjectByPropertyValue = "PREFIX rdfs: <" + SemanticConstants.NS_RDF_SCHEMA +"> "
			+ "PREFIX : <%s>"
			+ "SELECT * "
			+ "WHERE {"
			+ "       ?subject : %s"
			+ " \"%s\" . "
			+ "}";
}
