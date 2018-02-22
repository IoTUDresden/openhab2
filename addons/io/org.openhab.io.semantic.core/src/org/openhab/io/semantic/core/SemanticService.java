package org.openhab.io.semantic.core;

import org.openhab.io.semantic.core.util.QueryResult;

/**
 * The Semantic Service provides methods to access the things and items of openhab via semantic
 * annotation.
 *
 * @author André Kühnert
 *
 */
public interface SemanticService {

    /**
     * Executes a query, with the select statement
     *
     * @param queryAsString
     * @return null if queryAsString is null or empty
     */
    QueryResult executeSelect(String queryAsString);

    /**
     * Executes a query, with the select statement
     *
     * @deprecated withLatestValues is not needed anymore.
     *             Semantic is updated after state change event happend, and only for the specific item.
     *
     * @param queryAsString
     * @param withLatestValues
     *            If set to true, this will add the current values of all items to their specific
     *            stateValue in the ont model and then execute the query. This may take some time,
     *            so set this to true, only if you need the current values.
     * @return
     */
    @Deprecated
    QueryResult executeSelect(String queryAsString, boolean withLatestValues);

    /**
     * Executes an ask query
     *
     * @param askAsString
     * @return
     */
    boolean executeAsk(String askAsString);

    /**
     * Executes an ask query
     *
     * @deprecated withLatestValues is not needed anymore.
     *             Semantic is updated after state change event happend, and only for the specific item.
     *
     * @param askAsString
     * @param withLatestValues
     *            If set to true, this will add the current values of all items to their specific
     *            stateValue in the ont model and then execute the query. This may take some time,
     *            so set this to true, only if you need the current values.
     * @return
     */
    @Deprecated
    boolean executeAsk(String askAsString, boolean withLatestValues);

    /**
     * Executes a update statement
     *
     * @param updateStmt
     *            The update statement should also contain all prefixes, otherwise the execution will fail
     * @return true if execution was successful executed, otherwise false.
     */
    boolean executeUpdate(String updateStmt);

    /**
     * Sends a command to all items which are selected by the query. The query must contain a
     * variable, which holds the function of the specific item/thing. If the query contains no such
     * variable, than no command is send to the openhab event bus.
     *
     * @param command
     *            the command as String. e.g. 'ON', 'OFF', 'TOGGLE', 'DOWN', 'UP'
     * @param queryAsString
     * @return the result of the query
     */
    QueryResult sendCommand(String queryAsString, String command);

    /**
     * Sends a command to all items which are selected by the query. The query must contain a
     * variable, which holds the function of the specific item/thing. If the query contains no such
     * variable, than no command is send to the openhab event bus.
     *
     * @deprecated withLatestValues is not needed anymore.
     *             Semantic is updated after state change event happend, and only for the specific item.
     *
     * @param queryAsString
     * @param command
     * @param withLatestValues
     * @return the result of the query
     */
    @Deprecated
    QueryResult sendCommand(String queryAsString, String command, boolean withLatestValues);

    /**
     * Gets the Type of a given item.
     *
     * @param itemName
     *            OpenHab Item Name
     * @return
     */
    String getTypeName(String itemName);

    /**
     * Gets the location name of a given item.
     *
     * @param itemName
     *            OpenHab Item Name
     * @return
     */
    String getLocationName(String itemName);

    /**
     * Gets the current model instance as string
     *
     * @return
     */
    String getCurrentInstanceAsString();

    /**
     * Sets all current item states to the model.
     *
     * @deprecated this is not needed anymore.
     *             Semantic is updated after state change event happend, and only for the specific item.
     */
    @Deprecated
    void setAllValues();

    /**
     * Gets the OpenHab REST link for the given item, specified by its semantic model uid
     * (cause the uid in the semantic model and the uid for openhab can be different).
     *
     * @param uid
     * @return
     */
    String getRestUrlForItem(String uid);

    /**
     * Gets all Sensors.
     *
     * @return Vars: ?instance ?shortName ?typeName ?location(optional) ?thingName<br>
     *         - instance: the instance in the model <br>
     *         - shortName: the short name of the instance <br>
     *         - typeName: the name of the instance type <br>
     *         - location: the name of the location where the sensor is placed<br>
     *         - thingName: the name of the type which encapsulates the sensor
     */
    QueryResult getAllSensors();

}
