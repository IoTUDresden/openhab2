package org.openhab.io.semantic.internal;

import org.eclipse.smarthome.core.items.ItemRegistry;
import org.openhab.io.semantic.core.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.tdb.TDBFactory;

public class SemanticManager {
	private static final Logger logger = LoggerFactory.getLogger(SemanticManager.class);
	private static final String BASE_MODEL_PATH = "ontology";
	private static final String INSTANCE_SKELLETON_PATH = "instance_skeleton.ttl";
	private static final String TDB_PATH = "userData/semantic/database";
	
	private OntModel instanceSkelletonModel;
	private OntModel instancesModel;
	private Dataset storage;
	
	
	public void initTDB(){
		// generates new if not exists
		storage = TDBFactory.createDataset(TDB_PATH);
	}
	
	/**
	 * Creates the semantic instances with items currently added to openHAB
	 * @param itemRegistry
	 */
	public void createInstancesModel(ItemRegistry itemRegistry){
		OntModelSpec s = new OntModelSpec(OntModelSpec.OWL_MEM);
		
		storage.begin(ReadWrite.READ);
		try {			
			Model defaultModel = storage.getDefaultModel();
			instanceSkelletonModel = ModelFactory.createOntologyModel(s, ModelFactory.createFileModelMaker(BASE_MODEL_PATH + "/" + INSTANCE_SKELLETON_PATH), defaultModel);
			
		} catch (Exception e) {
			logger.error("error while creating semantic model");
			return;
		} finally{
			storage.end();			
		}
		
		
		

		
	}
	
	public QueryResult executeQuery(String query){
		return null;
	}
	


}
