package org.openhab.io.semantic.core;

import org.eclipse.smarthome.core.items.Item;

/**
 * The Semantic Service provides methods to access the things and items of openhab via semantic annotation.
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
	
	QueryResult sendCommand(String command, String query);
	
	boolean addItem(Item item);
	
	boolean removeItem(Item item);
	
	boolean removeItem(String uid);
	
	String getRestUrlForItem(String uid);
	
	String getRestUrlsForItemsInJson(String query);
	
	/**
	 * Gets the current model instance as string
	 * @return
	 */
	String getCurrentInstanceAsString();
	
	String getInstanceSkeletonAsString();
	
	/**
	 * Sets all current item states to the model.
	 */
	@Deprecated
	void setAllValues();
}
