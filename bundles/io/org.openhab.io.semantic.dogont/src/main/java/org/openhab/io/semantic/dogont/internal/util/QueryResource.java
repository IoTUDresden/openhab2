package org.openhab.io.semantic.dogont.internal.util;

import org.openhab.io.semantic.core.util.Poi;
import org.openhab.io.semantic.dogont.internal.ontology.DogontSchema;
import org.openhab.io.semantic.dogont.internal.ontology.VicciExtensionSchema;

/**
 * Contains all required SPARQL querys as string
 *
 * @author André Kühnert
 */
public class QueryResource {

    public static final String Prefix = "" + "PREFIX rdfs: <" + SemanticConstants.NS_RDFS_SCHEMA + "> "
            + "PREFIX rdf: <" + SemanticConstants.NS_RDF_SYNTAX + "> " + "PREFIX dogont: <" + DogontSchema.NS + "> "
            + "PREFIX instance: <" + SemanticConstants.NS_INSTANCE + "> " + "PREFIX uomvocab: <"
            + SemanticConstants.NS_UOMVOCAB + "> " + "PREFIX vicci: <" + VicciExtensionSchema.NS + "> \n";

    /**
     * Use String.format: Namespace, Property, Value
     */
    public static final String SubjectByPropertyValueUncertainty = "PREFIX rdfs: <" + SemanticConstants.NS_RDFS_SCHEMA
            + "> " + "PREFIX : <%s> " + "SELECT * " + "WHERE {" + "       ?subject : %s" + " ?value . "
            + "FILTER regex(?value, \"%s" + "\", \"i\") ." + "      }";

    /**
     * Use String.format: namespace, property, value
     */
    public static final String SubjectByPropertyValue = "PREFIX rdfs: <" + SemanticConstants.NS_RDFS_SCHEMA + "> "
            + "PREFIX : <%s>" + "SELECT * " + "WHERE {" + "       ?subject : %s" + " \"%s\" . " + "}";

    /**
     * Use String.format phaseId
     */
    public static final String SubjectByPhaseId = "PREFIX dogont: <" + DogontSchema.NS + "> " + "SELECT ?subject "
            + "WHERE {" + "	?statevalue dogont:phaseID \"<%s>\" ." + "	?state dogont:hasStateValue ?statevalue. "
            + "	?subject dogont:hasState ?state. " + "}";

    /**
     * Use String.format phaseId
     */
    public static final String StateValueByPhaseId = "PREFIX dogont: <" + DogontSchema.NS + "> " + "SELECT ?statevalue "
            + "WHERE {" + "	?statevalue dogont:phaseID \"<%s>\" ." + "}";

    /**
     * Selects all BuildingThings which have an StateValue.<br>
     * varNames: instance, state, value, realValue
     */
    public static final String BuildingThingsContainingStateValue = "" + "PREFIX rdfs: <"
            + SemanticConstants.NS_RDFS_SCHEMA + "> " + "PREFIX rdf: <" + SemanticConstants.NS_RDF_SYNTAX + "> "
            + "PREFIX dogont: <" + DogontSchema.NS + "> " + "SELECT ?instance ?state ?value ?realValue " + "WHERE { "
            + "?class rdfs:subClassOf* dogont:BuildingThing. " + "?instance rdf:type ?class . "
            + "?instance dogont:hasState ?state. " + "?state dogont:hasStateValue ?value. "
            + "?value dogont:realStateValue ?realValue }";

    /**
     * Ask query. True if the given resource is a subClassOf* Functionality.<br>
     * Use String.format local resource name
     */
    public static final String ResourceIsSubClassOfFunctionality = "" + "PREFIX rdf: <"
            + SemanticConstants.NS_RDF_SYNTAX + "> " + "PREFIX rdfs: <" + SemanticConstants.NS_RDFS_SCHEMA + "> "
            + "PREFIX instance: <" + SemanticConstants.NS_INSTANCE + "> " + "PREFIX dogont: <" + DogontSchema.NS + "> "
            + "ASK " + "{ " + "	instance:%s " + "	rdf:type ?type. "
            + "	?type rdfs:subClassOf* dogont:Functionality " + "}";

    /**
     * Query for receiving the location name for a thing.
     * Use String.format instance uri of the thing
     */
    public static final String LocationNameOfThing = Prefix + "SELECT ?location ?realname " + "WHERE { "
            + " instance:%s dogont:isIn ?location . " + "	?location rdfs:label ?realname . " + "} ";

    /**
     * Query for receiving the location name for a state.
     * Use String.format instance uri of the state.
     */
    public static final String LocationNameOfFunctionality = Prefix + "SELECT ?location ?realname " + "WHERE { "
            + " ?thing dogont:hasFunctionality instance:%s . " + " ?thing dogont:isIn ?location . "
            + " ?location rdfs:label ?realname . " + "} ";

    /**
     * Query for receiving the location name for a state.
     * Use String.format instance uri of the state.
     */
    public static final String LocationNameOfState = Prefix + "SELECT ?location ?realname " + "WHERE { "
            + " ?thing dogont:hasState instance:%s ." + " ?thing dogont:isIn ?location . "
            + " ?location rdfs:label ?realname . " + "} ";

