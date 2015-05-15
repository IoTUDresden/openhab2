package org.openhab.io.semantic.internal;

import org.eclipse.smarthome.core.events.EventPublisher;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.openhab.io.semantic.core.QueryResult;
import org.openhab.io.semantic.core.SemanticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SemanticServiceImpl implements SemanticService {
	private static final Logger logger = LoggerFactory.getLogger(SemanticServiceImpl.class);
	
	private ItemRegistry itemRegistry;
	private EventPublisher eventPublisher;
	

	@Override
	public QueryResult executeQuery() {
		// TODO Auto-generated method stub
		return null;
	}
	
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
	
	public void activate(){
		// TODO
		// register in rest
		// get the atmosphere stuff
		logger.debug("Semantic Service activated");
	}
	
	public void deactivate(){
		//TODO
		logger.debug("Semantic Service deactivated");
	}

}
