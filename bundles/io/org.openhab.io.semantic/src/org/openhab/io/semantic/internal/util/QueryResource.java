package org.openhab.io.semantic.internal.util;

import org.openhab.io.semantic.ontology.DogontSchema;

/**
 * Contains all required SPARQL querys as string
 * 
 * @author André Kühnert
 */
public class QueryResource {	
	
	/**
	 * Use String.format: Namespace, Property, Value
	 */
	public static final String SubjectByPropertyValueUncertainty = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
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
	public static final String SubjectByPropertyValue = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
			+ "PREFIX : <%s>"
			+ "SELECT * "
			+ "WHERE {"
			+ "       ?subject : %s"
			+ " \"%s\" . "
			+ "}";
	
	/**
	 * Use String.format phaseId
	 */
	public static final String SubjectByPhaseId = "PREFIX dogont: <" + DogontSchema.NS + "> "
			+ "SELECT ?subject "
			+ "WHERE {"
			+ "	?statevalue dogont:phaseID \"<%s>\" ."
			+ "	?state dogont:hasStateValue ?statevalue. "
			+ "	?subject dogont:hasState ?state. "
			+ "}";
	
	/**
	 * Use String.format phaseId
	 */
	public static final String StateValueByPhaseId = "PREFIX dogont: <" + DogontSchema.NS + "> "
			+ "SELECT ?statevalue "
			+ "WHERE {"
			+ "	?statevalue dogont:phaseID \"<%s>\" ."
			+ "}";
}
