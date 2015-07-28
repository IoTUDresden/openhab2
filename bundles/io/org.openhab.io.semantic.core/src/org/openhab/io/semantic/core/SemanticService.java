package org.openhab.io.semantic.core;

import org.eclipse.smarthome.core.items.Item;

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
	 * @param askString
	 * @return
	 */
	boolean executeAsk(String askString);

	/**
	 * Executes an ask query
	 * 
	 * @param askString
	 * @param withLatestValues
	 *            If set to true, this will add the current values of all items to their specific
	 *            stateValue in the ont model and then execute the query. This may take some time,
	 *            so set this to true, only if you need the current values.
	 * @return
	 */
	boolean executeAsk(String askString, boolean withLatestValues);

	/**
	 * Sends a command to all items which are selected by the query. The query must contain a
	 * variable, which holds the function of the specific item/thing. If the query contains no such
	 * variable, than no command is send to the openhab event bus.
	 * 
	 * @param command
	 *            the command as String. e.g. 'ON', 'OFF', 'TOGGLE', 'DOWN', 'UP'
	 * @param query
	 * @return the result of the query
	 */
	QueryResult sendCommand(String query, String command);

	/**
	 * Sends a command to all items which are selected by the query. The query must contain a
	 * variable, which holds the function of the specific item/thing. If the query contains no such
	 * variable, than no command is send to the openhab event bus.
	 * 
	 * @param query
	 * @param command
	 * @param withLatestValues
	 * @return  the result of the query
	 */
	QueryResult sendCommand(String query, String command, boolean withLatestValues);

	/**
	 * Gets the current model instance as string
	 * 
	 * @return
	 */
	String getCurrentInstanceAsString();

	String getInstanceSkeletonAsString();

	/**
	 * Sets all current item states to the model.
	 */
	@Deprecated
	void setAllValues();
	
	@Deprecated
	boolean addItem(Item item);
	@Deprecated
	boolean removeItem(Item item);
	@Deprecated
	boolean removeItem(String uid);
	@Deprecated
	String getRestUrlForItem(String uid);
	@Deprecated
	String getRestUrlsForItemsInJson(String query);
}
