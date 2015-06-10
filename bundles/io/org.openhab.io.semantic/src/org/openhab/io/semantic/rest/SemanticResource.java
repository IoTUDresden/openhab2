package org.openhab.io.semantic.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.eclipse.smarthome.io.rest.RESTResource;
import org.openhab.io.semantic.core.SemanticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(SemanticResource.PATH_SEMANTIC)
public class SemanticResource implements RESTResource {
	private static final Logger logger = LoggerFactory.getLogger(SemanticResource.class);
	
	private SemanticService semanticService;
	private ThingRegistry thingRegistry;
	private ItemRegistry itemRegistry;
	
	public static final String PATH_SEMANTIC = "semantic";
	
	
	public void setSemanticService(SemanticService semanticService){
		this.semanticService = semanticService;
	}
	
	public void unsetSemanticService(){
		semanticService = null;
	}
	
	public void activate(){
		logger.debug("Semantic rest resource activated");
	}
	
	public void deactivate(){
		logger.debug("Semantic rest resource deactivated");
	}
	
	public void setThingRegistry(ThingRegistry thingRegistry) {
		this.thingRegistry = thingRegistry;
	}
	
	public void unsetThingRegistry(){
		thingRegistry = null;
	}
	
	public void setItemRegistry(ItemRegistry itemRegistry) {
		this.itemRegistry = itemRegistry;
	}
	
	public void unsetItemRegistry(){
		itemRegistry = null;
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getSemantic(){
		//select all from the semantic db
		return semanticService.getCurrentInstanceAsString();
	}
	
	@GET
	@Path("/select/{uid: [a-zA-Z_0-9]*}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getSemanticForUid(@PathParam("uid") String uid){
		return "recieved uid: " + uid;		
	}
	
	@GET
	@Path("/query")
	@Produces(MediaType.TEXT_PLAIN)
	public String query(@QueryParam("s") String query){
		return "query: param: " + query;
	}
	
	@GET
	@Path("/instance")
	@Produces(MediaType.TEXT_PLAIN)
	public String getInstanceSkeleton(){
		return semanticService.getCurrentInstanceAsString();
	}
	
	//TODO only for testing
	@Deprecated
	@GET
	@Path("/testvaluesetting")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCurrentValues(){
		semanticService.setAllValues();
		return semanticService.getCurrentInstanceAsString();
	}
	
	@GET
	@Path("/helper")
	@Produces(MediaType.TEXT_PLAIN)
	public String getSetupHelper(){
		ConfigHelper helper = new ConfigHelper();
		helper.addThingsAndItems(thingRegistry, itemRegistry);
		return helper.getAsString();
	}

}
