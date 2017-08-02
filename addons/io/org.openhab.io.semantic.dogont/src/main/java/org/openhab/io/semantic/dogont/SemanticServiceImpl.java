package org.openhab.io.semantic.dogont;

import java.util.Iterator;

import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.events.ItemCommandEvent;
import org.eclipse.smarthome.core.items.events.ItemEventFactory;
import org.eclipse.smarthome.core.library.items.RollershutterItem;
import org.eclipse.smarthome.core.library.items.SwitchItem;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.UpDownType;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.TypeParser;
import org.openhab.io.semantic.core.SemanticService;
import org.openhab.io.semantic.core.util.QueryResult;
import org.openhab.io.semantic.dogont.internal.SemanticServiceImplBase;
import org.openhab.io.semantic.dogont.internal.util.QueryResource;
import org.openhab.io.semantic.dogont.internal.util.SemanticConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;

/**
 * Implementation of the semantic service, with the <a
 * href="http://lov.okfn.org/dataset/lov/vocabs/dogont">Dogont</a> Ontology.
 *
 * @author André Kühnert
 *
 */
public final class SemanticServiceImpl extends SemanticServiceImplBase implements SemanticService {
    private static final Logger logger = LoggerFactory.getLogger(SemanticServiceImpl.class);

    @Override
    public QueryResult executeSelect(String queryAsString) {
        return executeSelect(queryAsString, false);
    }

