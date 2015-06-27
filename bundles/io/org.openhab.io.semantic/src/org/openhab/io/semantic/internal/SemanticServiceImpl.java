package org.openhab.io.semantic.internal;

import java.util.Iterator;

import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.library.items.RollershutterItem;
import org.eclipse.smarthome.core.library.items.SwitchItem;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.UpDownType;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.TypeParser;
import org.openhab.io.semantic.core.QueryResult;
import org.openhab.io.semantic.core.SemanticService;
import org.openhab.io.semantic.internal.util.QueryResource;
import org.openhab.io.semantic.internal.util.SemanticConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetRewindable;
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
		ResultSetRewindable rsw = ResultSetFactory.copyResults(rs);
		QueryResult qr = new QueryResultImpl(rsw);
		//after the rs is consumed, the iterator has no next element. The rs must be reset
//		rsw.reset();
		String varName = null;
		boolean isFirst = true;
		while (rsw.hasNext()) {
			QuerySolution qs = rsw.next();
			if (isFirst) {
				varName = getFunctionVarFromQuerySolution(qs);
				if (varName == null) {
					logger.error("No functions found under the varnames. No command is send. Check the query");
					qe.close();
					return qr;
				}
				isFirst = false;
			}
			postCommandToEventBus(qs, varName, command);
		}
		qe.close();
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
		// TODO read another model with the instances
		return getInstanceModelAsString();
	}

	@Override
	@Deprecated
	public void setAllValues() {
		addCurrentItemStatesToModelRealStateValues();
	}

	private void postCommandToEventBus(QuerySolution querySolution, String varName, String command) {
		RDFNode node = querySolution.get(varName);
		String localName = node.asResource().getLocalName();
		if (!localName.startsWith(SemanticConstants.FUNCTION_PREFIX)) {
			logger.error("Wrong name prefix. '{}' should be a function and start with '{}'", localName,
					SemanticConstants.FUNCTION_PREFIX);
			return;
		}
		localName = localName.replaceFirst(SemanticConstants.FUNCTION_PREFIX, "");
		Item item = getItem(localName);
		if (item == null) {
			logger.error("item with name '{}' not found.", localName);
			return;
		}
		Command cmd = getCommand(command, item);
		if(command == null){
			logger.error("command '{}' not found or not supported by the item '{}'", command, localName);
			return;
		}
		eventPublisher.postCommand(localName, cmd);
	}

	private Command getCommand(String value, Item item) {
		Command command = null;
		if ("toggle".equalsIgnoreCase(value)
				&& (item instanceof SwitchItem || item instanceof RollershutterItem)) {
			if (OnOffType.ON.equals(item.getStateAs(OnOffType.class)))
				command = OnOffType.OFF;
			if (OnOffType.OFF.equals(item.getStateAs(OnOffType.class)))
				command = OnOffType.ON;
			if (UpDownType.UP.equals(item.getStateAs(UpDownType.class)))
				command = UpDownType.DOWN;
			if (UpDownType.DOWN.equals(item.getStateAs(UpDownType.class)))
				command = UpDownType.UP;
		} else {
			command = TypeParser.parseCommand(item.getAcceptedCommandTypes(), value);
		}
		return command;
	}

	private String getFunctionVarFromQuerySolution(QuerySolution querySolution) {
		for (Iterator<String> iterator = querySolution.varNames(); iterator.hasNext();) {
			String varName = iterator.next();
			RDFNode node = querySolution.get(varName);
			if (!node.isResource())
				continue;
			String queryTmp = node.asResource().getLocalName();
			queryTmp = String.format(QueryResource.ResourceIsSubClassOfFunctionality, queryTmp);
			if (executeAsk(queryTmp))
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
