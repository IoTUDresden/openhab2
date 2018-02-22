package org.openhab.io.semantic.dogont.internal.util;

import org.openhab.io.semantic.core.util.Poi;
import org.openhab.io.semantic.core.util.SemanticHealthSensor;
import org.openhab.io.semantic.core.util.SemanticPerson;
import org.openhab.io.semantic.core.util.SemanticRobot;
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
            + SemanticConstants.NS_UOMVOCAB + "> " + "PREFIX vicci: <" + VicciExtensionSchema.NS + "> " + "PREFIX xsd:<"
            + SemanticConstants.XSD_NAMESPACE + "> \n";

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
            + "bind(strafter(str(?class),str('#')) as ?typeName) . " + "?thing dogont:hasState ?instance . "
            + "?thing rdf:type ?thingType . " + "bind(strafter(str(?thingType),str('#')) as ?thingName) . "
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
     * Gets the poi of a thing
     *
     * @param thingName
     * @return
     */
    public static final String getThingPoi(String thingName) {
        thingName = SemanticConstants.NS_AND_THING_PREFIX + thingName;
        StringBuilder builder = new StringBuilder();
        builder.append(Prefix);
        builder.append(" SELECT ?position ?orientation");
        builder.append("  WHERE  {");
        builder.append("      BIND(URI(\"" + thingName + "\") as ?thing)");
        builder.append(" ?thing vicci:hasRobotPosition ?poi.");
        builder.append(" ?poi vicci:hasPosition ?position.");
        builder.append("  ?poi vicci:hasOrientation ?orientation.  }");
        return builder.toString();
    }

    /**
     * Delete stmt for the poi of a thing
     *
     * @param thingName
     * @return
     */
    public static final String deleteThingPoi(String thingName) {
        thingName = SemanticConstants.NS_AND_THING_PREFIX + thingName;
        StringBuilder builder = new StringBuilder();
        builder.append(Prefix);
        builder.append(" DELETE { ");
        builder.append("  ?thing vicci:hasRobotPosition ?poi .");
        builder.append("  ?poi vicci:hasPosition ?p .");
        builder.append("  ?poi vicci:hasOrientation ?o . ");
        builder.append("  ?poi rdf:type vicci:RobotPosition . ");
        builder.append("} WHERE { ");
        builder.append("  BIND(URI( \"" + thingName + "\") as ?thing) ");
        builder.append("  ?thing vicci:hasRobotPosition ?poi. ");
        builder.append("  ?poi vicci:hasPosition ?p. ");
        builder.append("  ?poi vicci:hasOrientation ?o. ");
        builder.append("} ");
        return builder.toString();
    }

    /**
     * Update query to update the poi of a thing.
     *
     * @param itemName
     * @param newPoi
     * @return
     */
    public static final String updateThingPoi(String thingName, Poi newPoi) {
        thingName = SemanticConstants.NS_AND_THING_PREFIX + thingName;
        StringBuilder builder = new StringBuilder();
        builder.append(Prefix);
        builder.append("DELETE { ");
        builder.append("  ?thing vicci:hasRobotPosition ?poi. ");
        builder.append("  ?poi vicci:hasPosition ?p. ");
        builder.append("  ?poi vicci:hasOrientation ?o. ");
        builder.append("  ?poi rdf:type vicci:RobotPosition . ");
        builder.append("} ");
        builder.append("INSERT { ");
        builder.append("  ?thing vicci:hasRobotPosition [");
        builder.append("  rdf:type vicci:RobotPosition; ");
        builder.append("  vicci:hasPosition \"" + newPoi.getPosition() + "\"; ");
        builder.append("  vicci:hasOrientation \"" + newPoi.getOrientation() + "\" ] .");
        builder.append("} ");
        builder.append("WHERE { ");
        builder.append("  BIND(URI( \"" + thingName + "\") as ?thing)");
        builder.append("  OPTIONAL { ");
        builder.append("    ?thing vicci:hasRobotPosition ?poi. ");
        builder.append("    ?poi vicci:hasPosition ?position.");
        builder.append("    ?poi vicci:hasOrientation ?orientation.");
        builder.append(" }};");
        return builder.toString();
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
        builder.append("SELECT ?thing ?thingName ?class ?loc ?realLoc ?position ?orientation ?locType ");
        builder.append("WHERE { ");
        builder.append("  ?class rdfs:subClassOf* dogont:Controllable ." + " ?thing rdf:type ?class. ");
        builder.append("  OPTIONAL {    ");
        builder.append("    ?thing dogont:isIn ?loc." + " ?loc rdfs:label ?realLoc . ?loc rdf:type ?locType");
        builder.append("  }");
        builder.append("  OPTIONAL {");
        builder.append("    ?thing vicci:hasRobotPosition ?p .");
        builder.append("    ?p vicci:hasOrientation ?orientation ." + " ?p vicci:hasPosition ?position .");
        builder.append("  }");
        builder.append("  BIND(STRAFTER(STR(?thing), '" + SemanticConstants.NS_AND_THING_PREFIX + "') as ?thingName)");
        builder.append("}");
        builder.append("ORDER BY ?thingName");
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

    /**
     * Updates the thing location
     *
     * @param thingName
     *            openHab Thing name
     * @param locationUri
     *            uri of location e.g. http://openhab...#location
     * @return
     */
    public static final String updateThingLocation(String thingName, String locationUri) {
        thingName = SemanticConstants.NS_AND_THING_PREFIX + thingName;
        StringBuilder builder = new StringBuilder();
        builder.append(Prefix);
        builder.append("DELETE { ");
        builder.append("  ?thing dogont:isIn ?oldLoc . ");
        builder.append("} ");
        builder.append("INSERT { ");
        builder.append("  ?thing dogont:isIn ?newLoc . ");
        builder.append("} ");
        builder.append("WHERE { ");
        builder.append("  BIND(URI('" + locationUri + "') as ?newLoc) ");
        builder.append("  BIND(URI('" + thingName + "') as ?thing) ");
        builder.append("  OPTIONAL { ?thing dogont:isIn ?oldLoc . }");
        builder.append("}");
        return builder.toString();
    }

    /**
     * Deletes the existing Location from a thing
     *
     * @param thingName
     *            openHab Thing name
     * @return
     */
    public static final String deleteThingLocation(String thingName) {
        thingName = SemanticConstants.NS_AND_THING_PREFIX + thingName;
        StringBuilder builder = new StringBuilder();
        builder.append(Prefix);
        builder.append("DELETE { ");
        builder.append("  ?thing dogont:isIn ?oldLoc . ");
        builder.append("} ");
        builder.append("WHERE { ");
        builder.append("  BIND(URI('" + thingName + "') as ?thing) ");
        builder.append("  ?thing dogont:isIn ?oldLoc .");
        builder.append("}");
        return builder.toString();
    }

    /**
     * Updates or adds a {@link SemanticPerson} to the instance model.
     * Uid of person should not be empty!.
     *
     * @param person
     * @return
     */
    public static final String addPerson(SemanticPerson person) {
        String thing = SemanticConstants.NS_AND_THING_PREFIX + person.getUid();
        String monitor = SemanticConstants.NS_AND_THING_PREFIX;
        boolean hasHealthMonitor = person.getHealthMonitorUid() != null && !person.getHealthMonitorUid().isEmpty();
        if (hasHealthMonitor) {
            monitor = monitor + person.getHealthMonitorUid();
        }

        StringBuilder builder = new StringBuilder();
        builder.append(Prefix);
        builder.append("DELETE{ ");
        builder.append("  ?person vicci:hasFirstname ?fName . ");
        builder.append("  ?person vicci:hasLastname ?lName . ");
        builder.append("  ?person vicci:hasGender ?gender . ");
        builder.append("  ?person vicci:hasAge ?age . ");
        builder.append("  ?person vicci:hasHealthMonitor ?monitor ");
        builder.append("}  ");
        builder.append("INSERT{ ");
        builder.append(" ?person vicci:hasFirstname '" + person.getFirstName() + "'^^xsd:string . ");
        builder.append(" ?person vicci:hasLastname '" + person.getLastName() + "'^^xsd:string . ");
        builder.append(" ?person vicci:hasGender '" + person.getGender() + "'^^xsd:string . ");
        builder.append(" ?person vicci:hasAge '" + person.getAge() + "'^^xsd:string . ");

        if (hasHealthMonitor) {
            builder.append("  ?person vicci:hasHealthMonitor ?monitorUri . ");
        }

        builder.append("}  ");
        builder.append("WHERE {  ");
        builder.append("  bind(uri('" + thing + "') as ?person)");

        if (hasHealthMonitor) {
            builder.append("  bind(uri('" + monitor + "') as ?monitorUri) ");
        }

        builder.append(" ?person rdf:type ?class.");
        builder.append("  OPTIONAL {  ?person vicci:hasFirstname ?fName . }");
        builder.append("  OPTIONAL {  ?person vicci:hasLastname ?lName . }");
        builder.append("  OPTIONAL {  ?person vicci:hasGender ?gender . }");
        builder.append("  OPTIONAL {  ?person vicci:hasAge ?age .}");
        builder.append("  OPTIONAL {  ?person vicci:hasHealthMonitor ?monitor . }");
        builder.append("} ");
        return builder.toString();
    }

    /**
     * Gets all {@link SemanticPerson}s query
     *
     * @return
     */
    public static final String getPersons() {
        StringBuilder builder = new StringBuilder();
        builder.append(Prefix);
        builder.append("SELECT ?class ?fName ?lName ?age ?gender ?uid ?healthMonitorUid ");
        builder.append("WHERE { ");
        builder.append("  ?class rdfs:subClassOf* vicci:Person .");
        builder.append("  ?person rdf:type ?class .");
        builder.append("  OPTIONAL { ");
        builder.append("    ?person vicci:hasFirstname ?fName .");
        builder.append("    ?person vicci:hasLastname ?lName .");
        builder.append("    ?person vicci:hasAge ?age .");
        builder.append("    ?person vicci:hasGender ?gender .");
        // builder.append(" }");
        // builder.append(" OPTIONAL { ");
        builder.append("    ?person vicci:hasHealthMonitor ?healthMonitor");
        builder.append("    bind(strafter(str(?healthMonitor), '" + SemanticConstants.NS_AND_THING_PREFIX
                + "') as ?healthMonitorUid)");
        builder.append("  } ");
        builder.append("  bind(strafter(str(?person), '" + SemanticConstants.NS_AND_THING_PREFIX + "') as ?uid)");
        builder.append("}");
        return builder.toString();
    }

    /**
     * Query for all {@link SemanticRobot}
     *
     * @return
     */
    public static final String getAllRobots() {
        StringBuilder builder = new StringBuilder();
        builder.append(Prefix);
        builder.append("SELECT ?class ?robot ?uid ?moveUid ?moveStateUid ?posStateUid ");
        builder.append("WHERE { ");
        builder.append("  ?class rdfs:subClassOf* vicci:Robot . ");
        builder.append("  ?robot rdf:type ?class . ");
        builder.append("  bind(strafter(str(?robot), '" + SemanticConstants.NS_AND_THING_PREFIX + "') as ?uid)");
        builder.append("  OPTIONAL {");
        builder.append("    ?robot dogont:hasFunctionality ?move .");
        builder.append("    ?move rdf:type vicci:RobotMovementFunctionality .");
        builder.append(
                "    bind(strafter(str(?move), '" + SemanticConstants.NS_AND_FUNCTION_PREFIX + "') as ?moveUid)");
        builder.append("    ?robot dogont:hasState ?moveState .");
        builder.append("    ?moveState rdf:type vicci:RobotMovementState .");
        builder.append("    bind(strafter(str(?moveState), '" + SemanticConstants.NS_AND_STATE_PREFIX
                + "') as ?moveStateUid)");
        builder.append("    ?robot dogont:hasState ?posState .");
        builder.append("    ?posState rdf:type vicci:RobotPositionState .");
        builder.append(
                " bind(strafter(str(?posState), '" + SemanticConstants.NS_AND_STATE_PREFIX + "') as ?posStateUid)");
        builder.append("  }");
        builder.append("}");
        return builder.toString();
    }

    /**
     * Query for all {@link SemanticHealthSensor}s
     *
     * @return
     */
    public static final String getAllHealthSensors() {
        StringBuilder builder = new StringBuilder();
        builder.append(Prefix);
        builder.append(
                "SELECT ?class ?uid ?healtM ?heartState ?heartUid ?heartRateValue ?oxygenState ?oxygenUid ?oxygenValue ");
        builder.append("WHERE { ");
        builder.append("  ?class rdfs:subClassOf* vicci:HealthMonitor .");
        builder.append("  ?healtM rdf:type ?class .");
        builder.append("   bind(strafter(str(?healtM), '" + SemanticConstants.NS_AND_THING_PREFIX + "') as ?uid)");
        builder.append("  OPTIONAL {");
        builder.append("    ?healtM dogont:hasState ?heartState .");
        builder.append("    ?heartState rdf:type vicci:HeartRateMeasurementState .");
        builder.append("    ?heartState dogont:hasStateValue ?heartV .");
        builder.append("    ?heartV dogont:realStateValue ?heartRateValue .");
        builder.append("    ?healtM dogont:hasState ?oxygenState .");
        builder.append("    ?oxygenState rdf:type vicci:BloodOxygenSaturationMeasurementState .");
        builder.append("    ?oxygenState dogont:hasStateValue ?oxygenV .");
        builder.append("    ?oxygenV dogont:realStateValue ?oxygenValue .");
        builder.append(
                "    bind(strafter(str(?heartState), '" + SemanticConstants.NS_AND_STATE_PREFIX + "') as ?heartUid)");
        builder.append(
                "    bind(strafter(str(?oxygenState), '" + SemanticConstants.NS_AND_STATE_PREFIX + "') as ?oxygenUid)");
        builder.append("}}");
        return builder.toString();
    }
}
