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
    }

    /**
     * Copies the the state and all needed stuff from the template to the instance model.
     * @param element
     */
    public void copyState(Item element){
        String id = getLastDelimiter(element.getName());
        String templateName = removeLastDelimiter(element.getName());
        LOGGER.debug("try to copy state '{}' from template", templateName);

        String query = String.format(COPY_STATE, templateName, id, id);
        UpdateRequest req = UpdateFactory.create(query);
        UpdateAction.execute(req, dataset);
        //TODO TDB sync or what??
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
     * Copy the thing and state.
     * Use String.format Template State name (without namespace and State_ prefix), new thing id, new state id
     */
    private static final String COPY_STATE = ""
            +   "PREFIX dogont: <" + DogontSchema.NS +"> "
            +   "PREFIX rdf: <" + SemanticConstants.NS_RDF_SYNTAX +"> "
            +   "INSERT { "
            +   "GRAPH <" + SemanticConstants.GRAPH_NAME_INSTANCE + "> { "
            +   "   ?newThing dogont:hasState ?newState . "
            +   "   ?newThing rdf:type ?type . "
            +   "   ?newState ?sp ?so . "
            +   "   ?newState rdf:type ?stateType . "
            +   "   ?so ?p ?o . "
            + " } "
            +"} "
            +"WHERE { "
            +"  GRAPH <" + SemanticConstants.GRAPH_NAME_TEMPLATE + "> { "
            +"      ?thing dogont:hasState ?state . "
            +"      ?thing rdf:type ?type . "
            +"      ?state ?sp ?so . "
            +"      ?state rdf:type ?stateType . "
            +"      ?so ?p ?o . "
            +"      FILTER( ?state = "+ SemanticConstants.NS_AND_STATE_PREFIX_TEMPLATE + "%s ) "
            +"      BIND (URI(CONCAT (\"" + SemanticConstants.NS_INSTANCE + "\", STRAFTER (STR(?thing),\"" + SemanticConstants.NS_TEMPLATE + "\"),\"_%s\")) AS ?newThing) "
            +"      BIND (URI(CONCAT (\"" + SemanticConstants.NS_INSTANCE + "\", STRAFTER (STR(?state),\"" + SemanticConstants.NS_TEMPLATE + "\"),\"_%s\")) AS ?newState) "
            +"  } "
            +"} ";

}
