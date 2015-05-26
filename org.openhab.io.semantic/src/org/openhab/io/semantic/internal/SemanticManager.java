package org.openhab.io.semantic.internal;

import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.openhab.io.semantic.core.QueryResult;
import org.openhab.io.semantic.internal.util.SemanticConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class SemanticManager {
	private static final Logger logger = LoggerFactory.getLogger(SemanticManager.class);

	private OntModel structureModel;
	private OntModel instanceSkeletonModel;
	private OntModel openHabInstancesModel;

	
	/**
	 * Creates the semantic instances with items currently added to openHAB
	 * @param itemRegistry
	 */
	public void createInstancesModel(ItemRegistry itemRegistry, ThingRegistry thingRegistry){
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
	
	public void close(){
		instanceSkeletonModel.close();
		structureModel.close();
		openHabInstancesModel.close();
	}
	
	private void createModels(){
		Model model = ModelFactory.createDefaultModel();
		Model model2 = ModelFactory.createDefaultModel();
		OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
		structureModel = ModelFactory.createOntologyModel(spec, model);
		instanceSkeletonModel = ModelFactory.createOntologyModel(spec, model);
		openHabInstancesModel = ModelFactory.createOntologyModel(spec, model2);	
		
		// must load semiwa schema: can't read via http or via filemapper
		structureModel.read(SemanticConstants.STRUCTURE, SemanticConstants.TURTLE_STRING);	
		instanceSkeletonModel.read(SemanticConstants.INSTANCE_SKELLETON, SemanticConstants.TURTLE_STRING);
		openHabInstancesModel.read(SemanticConstants.EMPTY_INSTANCE, SemanticConstants.TURTLE_STRING);		
		structureModel.add(instanceSkeletonModel); //merged model in memory		
	}
	
	private void addThing(Thing thing){
		if(!thing.isLinked())
			return;
		
	}
	
	private void addItem(Item item){
		
		
	}
	
	private void removeItem(Item item){
		
	}
}