    /**
     * Gets all State items with their location name, and the type name for state and the thing
     */
    public static final String AllSensors = Prefix
            + "SELECT ?instance ?shortName ?openHabName ?typeName ?location ?thingName ?unit ?symbol " + " WHERE { "
            + "  ?class rdfs:subClassOf* dogont:State . " + "	 ?instance rdf:type ?class . "
            + "  bind(strafter(str(?instance),str(instance:)) as ?shortName) . "
            + "  bind(strafter(str(?shortName),str(\"State_\")) as ?openHabName) ."
            + "bind(strafter(str(?class),str(dogont:)) as ?typeName) . " + "?thing dogont:hasState ?instance . "
            + "?thing rdf:type ?thingType . " + "bind(strafter(str(?thingType),str(dogont:)) as ?thingName) . "
            + "optional { " + "?thing dogont:isIn ?loc . " + "?loc rdfs:label ?location . " + "} " + " optional { "
            + "	?instance dogont:hasStateValue ?value . " + "	?value dogont:unitOfMeasure ?unit . "
            + " ?unit uomvocab:prefSymbol ?symbol . " + "} " + "}";

    /**
     * Update stmt for updating real state values in the sematic model.
     * Use String.format new Value, item name (the openhab item name can be used,
     * cause the state prefix is contained in the query)
     *
     */
    public static final String UpdateStateValue = Prefix + "\n"
            + "DELETE { ?stateValue dogont:realStateValue ?realStateValue } "
            + "INSERT { ?stateValue dogont:realStateValue \"%s\" } " + "WHERE { " + "    instance:"
            + SemanticConstants.STATE_PREFIX + "%s dogont:hasStateValue  ?stateValue . "
            + "    ?stateValue dogont:realStateValue ?realStateValue . " + "}";

    /**
     * Query to receive a thing which has the specified function or state.
     * Use String.format function name, state name. (Prefix for state or function is not needed)
     */
    public static final String GetThingWithFunctionOrState = Prefix + "\n" + "SELECT ?thing ?func ?state" + "WHERE { "
            + "  { " + "      ?thing dogont:hasFunctionality instance:" + SemanticConstants.FUNCTION_PREFIX + "%s . "
            + "      ?thing dogont:hasFunctionality ?func . " + "  } UNION { " + "     ?thing dogont:hasState instance:"
            + SemanticConstants.STATE_PREFIX + "%s . " + "     ?thing dogont:hasState ?state . " + "  } " + "} ";

    /**
     * Ask query to check, if the given subject already exists in the model.
     * Use String.format subject name.
     */
    public static final String SubjectExistsInModel = Prefix + "\n" + "ASK { instance:%s ?p ?o }";

    /**
     * Select Query to get the of a item
     *
     * @param itemName
     * @return
     */
    public static final String thingPoi(String itemName) {
        // SELECT *
        // WHERE {
        // ?thing dogont:hasFunctionality template:Function_homematic_co2_co2.
        // ?thing vicci:hasRobotPosition ?poi.
        // ?poi vicci:hasPosition ?p.
        // ?poi vicci:hasOrientation ?o.
        // }
        return null;
    }

    /**
     * Update query to update the poi of a thing.
     *
     * @param itemName
     * @param newPoi
     * @return
     */
    public static final String updateThingPoi(String thingName, Poi newPoi) {
        return Prefix + "DELETE { " + " ?thing vicci:hasRobotPosition ?poi." + " ?poi vicci:hasPosition ?p."
                + " ?poi vicci:hasOrientation ?o." + " ?poi rdf:type vicci:RobotPosition." + "}" + "INSERT {"
                + "   ?thing vicci:hasRobotPosition [" + "        rdf:type vicci:RobotPosition; "
                + "        vicci:hasPosition '" + newPoi.getPosition() + "'; " + "        vicci:hasOrientation '"
                + newPoi.getOrientation() + "'" + "        ] ." + " }" + "WHERE {" + "    BIND(" + "instance:"
                + thingName + " as ?thing)" + "     OPTIONAL { " + "         ?thing vicci:hasRobotPosition ?poi. "
                + "         ?poi vicci:hasPosition ?p." + "         ?poi vicci:hasOrientation ?o." + "  }}";
    }

    /**
     * Gets all things with their<br>
     * <br>
     * ?thing: uri of thing <br>
     * ?thingName: thing name without prefixes <br>
     * ?class: type class<br>
     * ?loc: location uri<br>
     * ?realLoc: location name<br>
     * ?position: robot position<br>
     * ?orientation: roboto orientation<br>
     *
     *
     * @return
     */
    public static final String getThings() {
        StringBuilder builder = new StringBuilder();
        builder.append(Prefix);
        builder.append("SELECT ?thing ?thingName ?class ?loc ?realLoc ?position ?orientation ?locType");
        builder.append("WHERE { ");
        builder.append("  ?class rdfs:subClassOf* dogont:Controllable ." + " ?thing rdf:type ?class. ");
        builder.append("  OPTIONAL {    ");
        builder.append("    ?thing dogont:isIn ?loc." + " ?loc rdfs:label ?realLoc . ?loc rdfs:type ?locType");
        builder.append("  }");
        builder.append("  OPTIONAL {");
        builder.append("    ?thing vicci:hasRobotPosition ?p .");
        builder.append("    ?p vicci:hasOrientation ?orientation ." + " ?p vicci:hasPosition ?position .");
        builder.append("  }");
        builder.append("  BIND(STRAFTER(STR(?thing), '" + SemanticConstants.NS_AND_THING_PREFIX + "') as ?thingName)");
        builder.append("}");
        return builder.toString();
    }

    /**
     * Gets all Locations
     *
     * @return
     */
    public static final String getLocations() {
        StringBuilder builder = new StringBuilder();
        builder.append(Prefix);
        builder.append("SELECT ?loc ?realLoc ?class ");
        builder.append("WHERE { ");
        builder.append("  ?class rdfs:subClassOf* dogont:BuildingEnvironment . ");
        builder.append("  ?loc rdf:type ?class.   ");
        builder.append("  ?loc rdfs:label ?realLoc.  ");
        builder.append("}");
        return builder.toString();
    }

}
