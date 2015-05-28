package org.openhab.io.semantic.internal;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.openhab.io.semantic.core.QueryResult;
import org.openhab.io.semantic.internal.util.SchemaUtil;
import org.openhab.io.semantic.internal.util.SemanticConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class SemanticManager {
	private static final Logger logger = LoggerFactory.getLogger(SemanticManager.class);

	private OntModel instanceSkeletonModel;
	private OntModel openHabInstancesModel;

	
	/**
	 * Creates the semantic instances with items currently added to openHAB
	 * @param itemRegistry
	 */
	public void createInstancesModel(ItemRegistry itemRegistry, ThingRegistry thingRegistry){
		logger.debug("creating semantic models");
		createModels();
		for (Thing thing : thingRegistry.getAll())
			addThing(thing);

		//TODO add listener
//		itemRegistry.addRegistryChangeListener(listener);
//		thingRegistry.addRegistryChangeListener(listener);
	}
	
	public QueryResult executeQuery(String query){
		return null;
	}
	
	public String getInstanceModelAsString(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		openHabInstancesModel.write(out);
		return new String(out.toByteArray());
	}
	
	public void close(){
		logger.debug("closing all models");
		instanceSkeletonModel.close();
		openHabInstancesModel.close();
	}
	
	private void createModels(){
		Model modelSkeleton = ModelFactory.createDefaultModel();
		Model modelInstances = ModelFactory.createDefaultModel();
		OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
		instanceSkeletonModel = ModelFactory.createOntologyModel(spec, modelSkeleton);
		openHabInstancesModel = ModelFactory.createOntologyModel(spec, modelInstances);	
		
		//this reads the instance skeleton
		instanceSkeletonModel.read(SemanticConstants.INSTANCE_SKELETON, SemanticConstants.TURTLE_STRING);
		//this reads the empty instance - so the correct imports and base uri is already set
		openHabInstancesModel.read(SemanticConstants.EMPTY_INSTANCE, SemanticConstants.TURTLE_STRING);		
	}
	
	private void addThing(Thing thing){
		//offline or not linked things are not added
		if(!thing.isLinked() || thing.getStatus().equals(ThingStatus.OFFLINE))
			return;		
				
	}
	
	private void addItem(Item item){
		//e.g. GroupItem, SwitchItem
		item.getType();		
		//e.g. fingerscanner_identify_c6faf529
		item.getName();
	}
	
	private void removeItem(Item item){
		
	}
}
