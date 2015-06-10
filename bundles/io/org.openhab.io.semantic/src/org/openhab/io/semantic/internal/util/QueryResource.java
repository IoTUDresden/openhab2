package org.openhab.io.semantic.internal.util;

import org.openhab.io.semantic.internal.ontology.DogontSchema;

/**
 * Contains all required SPARQL querys as string
 * 
 * @author André Kühnert
 */
public class QueryResource {

	/**
	 * Use String.format: Namespace, Property, Value
	 */
	public static final String SubjectByPropertyValueUncertainty = "PREFIX rdfs: <"
			+ SemanticConstants.NS_RDF_SCHEMA + "> " 
			+ "PREFIX : <%s> " 
			+ "SELECT * " 
			+ "WHERE {"
			+ "       ?subject : %s" + " ?value . " + "FILTER regex(?value, \"%s" + "\", \"i\") ."
			+ "      }";

	/**
	 * Use String.format: namespace, property, value
	 */
	public static final String SubjectByPropertyValue = "PREFIX rdfs: <" + SemanticConstants.NS_RDF_SCHEMA + "> " 
	 + "PREFIX : <%s>" 
	 + "SELECT * " 
	 + "WHERE {" + "       ?subject : %s" + " \"%s\" . " + "}";

	/**
	 * Use String.format phaseId
	 */
	public static final String SubjectByPhaseId = "PREFIX dogont: <" + DogontSchema.NS + "> "
			+ "SELECT ?subject " 
			+ "WHERE {" + "	?statevalue dogont:phaseID \"<%s>\" ."
			+ "	?state dogont:hasStateValue ?statevalue. " 
			+ "	?subject dogont:hasState ?state. " + "}";

	/**
	 * Use String.format phaseId
	 */
	public static final String StateValueByPhaseId = "PREFIX dogont: <" + DogontSchema.NS + "> "
			+ "SELECT ?statevalue " + "WHERE {" + "	?statevalue dogont:phaseID \"<%s>\" ." + "}";

	/**
	 * Selects all BuildingThings which have an StateValue
	 * varNames: instance, state, value, realValue
	 */
	public static final String BuildingThingsContainingStateValue = "PREFIX dogont: <" + DogontSchema.NS + "> "
			+ "SELECT ?instance ?state ?value ?realValue"
			+ "WHERE {"
			+ "?class rdfs:subClassOf* dogont:BuildingThing."
			+ "?instance rdf:type ?class ."
			+ "?instance dogont:hasState ?state."
			+ "?state dogont:hasStateValue ?value."
			+ "?value dogont:realStateValue ?realValue";
}
