package org.openhab.io.semantic.dogont.internal;

import java.io.ByteArrayOutputStream;

import org.eclipse.smarthome.core.common.registry.RegistryChangeListener;
import org.eclipse.smarthome.core.events.EventPublisher;
import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemNotFoundException;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.items.events.AbstractItemEventSubscriber;
import org.eclipse.smarthome.core.items.events.ItemStateEvent;
import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.openhab.io.semantic.dogont.internal.util.LocationMapperCustom;
import org.openhab.io.semantic.dogont.internal.util.ModelCopier;
import org.openhab.io.semantic.dogont.internal.util.QueryResource;
import org.openhab.io.semantic.dogont.internal.util.SchemaUtil;
import org.openhab.io.semantic.dogont.internal.util.SemanticConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.tdb.TDB;
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
public abstract class SemanticServiceImplBase extends AbstractItemEventSubscriber {
    private static final Logger logger = LoggerFactory.getLogger(SemanticServiceImplBase.class);
    private static final String OUTPUT_FORMAT = "RDF/XML-ABBREV";

    protected ItemRegistry itemRegistry;
    protected ThingRegistry thingRegistry;
    protected EventPublisher eventPublisher;

    protected OntModel openHabInstances;

    private Dataset openHabDataSet;
    private OntModel openHabTemplates;

    private ModelCopier modelCopier;

    public void setItemRegistry(ItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    public void unsetItemRegistry() {
        itemRegistry.removeRegistryChangeListener(itemListener);
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

    private RegistryChangeListener<Item> itemListener = new RegistryChangeListener<Item>() {

        @Override
        public void updated(Item oldElement, Item element) {
            // cheack name change
            logger.debug("item updated");

        }

        @Override
        public void removed(Item element) {
            // check if under the thing is one item left, else remove also the thing
            logger.debug("item removed");
        }

        @Override
        public void added(Item element) {
            // runs at every startup for all items

            if (!element.getName().startsWith("yahooweather_weather")) {
                return;
            }

            try {
                openHabDataSet.begin(ReadWrite.WRITE);
                modelCopier.copyStateAndFunction(element);
            } finally {
                openHabDataSet.end();
            }
            logger.debug("item added");
        }
    };

    /**
     * Activation method for the semantic service. This method is used by OSGI to activate this service.
     */
    public void activate() {
        LocationMapperCustom locationMapper = new LocationMapperCustom();
        LocationMapper.setGlobalLocationMapper(locationMapper);
        FileManager.get().setLocationMapper(locationMapper);

        // TODO automatic creation of resources

        createModels();
        modelCopier = new ModelCopier(openHabDataSet);
        itemRegistry.addRegistryChangeListener(itemListener);
        logger.debug("Dogont Semantic Service activated");
    }

    /**
     * Deactivation method for the semantic service. This method is used by OSGI to deactivate this service.
     */
    public void deactivate() {
        logger.debug("Dogont Semantic Service deactivated");
        itemRegistry.removeRegistryChangeListener(itemListener);
        openHabTemplates.close();
        openHabDataSet.close();
    }

    /**
     * Gets the complete instance model as an string
     *
     * @return
     */
    public String getInstanceModelAsString() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            openHabInstances.enterCriticalSection(Lock.READ);
            openHabInstances.write(out, OUTPUT_FORMAT, SemanticConstants.MODEL_NAME);
        } finally {
            openHabInstances.leaveCriticalSection();
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

    @Override
    protected void receiveUpdate(ItemStateEvent updateEvent) {
        String updateString = String.format(QueryResource.UpdateStateValue, updateEvent.getItemState().toString(),
                updateEvent.getItemName());
        UpdateRequest update = UpdateFactory.create(updateString);

        try {
            openHabInstances.enterCriticalSection(Lock.WRITE);
            openHabInstances.begin();
            UpdateAction.execute(update, openHabInstances);
            openHabInstances.commit();
            TDB.sync(openHabInstances);
        } finally {
            openHabInstances.leaveCriticalSection();
        }
    }

    protected boolean subjectExistsInModel(String subjectName, Model model) {
        String queryAsString = String.format(QueryResource.SubjectExistsInModel, subjectName);
        Query query = QueryFactory.create(queryAsString);
        QueryExecution qe = QueryExecutionFactory.create(query, openHabInstances);
        boolean result = qe.execAsk();
        qe.close();
        return result;
    }

    private void createModels() {
        openHabDataSet = TDBFactory.createDataset(SemanticConstants.TDB_PATH_BASE);
        Model modelTemplates = ModelFactory.createDefaultModel();
        OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
        openHabTemplates = ModelFactory.createOntologyModel(spec, modelTemplates);
        openHabTemplates.read(SemanticConstants.INSTANCE_FILE, SemanticConstants.TURTLE_STRING);

        boolean hasInstanceModel = openHabDataSet.containsNamedModel(SemanticConstants.MODEL_NAME);
        Model modelInstances = openHabDataSet.getNamedModel(SemanticConstants.MODEL_NAME);
        openHabInstances = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, modelInstances);
        if (!hasInstanceModel) {
            initCompleteNewModel();
        }
    }

    private void initCompleteNewModel() {
        try {
            openHabInstances.enterCriticalSection(Lock.WRITE);
            openHabInstances.begin();
            SchemaUtil.addRequiredNamespacePrefixToInstanceModel(openHabInstances);
            SchemaUtil.addOntologyInformation(openHabInstances);
            openHabInstances.commit();
            TDB.sync(openHabInstances);
        } finally {
            openHabInstances.leaveCriticalSection();
        }
    }
}
