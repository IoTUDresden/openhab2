package org.openhab.io.semantic.dogont.internal.util;

import org.openhab.io.semantic.dogont.internal.ontology.DogontSchema;

/**
 * Contains all required SPARQL querys as string
 * 
 * @author André Kühnert
 */
public class QueryResource {
	
	public static final String Prefix = ""
			+ "PREFIX rdfs: <" + SemanticConstants.NS_RDFS_SCHEMA + "> "
			+ "PREFIX rdf: <" + SemanticConstants.NS_RDF_SYNTAX + "> "
			+ "PREFIX dogont: <" + DogontSchema.NS + "> "
			+ "PREFIX instance: <" + SemanticConstants.NS_INSTANCE + "> ";

	/**
	 * Use String.format: Namespace, Property, Value
	 */
	public static final String SubjectByPropertyValueUncertainty = "PREFIX rdfs: <"
			+ SemanticConstants.NS_RDFS_SCHEMA + "> " 
			+ "PREFIX : <%s> " 
			+ "SELECT * " 
			+ "WHERE {"
			+ "       ?subject : %s" + " ?value . " + "FILTER regex(?value, \"%s" + "\", \"i\") ."
			+ "      }";

	/**
	 * Use String.format: namespace, property, value
	 */
	public static final String SubjectByPropertyValue = "PREFIX rdfs: <" + SemanticConstants.NS_RDFS_SCHEMA + "> " 
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
	 * Selects all BuildingThings which have an StateValue.<br>
	 * varNames: instance, state, value, realValue
	 */
	public static final String BuildingThingsContainingStateValue = ""
			+ "PREFIX rdfs: <" + SemanticConstants.NS_RDFS_SCHEMA + "> "
			+ "PREFIX rdf: <" + SemanticConstants.NS_RDF_SYNTAX + "> "
			+ "PREFIX dogont: <" + DogontSchema.NS + "> "
			+ "SELECT ?instance ?state ?value ?realValue "
			+ "WHERE { "
			+ "?class rdfs:subClassOf* dogont:BuildingThing. "
			+ "?instance rdf:type ?class . "
			+ "?instance dogont:hasState ?state. "
			+ "?state dogont:hasStateValue ?value. "
			+ "?value dogont:realStateValue ?realValue }";
	
	/**
	 * Ask query. True if the given resource is a subClassOf* Functionality.<br>
	 * Use String.format local resource name
	 */
	public static final String ResourceIsSubClassOfFunctionality = ""
			+ "PREFIX rdf: <" + SemanticConstants.NS_RDF_SYNTAX + "> "
			+ "PREFIX rdfs: <" + SemanticConstants.NS_RDFS_SCHEMA + "> "
			+ "PREFIX instance: <" + SemanticConstants.NS_INSTANCE + "> "
			+ "PREFIX dogont: <" + DogontSchema.NS + "> " 
			+ "ASK "
			+ "{ "
			+ "	instance:%s "
			+ "	rdf:type ?type. "
			+ "	?type rdfs:subClassOf* dogont:Functionality "
			+ "}";
	
	/**
	 * Query for receiving the location name for a thing.
	 * Use String.format instance uri of the thing
	 */
	public static final String LocationNameOfThing = Prefix
			+ "SELECT ?location ?realname "
			+ "WHERE { "
			+ " instance:%s dogont:isIn ?location . "
			+ "	?location rdfs:label ?realname . "
			+ "} ";
	
	/**
	 * Query for receiving the location name for a state.
	 * Use String.format instance uri of the state.
	 */
	public static final String LocationNameOfFunctionality = Prefix 
			+ "SELECT ?location ?realname "
			+ "WHERE { "
			+ " ?thing dogont:hasFunctionality instance:%s . "
			+ " ?thing dogont:isIn ?location . "
			+ " ?location rdfs:label ?realname . "
			+ "} ";
	
	/**
	 * Query for receiving the location name for a state.
	 * Use String.format instance uri of the state.
	 */
	public static final String LocationNameOfState = Prefix 
			+ "SELECT ?location ?realname "
			+ "WHERE { "
			+ " ?thing dogont:hasState instance:%s ."
			+ " ?thing dogont:isIn ?location . "
			+ " ?location rdfs:label ?realname . "
			+ "} ";
	
	/**
	 * Gets all State items with their location name, and the type name for state and the thing
	 */
	public static final String AllSensors = Prefix
			+ "SELECT ?instance ?shortName ?typeName ?location ?thingName ?unit ?symbol "
			+ " WHERE { "
			+ "  ?class rdfs:subClassOf* dogont:State . "
			+ "	 ?instance rdf:type ?class . "
			+ "  bind(strafter(str(?instance),str(instance:)) as ?shortName) . "
			+	"bind(strafter(str(?class),str(dogont:)) as ?typeName) . "
			+	"?thing dogont:hasState ?instance . "
			+	"?thing rdf:type ?thingType . "
			+	"bind(strafter(str(?thingType),str(dogont:)) as ?thingName) . "
			+	"optional { "
			+		"?thing dogont:isIn ?loc . "
			+		"?loc rdfs:label ?location . "
			+	"} "
			+	" optional { "
			+	"	?instance dogont:hasStateValue ?value . "
			+	"	?value dogont:unitOfMeasure ?unit . "
			+	" ?unit uomvocab:prefSymbol ?symbol . "
			+	"} "			
			+"}";
}
