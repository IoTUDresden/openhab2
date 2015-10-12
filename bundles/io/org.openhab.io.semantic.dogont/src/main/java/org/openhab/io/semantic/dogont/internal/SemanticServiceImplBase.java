package org.openhab.io.semantic.dogont.internal;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import org.eclipse.smarthome.core.events.EventPublisher;
import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemNotFoundException;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.openhab.io.semantic.dogont.internal.ontology.DogontSchema;
import org.openhab.io.semantic.dogont.internal.util.LocationMapperCustom;
import org.openhab.io.semantic.dogont.internal.util.QueryResource;
import org.openhab.io.semantic.dogont.internal.util.SemanticConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.LocationMapper;

/**
 * Base Class for the SemanticService Implementation
 * 
 * @author André Kühnert
 */
public class SemanticServiceImplBase {
	private static final Logger logger = LoggerFactory.getLogger(SemanticServiceImplBase.class);
	
	protected ItemRegistry itemRegistry;
	protected ThingRegistry thingRegistry;
	protected EventPublisher eventPublisher;
	
	protected OntModel openHabInstancesModel;
	
	
	public void setItemRegistry(ItemRegistry itemRegistry){
		this.itemRegistry = itemRegistry;
	}
	
	public void unsetItemRegistry(){
		itemRegistry = null;		
	}
	
	public void setEventPublisher(EventPublisher eventPublisher){
		this.eventPublisher = eventPublisher;
	}
	
	public void unsetEventPublisher(){
		eventPublisher = null;
	}
	
	public void setThingRegistry(ThingRegistry thingRegistry){
		this.thingRegistry = thingRegistry;
	}
	
	public void unsetThingRegistry(){
		thingRegistry = null;
	}
	
	/**
	 * Activation method for the semantic service. This method is used by OSGI to activate this service.
	 */
	public void activate(){
		LocationMapperCustom locationMapper = new LocationMapperCustom();
		LocationMapper.setGlobalLocationMapper(locationMapper);
		FileManager.get().setLocationMapper(locationMapper);	
		
		createModels();
		
		//for performance measurement
		createDummyInstances();
		
		//TODO remove not present instances
//		checkPresenceOfIndividuals();
		logger.debug("Dogont Semantic Service activated");
	}
	
	/**
	 * Deactivation method for the semantic service. This method is used by OSGI to deactivate this service.
	 */
	public void deactivate(){
		logger.debug("Dogont Semantic Service deactivated");
		openHabInstancesModel.close();
	}

	/**
	 * Gets the complete instance model as an string
	 * 
	 * @return
	 */
	public String getInstanceModelAsString() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		openHabInstancesModel.write(out);
		return new String(out.toByteArray());
	}

	/**
	 * Adds the current item states to their specific stateValues in the ont model.
	 */
	public void addCurrentItemStatesToModelRealStateValues() {
		Query query = QueryFactory.create(QueryResource.BuildingThingsContainingStateValue);
		QueryExecution qe = QueryExecutionFactory.create(query, openHabInstancesModel);
		ResultSet results = qe.execSelect();
		while (results.hasNext())
			addValueToModel(results.next());
		qe.close();
	}
	
	/**
	 * Gets the item from the item registry
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

	// TODO check if more than one states supported -> check the query
	private void addValueToModel(QuerySolution qs) {
		Resource state = qs.getResource("state");
		Resource value = qs.getResource("value");
		if (state == null || value == null)
			return;
		
		Item item = getItemWithModelStateLocalName(state.getLocalName());
		if (item == null)
			return;
		
		Statement stmt = value.getProperty(DogontSchema.realStateValue);
		if (stmt == null)
			return;
		RDFDatatype datatype = stmt.getLiteral().getDatatype();
		value.removeAll(DogontSchema.realStateValue);
		value.addProperty(DogontSchema.realStateValue, item.getState().toString(), datatype);
	}

	private Item getItemWithModelStateLocalName(String localName) {
		if (!localName.startsWith(SemanticConstants.STATE_PREFIX)) {
			logger.warn("the state '{}' does not have the correct name prefix", localName);
			return null;
		}
		
		//Hardcoded for performance measurement stuff
		if(localName.startsWith("State_DummySensor")){
			return getItem("IrTemp");
		}		
		
		localName = localName.replaceFirst(SemanticConstants.STATE_PREFIX, "");
		return getItem(localName);
	}
	
	private void createModels() {
		Model modelInstances = ModelFactory.createDefaultModel();
		OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
		openHabInstancesModel = ModelFactory.createOntologyModel(spec, modelInstances);
		openHabInstancesModel.read(SemanticConstants.INSTANCE_FILE, SemanticConstants.TURTLE_STRING);
	}

	@SuppressWarnings("unused")
	private void checkPresenceOfIndividuals() {
		for (Thing thing : thingRegistry.getAll()) {
			String thingUid = thing.getThingTypeUID().getAsString();
			Individual individual = openHabInstancesModel
					.getIndividual(SemanticConstants.NS_AND_THING_PREFIX + thingUid);
			if (individual == null) {
				logger.warn(
						"An instance of the thing with uid '{}' was not found in the semantic instance model.",
						thingUid);
				addSimpleThing(thing);
			}
		}
	}
	
	//creates dummy instances for performance tests
	private void createDummyInstances(){	
		for (int i = 0; i < 1e1; i++)
			createDummyTempSensor();
		logger.debug("Dummy Sensors Created");
		
		for (int i = 0; i < 1e1; i++)
			createDummyClass();
	}
	
	private void createDummyTempSensor() {
		String sensorUid = UUID.randomUUID().toString();
		OntClass sensorClass = openHabInstancesModel.getOntClass(DogontSchema.TemperatureSensor.getURI());
		Individual newSensorInstance = openHabInstancesModel
				.createIndividual(SemanticConstants.NS_INSTANCE + "DummySensor_" + sensorUid, sensorClass);

		OntClass stateClass = openHabInstancesModel.getOntClass(DogontSchema.TemperatureState.getURI());
		Individual stateInstance = openHabInstancesModel
				.createIndividual(SemanticConstants.NS_INSTANCE + "State_DummySensor_" + sensorUid, stateClass);
		newSensorInstance.addProperty(DogontSchema.hasState, stateInstance);
		
		OntClass stateValueClass = openHabInstancesModel.getOntClass(DogontSchema.TemperatureStateValue.getURI());
		Individual stateValueInstance = openHabInstancesModel.createIndividual(stateValueClass);
		stateInstance.addProperty(DogontSchema.hasStateValue, stateValueInstance);

		Literal literal = openHabInstancesModel.createTypedLiteral("2");
		stateValueInstance.addProperty(DogontSchema.realStateValue, literal);
	}
	
	private void createDummyClass(){
		String classUid = UUID.randomUUID().toString();
		OntClass newClass = openHabInstancesModel.createClass(SemanticConstants.NS_INSTANCE + "DummyClass_" + classUid);
		OntClass tempClass = openHabInstancesModel.getOntClass(DogontSchema.TemperatureSensor.getURI());		
		tempClass.addSubClass(newClass);
	}

	private void addSimpleThing(Thing thing) {
		// TODO generate a simple semantic annotation for items which have no model instance 
		// to complete the semantic model
	}

}
