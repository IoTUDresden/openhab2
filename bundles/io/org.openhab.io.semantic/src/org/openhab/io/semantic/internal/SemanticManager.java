package org.openhab.io.semantic.internal;

import java.io.ByteArrayOutputStream;

import org.apache.jena.riot.RDFDataMgr;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.openhab.io.semantic.core.QueryResult;
import org.openhab.io.semantic.internal.util.SemanticConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * The SemanticManager handels all operations with the ontology models.
 * 
 * @author André Kühnert
 * 
 */
public class SemanticManager implements AutoCloseable {
	private static final Logger logger = LoggerFactory.getLogger(SemanticManager.class);

	private OntModel instanceSkeletonModel;
	private OntModel openHabInstancesModel;

	/**
	 * Default constructor.
	 * 
	 * Reads the semantic instances and checks if all things/items, which are added in openHAB, have
	 * an instance in the semantic model.
	 */
	public SemanticManager(ItemRegistry itemRegistry, ThingRegistry thingRegistry) {
		logger.debug("creating semantic models");
		createModels();
		checkPresenceOfIndividuals(thingRegistry);
	}

	/**
	 * Executes a query, with the select statement
	 * 
	 * @param queryAsString
	 * @return null if queryAsString is null or empty
	 */
	public QueryResult executeQuery(String queryAsString) {
		return executeQuery(queryAsString, true);
	}

	/**
	 * Executes a query, with the select statement
	 * 
	 * @param queryAsString
	 * @param withLatestValues
	 *            if set to true, this will fill the result set with the latest values for the devices
	 * @return
	 */
	public QueryResult executeQuery(String queryAsString, boolean withLatestValues) {
		if (queryAsString == null || queryAsString.isEmpty())
			return null;
		Query query = QueryFactory.create(queryAsString);
		QueryExecution qe = QueryExecutionFactory.create(query, openHabInstancesModel);
		ResultSet resultSet = qe.execSelect();
		if (withLatestValues)
			addCurrentValuesToResultSet(resultSet);
		QueryResult queryResult = new QueryResultImpl(resultSet);
		qe.close();
		return queryResult;
	}

	// TODO
	private void addCurrentValuesToResultSet(ResultSet resultSet) {
		QuerySolution querySolution = null;
		while (resultSet.hasNext()) {
			querySolution = resultSet.next();

		}

	}

	public String selectBuildingThing(String uid) {
		return null;
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
	 * Closes all models.
	 */
	public void close() {
		logger.debug("closing all models");
		instanceSkeletonModel.close();
		openHabInstancesModel.close();
	}

	private void createModels() {
		Model modelSkeleton = ModelFactory.createDefaultModel();
		Model modelInstances = ModelFactory.createDefaultModel();
		OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
		instanceSkeletonModel = ModelFactory.createOntologyModel(spec, modelSkeleton);
		openHabInstancesModel = ModelFactory.createOntologyModel(spec, modelInstances);

		// this reads the instance skeleton
		instanceSkeletonModel.read(SemanticConstants.INSTANCE_SKELETON, SemanticConstants.TURTLE_STRING);
		// this reads the empty instance - so the correct imports and base uri
		// is already set
		openHabInstancesModel.read(SemanticConstants.EMPTY_INSTANCE, SemanticConstants.TURTLE_STRING);
	}

	private void checkPresenceOfIndividuals(ThingRegistry thingRegistry) {
		Individual individual = null;
		String thingUid = null;
		for (Thing thing : thingRegistry.getAll()) {
			thingUid = thing.getThingTypeUID().getAsString();
			individual = openHabInstancesModel.getIndividual(SemanticConstants.NS_INSTANCE + thingUid);
			if (individual == null) {
				logger.warn(
						"An instance of the thing with uid '{}' was not found in the semantic instance model.",
						thingUid);
				addSimpleThing(thing);
			}
		}
	}

	private void addSimpleThing(Thing thing) {
		// TODO generate a simple semantic annotation
	}
}
