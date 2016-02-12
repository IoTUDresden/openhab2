package org.openhab.io.semantic.dogont.internal;

import java.io.ByteArrayOutputStream;

import org.eclipse.smarthome.core.events.EventPublisher;
import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemNotFoundException;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.items.events.AbstractItemEventSubscriber;
import org.eclipse.smarthome.core.items.events.ItemStateEvent;
import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.openhab.io.semantic.dogont.internal.util.LocationMapperCustom;
import org.openhab.io.semantic.dogont.internal.util.QueryResource;
import org.openhab.io.semantic.dogont.internal.util.SemanticConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.LocationMapper;

/**
 * Base Class for the SemanticService Implementation
 *
 * @author André Kühnert
 */
public class SemanticServiceImplBase extends AbstractItemEventSubscriber {
    private static final Logger logger = LoggerFactory.getLogger(SemanticServiceImplBase.class);

    protected ItemRegistry itemRegistry;
    protected ThingRegistry thingRegistry;
    protected EventPublisher eventPublisher;

    // protected OntModel openHabInstancesModel;
    protected Dataset openHabDataSet;

    private boolean isReady = false;

    public void setItemRegistry(ItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    public void unsetItemRegistry() {
        itemRegistry = null;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void unsetEventPublisher() {
        eventPublisher = null;
    }

    public void setThingRegistry(ThingRegistry thingRegistry) {
        this.thingRegistry = thingRegistry;
    }

    public void unsetThingRegistry() {
        thingRegistry = null;
    }

    /**
     * Activation method for the semantic service. This method is used by OSGI to activate this service.
     */
    public void activate() {
        LocationMapperCustom locationMapper = new LocationMapperCustom();
        LocationMapper.setGlobalLocationMapper(locationMapper);
        FileManager.get().setLocationMapper(locationMapper);

        // TODO automatic creation of resources
        // TODO update state value if state change happens on items (maybe use sparql)
        // TODO make model modifications threadsafe
        // - https://jena.apache.org/documentation/notes/concurrency-howto.html
        // - maybe transactions are the better way to go
        // https://jena.apache.org/documentation/tdb/tdb_transactions.html#multi-threaded-use

        createModels();

        // for performance measurement
        // createDummyInstances();

        // TODO remove not present instances
        // checkPresenceOfIndividuals();
        isReady = true;
        logger.debug("Dogont Semantic Service activated");
    }

    /**
     * Deactivation method for the semantic service. This method is used by OSGI to deactivate this service.
     */
    public void deactivate() {
        logger.debug("Dogont Semantic Service deactivated");
    }

    /**
     * Gets the complete instance model as an string
     *
     * @return
     */
    public String getInstanceModelAsString() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        openHabDataSet.begin(ReadWrite.READ);
        try {
            openHabDataSet.getNamedModel(SemanticConstants.MODEL_NAME).write(out);
        } finally {
            openHabDataSet.end();
        }
        return new String(out.toByteArray());
    }

    /**
     * Adds the current item states to their specific stateValues in the ont model.
     *
     * @deprecated no need for this. values are up to date. semantic service listen to item state updates
     */
    @Deprecated
    public void addCurrentItemStatesToModelRealStateValues() {

    }

    /**
     * Gets the item from the item registry
     *
     * @param name
     * @return null, if not found
     */
    protected Item getItem(String name) {
        try {
            return itemRegistry.getItem(name);
        } catch (ItemNotFoundException e) {
            logger.error("Item with name '{}' not found. Wrong name in the instance model?", name);
            return null;
        }
    }

    private void createModels() {
        openHabDataSet = TDBFactory.createDataset(SemanticConstants.TDB_PATH_BASE);

        openHabDataSet.begin(ReadWrite.WRITE);
        if (!openHabDataSet.containsNamedModel(SemanticConstants.MODEL_NAME)) {
            try {
                Model modelInstances = ModelFactory.createDefaultModel();
                OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
                OntModel openHabInstancesModel = ModelFactory.createOntologyModel(spec, modelInstances);
                openHabInstancesModel.read(SemanticConstants.INSTANCE_FILE, SemanticConstants.TURTLE_STRING);
                openHabDataSet.addNamedModel(SemanticConstants.MODEL_NAME, openHabInstancesModel);
                openHabDataSet.commit();
                openHabInstancesModel.close();
            } finally {
                openHabDataSet.end();
            }
        } else {
            openHabDataSet.abort();
        }
    }

    @Override
    protected void receiveUpdate(ItemStateEvent updateEvent) {
        if (!isReady) {
            return;
        }
        String updateString = String.format(QueryResource.UpdateStateValue, updateEvent.getItemState().toString(),
                updateEvent.getItemName());
        UpdateRequest update = UpdateFactory.create(updateString);
        openHabDataSet.begin(ReadWrite.WRITE);
        try {
            UpdateAction.execute(update, getOpenHabNamedModel());
            openHabDataSet.commit();
        } finally {
            openHabDataSet.end();
        }
    }

    /**
     * Gets the {@link SemanticConstants#MODEL_NAME} named model from the dataset.
     * Due to thread safety this should be done within a {@link Dataset#begin(ReadWrite)} transaction.
     *
     * @return
     */
    protected Model getOpenHabNamedModel() {
        return openHabDataSet.getNamedModel(SemanticConstants.MODEL_NAME);
    }

}