    @Override
    public QueryResult executeSelect(String queryAsString, boolean withLatestValues) {
        logger.debug("received select: {}\nwith latest values: {}", queryAsString, withLatestValues);
        QueryResult queryResult = null;
        try {
            openHabDataSet.begin(ReadWrite.READ);
            QueryExecution qe = getQueryExecution(queryAsString, withLatestValues);
            if (qe != null) {
                ResultSet resultSet = qe.execSelect();
                queryResult = new QueryResultImpl(resultSet);
                qe.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } finally {
            openHabDataSet.end();
        }
        return queryResult;
    }

    @Override
    public boolean executeAsk(String askAsString, boolean withLatestValues) {
        logger.debug("received ask: {}\nwith latest values: {}", askAsString, withLatestValues);
        boolean result = false;
        try {
            openHabDataSet.begin(ReadWrite.READ);
            result = executeAskPrivate(askAsString);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } finally {
            openHabDataSet.end();
        }
        return result;
    }

    @Override
    public boolean executeAsk(String askAsString) {
        return executeAsk(askAsString, false);
    }

    @Override
    public QueryResult sendCommand(String queryAsString, String command) {
        return sendCommand(queryAsString, command, false);
    }

    @Override
    public QueryResult sendCommand(String queryAsString, String command, boolean withLatestValues) {
        logger.debug("trying to send command to items: command: {} query: {}", command, queryAsString);
        QueryResult qr = null;
        try {
            openHabDataSet.begin(ReadWrite.READ);
            QueryExecution qe = getQueryExecution(queryAsString, withLatestValues);
            ResultSet rs = qe.execSelect();
            ResultSetRewindable rsw = ResultSetFactory.copyResults(rs);
            qr = new QueryResultImpl(rsw);
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
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } finally {
            openHabDataSet.end();
        }
        return qr;
    }

    @Override
    public String getRestUrlForItem(String uid) {
        logger.debug("get rest url for item with uid: {}", uid);
        return null;
    }

    @Override
    public String getCurrentInstanceAsString() {
        return getInstanceModelAsString();
    }

    @Override
    public void setAllValues() {

    }

    @Override
    public String getTypeName(String itemName) {
        logger.debug("get semantic type name for openhab item '{}'", itemName);
        return null;
    }

    /**
     * Gets the location name for an item, thing or state. If the param 'itemName' does not start
     * with on of these prefixes, it is tried to find a matching thing, functionality or state (in this order).
     *
     * @param itemName
     * @return null if no location was specified or the thing, state, func was not found.
     */
    @Override
    public String getLocationName(String itemName) {
        logger.debug("get semantic location name for item or thing '{}'", itemName);
        if (itemName.startsWith(SemanticConstants.THING_PREFIX)) {
            return getLocationRealname(QueryResource.LocationNameOfThing, itemName);
        }
        if (itemName.startsWith(SemanticConstants.FUNCTION_PREFIX)) {
            return getLocationRealname(QueryResource.LocationNameOfFunctionality, itemName);
        }
        if (itemName.startsWith(SemanticConstants.STATE_PREFIX)) {
            return getLocationRealname(QueryResource.LocationNameOfState, itemName);
        }

        String loc = getLocationName(SemanticConstants.THING_PREFIX + itemName);
        if (loc != null) {
            return loc;
        }
        loc = getLocationName(SemanticConstants.FUNCTION_PREFIX + itemName);
        if (loc != null) {
            return loc;
        }
        loc = getLocationName(SemanticConstants.STATE_PREFIX + itemName);
        return loc;
    }

    @Override
    public QueryResult getAllSensors() {
        QueryResult result = null;
        try {
            openHabDataSet.begin(ReadWrite.READ);
            QueryExecution queryExecution = getQueryExecution(QueryResource.AllSensors, false);
            ResultSet rs = queryExecution.execSelect();
            result = new QueryResultImpl(rs);
            queryExecution.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } finally {
            openHabDataSet.end();
        }
        return result;
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
        if (command == null) {
            logger.error("command '{}' not found or not supported by the item '{}'", command, localName);
            return;
        }
        ItemCommandEvent event = ItemEventFactory.createCommandEvent(localName, cmd);
        eventPublisher.post(event);
    }

    private Command getCommand(String value, Item item) {
        Command command = null;
        if ("toggle".equalsIgnoreCase(value) && (item instanceof SwitchItem || item instanceof RollershutterItem)) {
            if (OnOffType.ON.equals(item.getStateAs(OnOffType.class))) {
                command = OnOffType.OFF;
            }
            if (OnOffType.OFF.equals(item.getStateAs(OnOffType.class))) {
                command = OnOffType.ON;
            }
            if (UpDownType.UP.equals(item.getStateAs(UpDownType.class))) {
                command = UpDownType.DOWN;
            }
            if (UpDownType.DOWN.equals(item.getStateAs(UpDownType.class))) {
                command = UpDownType.UP;
            }
        } else {
            command = TypeParser.parseCommand(item.getAcceptedCommandTypes(), value);
        }
        return command;
    }

    private String getFunctionVarFromQuerySolution(QuerySolution querySolution) {
        for (Iterator<String> iterator = querySolution.varNames(); iterator.hasNext();) {
            String varName = iterator.next();
            RDFNode node = querySolution.get(varName);
            if (!node.isResource()) {
                continue;
            }
            String queryTmp = node.asResource().getLocalName();
            queryTmp = String.format(QueryResource.ResourceIsSubClassOfFunctionality, queryTmp);
            if (executeAskPrivate(queryTmp)) {
                return varName;
            }
        }
        return null;
    }

    // must be executed within a transaction
    private boolean executeAskPrivate(String query) {
        QueryExecution qe = getQueryExecution(query, false);
        boolean result = qe == null ? false : qe.execAsk();
        if (qe != null) {
            qe.close();
        }
        return result;
    }

    private QueryExecution getQueryExecution(String queryAsString, boolean withLatestValues) {
        if (queryAsString == null || queryAsString.isEmpty()) {
            return null;
        }
        Query query = QueryFactory.create(queryAsString);
        return QueryExecutionFactory.create(query, getInstanceModel());
    }

    private String getLocationRealname(String baseQueryString, String stateOrFunctionOrThingName) {
        Literal node = null;
        try {
            openHabDataSet.begin(ReadWrite.READ);
            String queryAsString = String.format(baseQueryString, stateOrFunctionOrThingName);
            QueryExecution query = getQueryExecution(queryAsString, false);
            ResultSet resultSet = query.execSelect();
            if (resultSet.hasNext()) {
                node = resultSet.next().getLiteral("realname");
            }
            query.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } finally {
            openHabDataSet.end();
        }
        return node == null ? null : node.getString();
    }

    @Override
    public boolean executeUpdate(String updateStmt) {
        boolean success = false;
        if (updateStmt == null) {
            logger.error("No update stmt. Update not executed.");
            return success;
        }

        try {
            logger.debug("execute update \n{}", updateStmt);
            openHabDataSet.begin(ReadWrite.WRITE);
            UpdateRequest req = UpdateFactory.create(updateStmt);
            UpdateAction.execute(req, getInstanceModel());
            openHabDataSet.commit();
            success = true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } finally {
            openHabDataSet.end();
        }

        return success;
    }
}
