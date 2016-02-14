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
import org.openhab.io.semantic.dogont.internal.ontology.DogontSchema;
import org.openhab.io.semantic.dogont.internal.util.LocationMapperCustom;
import org.openhab.io.semantic.dogont.internal.util.QueryResource;
import org.openhab.io.semantic.dogont.internal.util.SchemaUtil;
import org.openhab.io.semantic.dogont.internal.util.SemanticConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
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
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
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
    private static final String ITEM_NAME_DELIMITER = "_";

    protected ItemRegistry itemRegistry;
    protected ThingRegistry thingRegistry;
    protected EventPublisher eventPublisher;

    protected OntModel openHabInstances;

    private Dataset openHabDataSet;
    private OntModel openHabTemplates;
    private boolean isReady = false;

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

            if (itemExistsInSemanticModel(element)) {
                return;
            }

            String itemId = getLastDelimiter(element.getName());
            String itemNameWithoutId = removeLastDelimiter(element.getName());
            String thingName = removeLastDelimiter(itemNameWithoutId);
            String newThingName = thingName.concat(ITEM_NAME_DELIMITER).concat(itemId);

            Individual thingToCopy = getExistingIndividual(SemanticConstants.NS_AND_THING_PREFIX, thingName,
                    openHabTemplates);
            Individual thingInstance = getExistingIndividual(SemanticConstants.NS_AND_THING_PREFIX, newThingName,
                    openHabInstances);

            if (thingToCopy == null && thingInstance == null) {
                logger.error("cant add item with name '{}' cause no semantic template was found", element.getName());
                return;
            }

            if (thingInstance == null && thingToCopy != null) {
                thingInstance = copyThingTemplateToInstance(thingToCopy, newThingName);
            }

            copyStateAndFunction(thingToCopy, thingInstance, itemNameWithoutId, element.getName());

            logger.debug("item added");
        }
    };

    private Individual copyThingTemplateToInstance(Individual thingToCopy, String newName) {
        Individual individual = null;
        try {
            openHabInstances.enterCriticalSection(Lock.WRITE);
            // openHabInstances.begin();
            OntClass clazz = openHabInstances.getOntClass(thingToCopy.getOntClass().getURI());
            individual = clazz.createIndividual(SemanticConstants.NS_AND_THING_PREFIX.concat(newName));
            openHabInstances.commit();
            TDB.sync(openHabInstances);
            logger.debug("added thing to semantic: '{}'", newName);
        } finally {
            openHabInstances.leaveCriticalSection();
        }
        return individual;
    }

    private void copyStateAndFunction(Individual thingToCopy, Individual thingInstance, String itemNameWithoutId,
            String itemName) {
        try {
            openHabInstances.enterCriticalSection(Lock.WRITE);
            // openHabInstances.begin();
            String withState = SemanticConstants.STATE_PREFIX.concat(itemNameWithoutId);
            String withFunction = SemanticConstants.FUNCTION_PREFIX.concat(itemNameWithoutId);
            NodeIterator iterator = thingToCopy.listPropertyValues(DogontSchema.hasState);
            Individual state = null;
            while (iterator.hasNext()) {
                RDFNode rdfNode = iterator.next();
                String localName = rdfNode.asResource().getLocalName();
                if (localName.equals(withState)) {
                    String uri = rdfNode.asResource().getURI();
                    state = openHabTemplates.getIndividual(uri);
                    break;
                }
            }

            // thing has no state
            if (state == null) {
                return;
            }

            OntClass clazz = openHabInstances.getOntClass(state.getOntClass().getURI());
            Individual stateToCopy = clazz.createIndividual(SemanticConstants.NS_AND_STATE_PREFIX.concat(itemName));
            thingInstance.addProperty(DogontSchema.hasState, stateToCopy);

            openHabInstances.commit();
            TDB.sync(openHabInstances);
        } finally {
            openHabInstances.leaveCriticalSection();
        }

    }

    private boolean itemExistsInSemanticModel(Item element) {
        Individual individual = getExistingIndividual(SemanticConstants.NS_AND_STATE_PREFIX, element.getName(),
                openHabInstances);
        if (individual != null) {
            return true;
        }
        individual = getExistingIndividual(SemanticConstants.NS_AND_FUNCTION_PREFIX, element.getName(),
                openHabInstances);
        if (individual != null) {
            return true;
        }
        return false;
    }

    private static String getLastDelimiter(String name) {
        int lastInd = name.lastIndexOf(ITEM_NAME_DELIMITER);
        return name.substring(lastInd + 1);
    }

    private static String removeLastDelimiter(String name) {
        int lastInd = name.lastIndexOf(ITEM_NAME_DELIMITER);
        return name.substring(0, lastInd);
    }

    private static Individual getExistingIndividual(String prefix, String name, OntModel model) {
        String uri = prefix.concat(name);
        Individual individual = model.getIndividual(uri);
        return individual;
    }

    /**
     * Activation method for the semantic service. This method is used by OSGI to activate this service.
     */
    public void activate() {
        if (isReady) {
            return;
        }
        LocationMapperCustom locationMapper = new LocationMapperCustom();
        LocationMapper.setGlobalLocationMapper(locationMapper);
        FileManager.get().setLocationMapper(locationMapper);

        // TODO automatic creation of resources

        createModels();
        itemRegistry.addRegistryChangeListener(itemListener);

        isReady = true;
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
        openHabDataSet.begin(ReadWrite.READ);
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
        if (!isReady) {
            return;
        }
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
