package org.openhab.io.semantic.tests;

import org.openhab.io.semantic.core.SemanticService;
import org.openhab.io.semantic.core.util.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SemanticServiceTestImpl implements SemanticService {
    private static Logger logger = LoggerFactory.getLogger(SemanticServiceTestImpl.class);

    public void activate() {
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
    public void setAllValues() {

    }

    @Override
    public String getRestUrlForItem(String uid) {
        return null;
    }

    @Override
    public String getTypeName(String itemName) {
        return "default type name";
    }

    @Override
    public String getLocationName(String itemName) {
        return "default location name";
    }

    @Override
    public QueryResult getAllSensors() {
        return null;
    }

    @Override
    public boolean executeUpdate(String updateStmt) {
        // TODO Auto-generated method stub
        return false;
    }

}
