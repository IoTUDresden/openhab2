package org.openhab.io.semantic.core;


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
	 * @param queryAsString
	 * @param withLatestValues
	 *            If set to true, this will add the current values of all items to their specific
	 *            stateValue in the ont model and then execute the query. This may take some time,
	 *            so set this to true, only if you need the current values.
	 * @return
	 */
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
	 * @param askAsString
	 * @param withLatestValues
	 *            If set to true, this will add the current values of all items to their specific
	 *            stateValue in the ont model and then execute the query. This may take some time,
	 *            so set this to true, only if you need the current values.
	 * @return
	 */
	boolean executeAsk(String askAsString, boolean withLatestValues);

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
	 * @param queryAsString
	 * @param command
	 * @param withLatestValues
	 * @return the result of the query
	 */
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
	 */
	void setAllValues();

	/**
	 * Gets the OpenHab REST link for the given item, specified by its semantic model uid
	 * (cause the uid in the semantic model and the uid for openhab can be different).
	 * @param uid
	 * @return
	 */
	String getRestUrlForItem(String uid);
}
