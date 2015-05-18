package org.openhab.io.semantic.internal;

import org.eclipse.smarthome.core.items.ItemRegistry;
import org.openhab.io.semantic.core.QueryResult;

import com.hp.hpl.jena.ontology.OntModel;

public class SemanticManager {
	private OntModel instanceSkelletonModel;
	private OntModel instancesModel;
	
	/**
	 * Creates the semantic instances with items currently added to openHAB
	 * @param itemRegistry
	 */
	public void createInstancesModel(ItemRegistry itemRegistry){
		
		//TODO
		
	}
	
	public QueryResult executeQuery(String query){
		return null;
	}
	


}
