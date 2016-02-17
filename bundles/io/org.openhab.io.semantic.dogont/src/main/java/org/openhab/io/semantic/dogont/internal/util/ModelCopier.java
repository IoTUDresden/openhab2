package org.openhab.io.semantic.dogont.internal.util;

import org.eclipse.smarthome.core.items.Item;
import org.openhab.io.semantic.dogont.internal.ontology.DogontSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;

/**
 * All functions need to be locked from the caller with WRITE! {@link OntModel#enterCriticalSection(boolean)} -
 * {@link Lock#WRITE}
 * This needs to be refactored if a reasoner is used on the template model.
 */
public class ModelCopier {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelCopier.class);
    private static final String ITEM_NAME_DELIMITER = "_";

    private Dataset dataset;

    /**
     * Templates are READ from the base model and WRITTEN to the target model.
     *
     * @param base
     * @param target
     */
    public ModelCopier(Dataset dataset) {
        this.dataset = dataset;
    }

    public void copyStateAndFunction(Item element) {
        String id = getLastDelimiter(element.getName());
        String templateName = removeLastDelimiter(element.getName());
        copyState(templateName, id);
        copyFunction(templateName, id);
    }

    /**
     * Copies the the state and all needed stuff from the template to the instance model.
     *
     * @param element
     */
    public void copyState(String templateName, String id) {
        LOGGER.debug("try to copy state '{}' from template", templateName);
        executeUpdateAction(getCopyStateQuery(templateName, id));
    }

    public void copyFunction(String templateName, String id) {
        // TODO

        // LOGGER.debug("try to copy function '{}' from template", templateName);
        // executeUpdateAction(getCopyFunctionQuery(templateName, id));
    }

    private void executeUpdateAction(String formatedQuery) {
        UpdateRequest req = UpdateFactory.create(formatedQuery);
        UpdateAction.execute(req, dataset);
        // TODO TDB sync or what to do that a loaded model gets the new values??
    }

    private static String getLastDelimiter(String name) {
        int lastInd = name.lastIndexOf(ITEM_NAME_DELIMITER);
        return name.substring(lastInd + 1);
    }

    private static String removeLastDelimiter(String name) {
        int lastInd = name.lastIndexOf(ITEM_NAME_DELIMITER);
        return name.substring(0, lastInd);
    }

    /**
     * Gets the copy state query as string to copy the thing and state.
     *
     * @param stateName
     *            state name in template
     * @param id
     *            unique id for the item
     * @return
     */
    private static String getCopyStateQuery(String stateName, String id) {
        // String builder should be faster than String.format(...)
        StringBuilder builder = new StringBuilder();
        builder.append("PREFIX dogont: <" + DogontSchema.NS + "> ");
        builder.append("PREFIX rdf: <" + SemanticConstants.NS_RDF_SYNTAX + "> ");
        builder.append("INSERT { GRAPH < " + SemanticConstants.GRAPH_NAME_INSTANCE + "> { ");
        builder.append("  ?newThing dogont:hasState ?newState . ");
        builder.append("  ?newThing rdf:type ?type . ");
        builder.append("  ?newState ?sp ?so . ");
        builder.append("  ?newState rdf:type ?stateType . ");
        builder.append("  ?so ?p ?o . ");
        builder.append("}} ");
        builder.append("WHERE { GRAPH <" + SemanticConstants.GRAPH_NAME_TEMPLATE + "> { ");
        builder.append("  ?thing dogont:hasState ?state . ");
        builder.append("  ?thing rdf:type ?type . ");
        builder.append("  ?state ?sp ?so . ");
        builder.append("  ?state rdf:type ?stateType . ");
        builder.append("  ?so ?p ?o . ");
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

    private static String getCopyFunctionQuery(String functionName, String id) {
        return "";
    }

}
