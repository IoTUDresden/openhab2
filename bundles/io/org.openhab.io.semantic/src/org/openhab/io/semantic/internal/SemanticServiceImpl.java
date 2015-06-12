package org.openhab.io.semantic.internal;

import java.util.Iterator;

import org.eclipse.smarthome.core.items.Item;
import org.openhab.io.semantic.core.QueryResult;
import org.openhab.io.semantic.core.SemanticService;
import org.openhab.io.semantic.internal.util.QueryResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * Implementation of the semantic service
 * 
 * @author André Kühnert
 *
 */
public class SemanticServiceImpl extends SemanticServiceImplBase implements SemanticService {
	private static final Logger logger = LoggerFactory.getLogger(SemanticServiceImpl.class);
	
	@Override
	public QueryResult executeSelect(String queryAsString) {		
		return executeSelect(queryAsString, false);
	}
	
	@Override
	public QueryResult executeSelect(String queryAsString, boolean withLatestValues) {
		logger.debug("received select: {}\nwith latest values: {}", queryAsString, withLatestValues);
		QueryExecution qe = getQueryExecution(queryAsString, withLatestValues);
		ResultSet resultSet = qe.execSelect();
		QueryResult queryResult = new QueryResultImpl(resultSet);
		qe.close();
		return queryResult;
	}
	
	@Override
	public boolean executeAsk(String askString, boolean withLatestValues) {
		logger.debug("received ask: {}\nwith latest values: {}", askString, withLatestValues);
		QueryExecution qe = getQueryExecution(askString, withLatestValues);
		if (qe == null)
			return false;
		return qe.execAsk();
	}

	@Override
	public boolean executeAsk(String askString) {
		return executeAsk(askString, false);
	}

	@Override
	public QueryResult sendCommand(String query, String command) {	
		return sendCommand(query, command, false);
	}
	
	@Override
	public QueryResult sendCommand(String query, String command, boolean withLatestValues) {
		logger.debug("trying to send command to items: command: {} query: {}", command, query);
		QueryExecution qe = getQueryExecution(query, withLatestValues);
		ResultSet rs = qe.execSelect();
		QueryResult qr = new QueryResultImpl(rs);
		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			String varName = getFunctionVarFromQuerySolution(qs);
			//TODO go on here
		}		
		return qr;
	}
	
	@Override
	public boolean addItem(Item item) {
		logger.debug("trying to add item to semantic resource: {}", item.getName());
		return false;
	}

	@Override
	public boolean removeItem(Item item) {
		logger.debug("trying to remove item from semantic resource: {}", item.getName());
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeItem(String uid) {
		logger.debug("trying to remove item from semantic resource: UID: {}", uid);
		return false;
	}

	@Override
	public String getRestUrlForItem(String uid) {
		logger.debug("get rest url for item with uid: {}", uid);
		return null;
	}

	@Override
	public String getRestUrlsForItemsInJson(String query) {
		logger.debug("get rest urls for items received with query: {}", query);
		return null;
	}

	@Override
	public String getCurrentInstanceAsString() {
		return getInstanceModelAsString();
	}

	@Override
	public String getInstanceSkeletonAsString() {
		//TODO read another model with the instances
		return getInstanceModelAsString();
	}

	@Override
	@Deprecated
	public void setAllValues() {
		addCurrentItemStatesToModelRealStateValues();		
	}
	
	private String getFunctionVarFromQuerySolution(QuerySolution querySolution){
		for (Iterator<String> iterator = querySolution.varNames(); iterator.hasNext();) {
			String varName = iterator.next();
			RDFNode node = querySolution.get(varName);
			if(!node.isResource())
				continue;
			String queryTmp = node.asResource().getLocalName();
			queryTmp = String.format(QueryResource.ResourceIsSubClassOfFunctionality, queryTmp);
			if(executeAsk(queryTmp))
				return varName;					
		}
		return null;
	}
	
	private QueryExecution getQueryExecution(String queryAsString, boolean withLatestValues) {
		if (queryAsString == null || queryAsString.isEmpty())
			return null;
		if (withLatestValues)
			addCurrentItemStatesToModelRealStateValues();
		Query query = QueryFactory.create(queryAsString);
		return QueryExecutionFactory.create(query, openHabInstancesModel);
	}
}
