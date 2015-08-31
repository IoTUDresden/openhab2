package org.openhab.io.semantic.tests;

import org.eclipse.smarthome.core.items.Item;
import org.openhab.io.semantic.core.QueryResult;
import org.openhab.io.semantic.core.SemanticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SemanticServiceTestImpl implements SemanticService {
	private static Logger logger = LoggerFactory.getLogger(SemanticServiceTestImpl.class);
	
	public void activate(){
		logger.debug("Test Impl of semantic service activated");		
	}
	
	public void deactivate() {
		logger.debug("Test Impl of semantic service deactivated");	
	}

	@Override
	public QueryResult executeSelect(String queryAsString) {
		return new QueryResultTestImpl(queryAsString);
	}

	@Override
	public QueryResult executeSelect(String queryAsString, boolean withLatestValues) {
		return new QueryResultTestImpl(queryAsString);
	}

	@Override
	public boolean executeAsk(String askAsString) {
		return true;
	}

	@Override
	public boolean executeAsk(String askAsString, boolean withLatestValues) {
		return true;
	}

	@Override
	public QueryResult sendCommand(String queryAsString, String command) {
		return new QueryResultTestImpl(queryAsString);
	}

	@Override
	public QueryResult sendCommand(String queryAsString, String command, boolean withLatestValues) {
		return new QueryResultTestImpl(queryAsString);
	}

	@Override
	public String getCurrentInstanceAsString() {
		return "static test instance";
	}

	@Override
	public String getInstanceSkeletonAsString() {
		return "static test instance";
	}

	@Override
	public void setAllValues() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean removeItem(String uid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getRestUrlForItem(String uid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRestUrlsForItemsInJson(String query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addItem(Item item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeItem(Item item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getTypeName(String itemName) {
		return "default type name";
	}

	@Override
	public String getLocationName(String itemName) {
		return "default location name";
	}

}
