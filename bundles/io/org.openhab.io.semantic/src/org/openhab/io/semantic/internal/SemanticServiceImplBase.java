package org.openhab.io.semantic.internal;

import java.io.ByteArrayOutputStream;

import org.eclipse.smarthome.core.events.EventPublisher;
import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemNotFoundException;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.openhab.io.semantic.internal.ontology.DogontSchema;
import org.openhab.io.semantic.internal.util.QueryResource;
import org.openhab.io.semantic.internal.util.SemanticConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

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
	
	public void activate(){
		createModels();
		checkPresenceOfIndividuals();		
		logger.debug("Semantic Service activated");
	}
	
	public void deactivate(){
		logger.debug("Semantic Service deactivated");
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
		localName = localName.replaceFirst(SemanticConstants.STATE_PREFIX, "");
		return getItem(localName);
	}
	
	private void createModels() {
		Model modelInstances = ModelFactory.createDefaultModel();
		OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
		openHabInstancesModel = ModelFactory.createOntologyModel(spec, modelInstances);
		openHabInstancesModel.read(SemanticConstants.INSTANCE_FILE, SemanticConstants.TURTLE_STRING);
	}

	private void checkPresenceOfIndividuals() {
		Individual individual = null;
		String thingUid = null;
		for (Thing thing : thingRegistry.getAll()) {
			thingUid = thing.getThingTypeUID().getAsString();
			individual = openHabInstancesModel
					.getIndividual(SemanticConstants.NS_AND_THING_PREFIX + thingUid);
			if (individual == null) {
				logger.warn(
						"An instance of the thing with uid '{}' was not found in the semantic instance model.",
						thingUid);
				addSimpleThing(thing);
			}
		}
	}

	private void addSimpleThing(Thing thing) {
		// TODO generate a simple semantic annotation for items which have no model instance 
		// to complete the semantic model
	}

}
