package org.openhab.io.semantic.dogont.internal;

import java.io.ByteArrayOutputStream;
import java.util.Random;

import org.eclipse.smarthome.core.common.registry.RegistryChangeListener;
import org.eclipse.smarthome.core.events.EventPublisher;
import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemNotFoundException;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.items.events.AbstractItemEventSubscriber;
import org.eclipse.smarthome.core.items.events.ItemStateEvent;
import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.openhab.io.semantic.dogont.internal.ontology.VicciExtensionSchema;
import org.openhab.io.semantic.dogont.internal.performance.ModelCopierForPerformanceTest;
import org.openhab.io.semantic.dogont.internal.performance.PerformanceItem;
import org.openhab.io.semantic.dogont.internal.performance.PerformanceItemEvent;
import org.openhab.io.semantic.dogont.internal.util.LocationMapperCustom;
import org.openhab.io.semantic.dogont.internal.util.QueryResource;
import org.openhab.io.semantic.dogont.internal.util.SchemaUtil;
import org.openhab.io.semantic.dogont.internal.util.SemanticConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntDocumentManager;
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
 * Base Class for the SemanticService Implementation.<br>
 * <br>
 *
 * TODO Is there a way to use a reasoner on a dataset?
 * At the moment always ontmodel with correct specs is needed.
 *
 * @author André Kühnert
 */
public abstract class SemanticServiceImplBase extends AbstractItemEventSubscriber {
    private static final Logger logger = LoggerFactory.getLogger(SemanticServiceImplBase.class);
    private static final String OUTPUT_FORMAT = "RDF/XML-ABBREV";

    protected ItemRegistry itemRegistry;
    protected ThingRegistry thingRegistry;
    protected EventPublisher eventPublisher;
    protected Dataset openHabDataSet;

    private ModelCopierForPerformanceTest modelCopier;

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
        }

        @Override
        public void removed(Item element) {
            // check if under the thing is one item left, else remove also the thing
        }

        @Override
        public void added(Item element) {
            // runs at every startup for all items
            try {
                openHabDataSet.begin(ReadWrite.WRITE);
                modelCopier.copyStateAndFunction(element);
                openHabDataSet.commit();
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
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

        // should working for imports
        OntDocumentManager.getInstance().addAltEntry(VicciExtensionSchema.BASE_URI,
                SemanticConstants.VICCI_EXTENSION_FILE);

        FileManager.get().setLocationMapper(locationMapper);

        createModels();

        itemRegistry.addRegistryChangeListener(itemListener);
        logger.debug("Dogont Semantic Service activated");
    }

    /**
     * Deactivation method for the semantic service. This method is used by OSGI to deactivate this service.
     */
    public void deactivate() {
        logger.debug("Dogont Semantic Service deactivated");
        itemRegistry.removeRegistryChangeListener(itemListener);
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
            openHabDataSet.begin(ReadWrite.READ);
            getInstanceModel().write(out, OUTPUT_FORMAT, SemanticConstants.GRAPH_NAME_INSTANCE);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
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

    @Override
    protected void receiveUpdate(ItemStateEvent updateEvent) {
        String updateString = String.format(QueryResource.UpdateStateValue, updateEvent.getItemState().toString(),
                updateEvent.getItemName());
        UpdateRequest update = UpdateFactory.create(updateString);

        try {
            openHabDataSet.begin(ReadWrite.WRITE);
            UpdateAction.execute(update, getInstanceModel());
            openHabDataSet.commit();
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } finally {
            openHabDataSet.end();
        }
    }

    /**
     * Get and use the instance model only within dataset transactions!
     *
     * @return
     */
    protected Model getInstanceModel() {
        // TODO how is performance of this?
        // if i just get the named model, i will not have any reasoning -> subClassOf* will not work
        Model modelInstances = openHabDataSet.getNamedModel(SemanticConstants.GRAPH_NAME_INSTANCE);
        return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_TRANS_INF, modelInstances);
    }

    private void createModels() {
        openHabDataSet = TDBFactory.createDataset(SemanticConstants.TDB_PATH_BASE);
        modelCopier = new ModelCopierForPerformanceTest(openHabDataSet);
        if (!openHabDataSet.containsNamedModel(SemanticConstants.GRAPH_NAME_INSTANCE)) {
            try {
                openHabDataSet.begin(ReadWrite.WRITE);
                Model modelInstances = openHabDataSet.getNamedModel(SemanticConstants.GRAPH_NAME_INSTANCE);
                Model modelTemplates = openHabDataSet.getNamedModel(SemanticConstants.GRAPH_NAME_TEMPLATE);

                OntModel openHabInstances = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_TRANS_INF,
                        modelInstances);
                OntModel openHabTemplates = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_TRANS_INF,
                        modelTemplates);
                openHabTemplates.read(SemanticConstants.TEMPLATE_FILE, SemanticConstants.TURTLE_STRING);
                SchemaUtil.addRequiredNamespacePrefixToInstanceModel(openHabInstances);
                SchemaUtil.addOntologyInformation(openHabInstances);

                openHabInstances.addSubModel(openHabTemplates);
                modelCopier.copyCommands();
                // merged (template + instance) ontmodel needed here because reasoner is needed
                modelCopier.copyLocations(openHabInstances);
                openHabDataSet.commit();
            } finally {
                openHabDataSet.end();
            }
        }
    }

    private static final int maxLightSensors = 10;
    private static final int maxDimmerSwitches = 10;
    private static final int ambLightStartNumber = 5;
    private static final int dimmerStartNumber = 5;
    private static final String ambLightName = "tinkerforge_ambientLight_ambientLight_gen";
    private static final String dimmerName = "homematic_dimmer_dimmer_gen";
    // private static final String livingRoomName = "LivingRoom1";

    @SuppressWarnings("unused")
    private void copyThingsForPerformanceTest() {
        // modelCopier.addLivingRoomWithName("Wohnzimmer", livingRoomName);

        for (int i = ambLightStartNumber; i < maxLightSensors + ambLightStartNumber; i++) {
            itemListener.added(new PerformanceItem(ambLightName + i));
            // if (i == maxLightSensors) {
            // modelCopier.updateLocationForThing(SemanticConstants.THING_PREFIX + ambLightName + i, livingRoomName);
            // }
        }

        for (int i = dimmerStartNumber; i < maxDimmerSwitches + dimmerStartNumber; i++) {
            itemListener.added(new PerformanceItem(dimmerName + i));
            // if (i == maxDimmerSwitches) {
            // modelCopier.updateLocationForThing(SemanticConstants.THING_PREFIX + dimmerName + 1, livingRoomName);
            // }
        }

        // pushes values in Background
        Thread pushLightThread = new Thread(pushLightValues);
        pushLightThread.setDaemon(true);
        pushLightThread.start();
    }

    private Runnable pushLightValues = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(200);
                    updateAllLightSensors();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void updateAllLightSensors() {
        for (int i = ambLightStartNumber; i < maxLightSensors + ambLightStartNumber; i++) {
            String name = ambLightName + i;
            String value = getRandLightValueAsString();
            receiveUpdate(new PerformanceItemEvent(name, value));
        }
    }

    private String getRandLightValueAsString() {
        float minLight = 289.0f;
        float maxLight = 291.0f;
        Random rand = new Random();
        float finalLight = rand.nextFloat() * (maxLight - minLight) + minLight;
        return String.valueOf(finalLight);
    }
}
