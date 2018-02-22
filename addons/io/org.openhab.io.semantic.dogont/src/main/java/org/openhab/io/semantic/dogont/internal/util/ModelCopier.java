package org.openhab.io.semantic.dogont.internal.util;

import org.eclipse.smarthome.core.items.Item;
import org.openhab.io.semantic.dogont.internal.ontology.DogontSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;

/**
 * All functions need to be locked from the caller with WRITE! {@link OntModel#enterCriticalSection(boolean)} -
 * {@link Lock#WRITE}
 */
public class ModelCopier {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelCopier.class);
    private static final String ITEM_NAME_DELIMITER = "_";

    private Dataset dataset;

    /**
     * Templates are READ from the base model and WRITTEN to the target model.
     * The template/instance graph names are defined in {@link SemanticConstants#GRAPH_NAME_TEMPLATE} and
     * {@link SemanticConstants#GRAPH_NAME_INSTANCE}.
     *
     * @param base
     * @param target
     */
    public ModelCopier(Dataset dataset) {
        this.dataset = dataset;
    }

    /**
     * Copies the state and function for one given element. The encapsulating thing is also generated.
     *
     * @param element
     */
    public void copyStateAndFunction(Item element) {
        String id = getLastDelimiter(element.getName());
        String templateName = removeLastDelimiter(element.getName());
        if (id == null || templateName == null) {
            LOGGER.warn("cant get semantic template name for item '{}'", element.getName());
        }

        if (!instanceModelContainsState(element.getName())) {
            copyState(templateName, id);
        }

        if (!instanceModelContainsFunction(element.getName())) {
            copyFunction(templateName, id);
        }
    }

    /**
     * Copies the the state and all needed stuff from the template to the instance model.
     *
     * @param templateName
     *            template name without id and function prefix
     * @param id
     *            id e.g. 'berlin'
     */
    public void copyState(String templateName, String id) {
        LOGGER.debug("try to copy state '{}' from template", templateName);
        String thingName = removeLastDelimiter(templateName);

        executeUpdateAction(getCopyStateAllQuery(templateName, id));

        if (instanceModelContainsThing(thingName)) {
            executeUpdateAction(getAddStateToThingQuery(templateName, thingName, id));
        } else {
            executeUpdateAction(getCopyThingAndAddStateQuery(templateName, id));
        }
    }

    /**
     * Copies the the functionality and all needed stuff from the template to the instance model.
     *
     * @param templateName
     *            template name without id and function prefix
     * @param id
     *            id e.g. 'berlin'
     */
    public void copyFunction(String templateName, String id) {
        LOGGER.debug("try to copy function '{}' from template", templateName);
        String thingName = removeLastDelimiter(templateName);

        executeUpdateAction(getCopyFunctionQuery(templateName, id));

        if (instanceModelContainsThing(thingName)) {
            executeUpdateAction(getAddFunctionToThingQuery(templateName, thingName, id));
        } else {
            executeUpdateAction(getCopyThingAndAddFunctionQuery(templateName, id));
        }

    }

    /**
     * Copies all commands, which are used in the template model, to the instance model.
     */
    public void copyCommands() {
        executeUpdateAction(getCopyAllCommandsQuery());
    }

    /**
     * Copies all locations from the template model to the instance model
     * Use a OntModel otherwise Reasoning will not work for the query.
     */
    public void copyLocations(OntModel model) {
        UpdateRequest req = UpdateFactory.create(getCopyLocationsQuery());
        UpdateAction.execute(req, model);
    }

    private void executeUpdateAction(String formatedQuery) {
        UpdateRequest req = UpdateFactory.create(formatedQuery);
        UpdateAction.execute(req, dataset);
    }

    private static String getLastDelimiter(String name) {
        if (name == null) {
            return null;
        }
        int lastInd = name.lastIndexOf(ITEM_NAME_DELIMITER);
        if (lastInd < 1) {
            return null;
        }
        return name.substring(lastInd + 1);
    }

    private static String removeLastDelimiter(String name) {
        if (name == null) {
            return null;
        }
        int lastInd = name.lastIndexOf(ITEM_NAME_DELIMITER);
        if (lastInd < 1) {
            return null;
        }
        return name.substring(0, lastInd);
    }

    // no state prefix needed
    private boolean instanceModelContainsState(String stateName) {
        String query = getContainsQuery(SemanticConstants.STATE_PREFIX, stateName);
        return executeAskOnInstanceModel(query);
    }

    private boolean instanceModelContainsFunction(String functionName) {
        String query = getContainsQuery(SemanticConstants.FUNCTION_PREFIX, functionName);
        return executeAskOnInstanceModel(query);
    }

    private boolean instanceModelContainsThing(String thingName) {
        String query = getContainsQuery(SemanticConstants.THING_PREFIX, thingName);
        return executeAskOnInstanceModel(query);
    }

    private boolean executeAskOnInstanceModel(String query) {
        Model instanceModel = dataset.getNamedModel(SemanticConstants.GRAPH_NAME_INSTANCE);
        Query q = QueryFactory.create(query);
        QueryExecution qe = QueryExecutionFactory.create(q, instanceModel);
        boolean result = qe.execAsk();
        qe.close();
        return result;
    }

    private static String getContainsQuery(String prefix, String resourceName) {
        StringBuilder builder = new StringBuilder();
        builder.append("PREFIX dogont: <" + DogontSchema.NS + "> ");
        builder.append("PREFIX rdf: <" + SemanticConstants.NS_RDF_SYNTAX + "> ");
        builder.append("PREFIX instance: <" + SemanticConstants.NS_INSTANCE + "> ");
        builder.append("ASK  {  instance:" + prefix + resourceName + " rdf:type ?type . }");
        return builder.toString();
    }

    private static String getCopyStateAllQuery(String stateName, String id) {
        StringBuilder builder = new StringBuilder();
        builder.append("PREFIX dogont: <" + DogontSchema.NS + "> ");
        builder.append("PREFIX rdf: <" + SemanticConstants.NS_RDF_SYNTAX + "> ");
        builder.append("INSERT { GRAPH <" + SemanticConstants.GRAPH_NAME_INSTANCE + "> { ");
        builder.append("  ?newState rdf:type ?stateType ; ");
        builder.append("    dogont:hasStateValue [ ");
        builder.append("      rdf:type ?stateValueType; dogont:realStateValue ?realStateValue; ");
        builder.append("      dogont:unitOfMeasure ?unitOfMeasure  ] . ");
        builder.append("}} ");
        builder.append("WHERE { GRAPH <" + SemanticConstants.GRAPH_NAME_TEMPLATE + "> { ");
        builder.append("  ?state rdf:type ?stateType ; dogont:hasStateValue ?stateValue . ");
        builder.append("  ?stateValue rdf:type ?stateValueType . ");
        builder.append("  OPTIONAL {?stateValue dogont:realStateValue ?realStateValue. } ");
        builder.append("  OPTIONAL {?stateValue dogont:unitOfMeasure ?unitOfMeasure . } ");
        builder.append("  FILTER( ?state = <" + SemanticConstants.NS_AND_STATE_PREFIX_TEMPLATE + stateName + ">) ");
        builder.append("  BIND (URI(CONCAT (\"" + SemanticConstants.NS_INSTANCE + "\", ");
        builder.append("    STRAFTER (STR(?state),\"" + SemanticConstants.NS_TEMPLATE + "\"), \"_" + id
                + "\")) AS ?newState) ");
        builder.append("}}");
        return builder.toString();
    }

    private static String getCopyThingAndAddStateQuery(String stateName, String id) {
        StringBuilder builder = new StringBuilder();
        builder.append("PREFIX dogont: <" + DogontSchema.NS + "> ");
        builder.append("PREFIX rdf: <" + SemanticConstants.NS_RDF_SYNTAX + "> ");
        builder.append("INSERT { GRAPH <" + SemanticConstants.GRAPH_NAME_INSTANCE + "> { ");
        builder.append("  ?newThing rdf:type ?thingType ; dogont:hasState ?newState . ");
        builder.append("}} ");
        builder.append("WHERE { GRAPH <" + SemanticConstants.GRAPH_NAME_TEMPLATE + "> { ");
        builder.append("  ?thing dogont:hasState ?state; rdf:type ?thingType. ");
        builder.append("  FILTER( ?state = <" + SemanticConstants.NS_AND_STATE_PREFIX_TEMPLATE + stateName + ">) ");
        builder.append("  BIND (URI(CONCAT (\"" + SemanticConstants.NS_INSTANCE + "\", ");
        builder.append("    STRAFTER (STR(?thing),\"" + SemanticConstants.NS_TEMPLATE + "\"), \"_" + id
                + "\")) AS ?newThing) ");
        builder.append("  BIND (URI(CONCAT (\"" + SemanticConstants.NS_INSTANCE + "\", ");
        builder.append("    STRAFTER (STR(?state),\"" + SemanticConstants.NS_TEMPLATE + "\"), \"_" + id
                + "\")) AS ?newState) ");
        builder.append("}}");
        return builder.toString();
    }

    private static String getAddStateToThingQuery(String stateName, String thingName, String id) {
        StringBuilder builder = new StringBuilder();
        builder.append("PREFIX dogont: <" + DogontSchema.NS + "> ");
        builder.append("PREFIX instance: <" + SemanticConstants.NS_INSTANCE + "> ");
        builder.append("PREFIX rdf: <" + SemanticConstants.NS_RDF_SYNTAX + "> ");
        builder.append("INSERT { GRAPH <" + SemanticConstants.GRAPH_NAME_INSTANCE + "> { ");
        builder.append("  instance:Thing_" + thingName + "_" + id + " dogont:hasState ");
        builder.append("    instance:State_" + stateName + "_" + id + " . ");
        builder.append("}} WHERE {}");
        return builder.toString();
    }

    private static String getCopyAllCommandsQuery() {
        StringBuilder builder = new StringBuilder();
        builder.append("PREFIX dogont: <" + DogontSchema.NS + "> ");
        builder.append("PREFIX rdf: <" + SemanticConstants.NS_RDF_SYNTAX + "> ");
        builder.append("INSERT { GRAPH <" + SemanticConstants.GRAPH_NAME_INSTANCE + "> { ");
        builder.append("  ?newCommand rdf:type ?commandType; ");
        builder.append("    dogont:realCommandName ?realCommandName . ");
        builder.append("}}");
        builder.append("WHERE { GRAPH <" + SemanticConstants.GRAPH_NAME_TEMPLATE + "> { ");
        builder.append("  ?func dogont:hasCommand ?command .");
        builder.append("  ?command rdf:type ?commandType .");
        builder.append("  OPTIONAL {?command dogont:realCommandName ?realCommandName} .");
        builder.append("  BIND (URI(CONCAT (\"" + SemanticConstants.NS_INSTANCE + "\", ");
        builder.append("    STRAFTER (STR(?command),\"" + SemanticConstants.NS_TEMPLATE + "\"))) AS ?newCommand) ");
        builder.append("}}");
        return builder.toString();
    }

    private static String getCopyFunctionQuery(String functionName, String id) {
        StringBuilder builder = new StringBuilder();
        builder.append("PREFIX dogont: <" + DogontSchema.NS + "> ");
        builder.append("PREFIX rdf: <" + SemanticConstants.NS_RDF_SYNTAX + "> ");
        builder.append("INSERT { GRAPH <" + SemanticConstants.GRAPH_NAME_INSTANCE + "> { ");
        builder.append("  ?newFunc rdf:type ?funcType . ");
        builder.append("  ?newFunc dogont:hasCommand ?newCommand . ");
        builder.append("}}");
        builder.append("WHERE { GRAPH <" + SemanticConstants.GRAPH_NAME_TEMPLATE + "> { ");
        builder.append("  ?func rdf:type ?funcType .");
        builder.append("  OPTIONAL { ?func dogont:hasCommand ?command. } ");
        builder.append(
                "  FILTER( ?func = <" + SemanticConstants.NS_AND_FUNCTION_PREFIX_TEMPLATE + functionName + ">) ");
        builder.append("  BIND (URI(CONCAT (\"" + SemanticConstants.NS_INSTANCE + "\", ");
        builder.append(
                "    STRAFTER (STR(?func),\"" + SemanticConstants.NS_TEMPLATE + "\"), \"_" + id + "\")) AS ?newFunc) ");
        builder.append("  BIND (URI(CONCAT (\"" + SemanticConstants.NS_INSTANCE + "\", ");
        builder.append("    STRAFTER (STR(?command),\"" + SemanticConstants.NS_TEMPLATE + "\"))) AS ?newCommand) ");
        builder.append("}}");
        return builder.toString();
    }

    private static String getAddFunctionToThingQuery(String functionName, String thingName, String id) {
        StringBuilder builder = new StringBuilder();
        builder.append("PREFIX dogont: <" + DogontSchema.NS + "> ");
        builder.append("PREFIX instance: <" + SemanticConstants.NS_INSTANCE + "> ");
        builder.append("PREFIX rdf: <" + SemanticConstants.NS_RDF_SYNTAX + "> ");
        builder.append("INSERT { GRAPH <" + SemanticConstants.GRAPH_NAME_INSTANCE + "> { ");
        builder.append(
                "  instance:" + SemanticConstants.THING_PREFIX + thingName + "_" + id + " dogont:hasFunctionality ");
        builder.append("    instance:" + SemanticConstants.FUNCTION_PREFIX + functionName + "_" + id + " . ");
        builder.append("}} WHERE {}");
        return builder.toString();
    }

    private static String getCopyThingAndAddFunctionQuery(String functionName, String id) {
        StringBuilder builder = new StringBuilder();
        builder.append("PREFIX dogont: <" + DogontSchema.NS + "> ");
        builder.append("PREFIX rdf: <" + SemanticConstants.NS_RDF_SYNTAX + "> ");
        builder.append("INSERT { GRAPH <" + SemanticConstants.GRAPH_NAME_INSTANCE + "> { ");
        builder.append("  ?newThing rdf:type ?thingType ; dogont:hasFunctionality ?newFunc . ");
        builder.append("}} ");
        builder.append("WHERE { GRAPH <" + SemanticConstants.GRAPH_NAME_TEMPLATE + "> { ");
        builder.append("  ?thing dogont:hasFunctionality ?func; rdf:type ?thingType. ");
        builder.append(
                "  FILTER( ?func = <" + SemanticConstants.NS_AND_FUNCTION_PREFIX_TEMPLATE + functionName + ">) ");
        builder.append("  BIND (URI(CONCAT (\"" + SemanticConstants.NS_INSTANCE + "\", ");
        builder.append("    STRAFTER (STR(?thing),\"" + SemanticConstants.NS_TEMPLATE + "\"), \"_" + id
                + "\")) AS ?newThing) ");
        builder.append("  BIND (URI(CONCAT (\"" + SemanticConstants.NS_INSTANCE + "\", ");
        builder.append(
                "    STRAFTER (STR(?func),\"" + SemanticConstants.NS_TEMPLATE + "\"), \"_" + id + "\")) AS ?newFunc) ");
        builder.append("}}");
        return builder.toString();
    }

    private static String getCopyLocationsQuery2() {
        StringBuilder builder = new StringBuilder();
        builder.append("PREFIX dogont: <" + DogontSchema.NS + "> ");
        builder.append("PREFIX rdf: <" + SemanticConstants.NS_RDF_SYNTAX + "> ");
        builder.append("PREFIX rdfs: <" + SemanticConstants.NS_RDFS_SCHEMA + "> ");
        builder.append("INSERT { GRAPH <" + SemanticConstants.GRAPH_NAME_INSTANCE + "> { ");
        builder.append("  ?newLoc rdf:type ?class; rdfs:label ?realLoc . ");
        builder.append(" }}");
        builder.append("WHERE { GRAPH <" + SemanticConstants.GRAPH_NAME_TEMPLATE + "> { ");
        builder.append("  ?class rdfs:subClassOf* dogont:BuildingEnvironment . ");
        builder.append("  ?loc rdf:type ?class . ");
        builder.append("  ?loc rdfs:label ?realLoc . ");
        builder.append("   BIND(URI(CONCAT( \"" + SemanticConstants.NS_INSTANCE + "\" , STRAFTER(STR(?loc), \""
                + SemanticConstants.NS_TEMPLATE + "\"))) AS ?newLoc)");
        builder.append("}}");
        return builder.toString();
    }

    private static String getCopyLocationsQuery() {
        StringBuilder builder = new StringBuilder();
        builder.append("PREFIX dogont: <" + DogontSchema.NS + "> ");
        builder.append("PREFIX rdf: <" + SemanticConstants.NS_RDF_SYNTAX + "> ");
        builder.append("PREFIX rdfs: <" + SemanticConstants.NS_RDFS_SCHEMA + "> ");
        builder.append("INSERT {  ");
        builder.append("  ?newLoc rdf:type ?class; rdfs:label ?realLoc . ");
        builder.append(" }");
        builder.append("WHERE { ");
        builder.append("  ?class rdfs:subClassOf* dogont:BuildingEnvironment . ");
        builder.append("  ?loc rdf:type ?class . ");
        builder.append("  ?loc rdfs:label ?realLoc . ");
        builder.append("   BIND(URI(CONCAT( \"" + SemanticConstants.NS_INSTANCE + "\" , STRAFTER(STR(?loc), \""
                + SemanticConstants.NS_TEMPLATE + "\"))) AS ?newLoc)");
        builder.append("}");
        return builder.toString();
    }

}
