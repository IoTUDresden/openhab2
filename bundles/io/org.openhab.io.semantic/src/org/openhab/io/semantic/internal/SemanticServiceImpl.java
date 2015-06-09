package org.openhab.io.semantic.internal;

import org.eclipse.smarthome.core.events.EventPublisher;
import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.openhab.io.semantic.core.QueryResult;
import org.openhab.io.semantic.core.SemanticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SemanticServiceImpl implements SemanticService {
	private static final Logger logger = LoggerFactory.getLogger(SemanticServiceImpl.class);
	
	private ItemRegistry itemRegistry;
	private ThingRegistry thingRegistry;
	private EventPublisher eventPublisher;
	private SemanticManager semanticManager;
	
	public void setItemRegistry(ItemRegistry itemRegistry){
		this.itemRegistry = itemRegistry;
	}
	
	public void unsetItemRegistry(){
		itemRegistry = null;		
	}
	
	public void setEventPublisher(EventPublisher eventPublisher){
		this.eventPublisher = eventPublisher;
	}
	
	public void unsetEventPublisher(){
		eventPublisher = null;
	}
	
	public void setThingRegistry(ThingRegistry thingRegistry){
		this.thingRegistry = thingRegistry;
	}
	
	public void unsetThingRegistry(){
		thingRegistry = null;
	}
	
	public void activate(){
		semanticManager = new SemanticManager(itemRegistry, thingRegistry);
		logger.debug("Semantic Service activated");
	}
	
	public void deactivate(){
		logger.debug("Semantic Service deactivated");
		semanticManager.close();
		semanticManager = null;
	}
	
	@Override
	public QueryResult executeQuery(String query) {
		logger.debug("recieved query: {}", query);
		return semanticManager.executeQuery(query);
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
	public QueryResult sendCommand(String command, String query) {
		logger.debug("trying to send command to items: command: {} query: {}", command, query);
		// TODO Auto-generated method stub
		
		
		return null;
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
		return semanticManager.getInstanceModelAsString();
	}

	@Override
	public String getInstanceSkeletonAsString() {
		return semanticManager.getInstanceModelAsString();
	}

}
