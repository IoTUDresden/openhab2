package org.openhab.io.semantic.core;

import org.eclipse.smarthome.core.items.Item;

public interface SemanticService {
	
	QueryResult executeQuery(String query);
	
	boolean addItem(Item item);
	
	boolean removeItem(Item item);
	
	boolean removeItem(String uid);
	
	QueryResult sendCommand(String command, String query);
	
	String getRestUrlForItem(String uid);
	
	String getRestUrlsForItemsInJson(String query);
	
	String getCurrentInstanceAsString();
	
	String getInstanceSkeletonAsString();
	
	//TODO only for testing
	@Deprecated
	void setAllValues();
}
