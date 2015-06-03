package org.openhab.io.semantic.internal;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.apache.commons.lang3.text.WordUtils;
import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.UID;
import org.openhab.io.semantic.core.QueryResult;
import org.openhab.io.semantic.internal.util.QueryUtil;
import org.openhab.io.semantic.internal.util.SchemaUtil;
import org.openhab.io.semantic.internal.util.SemanticConstants;
import org.openhab.io.semantic.ontology.DogontSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.impl.IndividualImpl;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.tdb.TDBFactory;

public class SemanticManager {
	private static final Logger logger = LoggerFactory.getLogger(SemanticManager.class);

	private OntModel instanceSkeletonModel;
	private OntModel openHabInstancesModel;

	public SemanticManager() {

	}
	
	/**
	 * Creates the semantic instances with items currently added to openHAB
	 * @param itemRegistry
	 */
	public void createInstancesModel(ItemRegistry itemRegistry, ThingRegistry thingRegistry){
		logger.debug("creating semantic models");
		createModels();
		
		//TODO automatic creation from a skeleton
//		for (Thing thing : thingRegistry.getAll())
//			addThing(thing);

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
	
	private void add(Thing thing){
		String uid = getThingUidCamelCased(thing);
		if(uid == null)
			return;
		uid = SemanticConstants.NS_INSTANCE + uid;
		OntClass ontClass = openHabInstancesModel.getOntClass(uid);
		//functionalities
		//commands
		//state
		//statevalue
		//http://stackoverflow.com/questions/10986472/list-object-properties-from-a-instance-in-jena
		
	}
	
	//TODO automatic creation from a skeleton
	private void addThing(Thing thing){
		Individual skeleton = getIndividualSkeleton(thing);	
		if(skeleton == null){
			logger.warn("To the thing with uid '{}' was no individual found in the skeleton. "
					+ "The thing is not added to the semantic model", 
					thing.getThingTypeUID().getAsString());
			return;
		}		
	}
	
	private String getThingUidCamelCased(Thing thing){
		if(ThingTypeUID.SEPARATOR.length() > 1){
			//the code on this point must be fixed, if this happens
			logger.error("The thingtypeuid separator contains more than one char.");
			return null;
		}			
		String uid = WordUtils.capitalizeFully(thing.getThingTypeUID().getAsString(), 
				new char[]{ThingTypeUID.SEPARATOR.charAt(0)});
		uid = uid.replace(ThingTypeUID.SEPARATOR, "");
		return uid;
	}

	private Individual getIndividualSkeleton(Thing thing){
		String search = getThingUidCamelCased(thing);
		if(search == null)
			return null;
		search = SemanticConstants.NS_INSTANCE + search; 
		return instanceSkeletonModel.getIndividual(search);
	}
}
