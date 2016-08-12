package org.openhab.io.semantic.core.rest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.jena.atlas.json.JsonNull;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.eclipse.smarthome.io.rest.RESTResource;
import org.openhab.io.semantic.core.SemanticService;
import org.openhab.io.semantic.core.internal.util.ConfigHelper;
import org.openhab.io.semantic.core.internal.util.SemanticPostCommandBean;
import org.openhab.io.semantic.core.util.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Path(SemanticResource.PATH_SEMANTIC)
@Api(value = SemanticResource.PATH_SEMANTIC)
public class SemanticResource implements RESTResource {
    private static final Logger logger = LoggerFactory.getLogger(SemanticResource.class);
    private static final String JSON_BOOLEAN_FORMAT = "{ \"result\":%s }";

    private SemanticService semanticService;
    private ThingRegistry thingRegistry;
    private ItemRegistry itemRegistry;

    public static final String PATH_SEMANTIC = "semantic";

    public void setSemanticService(SemanticService semanticService) {
        this.semanticService = semanticService;
    }

    public void unsetSemanticService() {
        semanticService = null;
    }

    public void activate() {
        logger.debug("Semantic rest resource activated");
    }

    public void deactivate() {
        logger.debug("Semantic rest resource deactivated");
    }

    public void setThingRegistry(ThingRegistry thingRegistry) {
        this.thingRegistry = thingRegistry;
    }

    public void unsetThingRegistry() {
        thingRegistry = null;
    }

    public void setItemRegistry(ItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    public void unsetItemRegistry() {
        itemRegistry = null;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Get the current semantic model instance.")
    public String getSemantic() {
        return semanticService.getCurrentInstanceAsString();
    }

    @GET
    @Path("/select/{uid: [a-zA-Z_0-9]*}")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Gets semantic informations about the uid. (Not implemented yet)")
    public String getSemanticForUid(@PathParam("uid") String uid) {
        return "recieved uid: " + uid;
    }

    @GET
    @Path("select/{uid: [a-zA-Z_0-9]*}/location")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Gets the location name for the given uid.")
    public String getLocationName(@PathParam("uid") String uid) {
        return semanticService.getLocationName(uid);
    }

    @POST
    @Path("/post/command")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Posts a semantic command to control items.")
    public Response postCommand(SemanticPostCommandBean commandBean) {
        long start = System.nanoTime();
        if (commandBean == null || commandBean.statement == null || commandBean.command == null
                || commandBean.statement.isEmpty() || commandBean.command.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        QueryResult qr = semanticService.sendCommand(commandBean.statement, commandBean.command);
        long end = System.nanoTime();
        double time = calcTimeDifInMs(start, end);
        logger.debug("sending semantic command takes {} ms", time);

        writeToFile("commandTime", String.valueOf(time));
        return Response.ok(qr.getAsJsonString(), MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/select")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Executes a select query on the semantic model.")
    public String select(@QueryParam("statement") String query, @QueryParam("withlatest") boolean withLatest) {
        long start = System.nanoTime();
        QueryResult result = semanticService.executeSelect(query);
        long end = System.nanoTime();
        double time = calcTimeDifInMs(start, end);
        logger.debug("execute semantic select takes {} ms", time);

        writeToFile("selectTime", String.valueOf(time));
        return result == null ? JsonNull.instance.toString() : result.getAsJsonString();
    }

    @GET
    @Path("/ask")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Executes a ask query on the semantic model.")
    public String ask(@QueryParam("statement") String query, @QueryParam("withlatest") boolean withLatest) {
        long start = System.nanoTime();
        boolean result = semanticService.executeAsk(query);
        long end = System.nanoTime();
        double time = calcTimeDifInMs(start, end);
        logger.debug("execute semantic ask takes {} ms", time);

        writeToFile("askTime", String.valueOf(time));
        return String.format(JSON_BOOLEAN_FORMAT, result);
    }

    @GET
    @Path("/sensors")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Gets all sensors with their semantic informations.")
    public String getAllSensors() {
        QueryResult qr = semanticService.getAllSensors();
        return qr.getAsJsonString();
    }

    @GET
    @Path("/helper")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Helper which lists things and items in a readable way. (Helps with the model creation)")
    public String getSetupHelper() {
        ConfigHelper helper = new ConfigHelper();
        helper.addThingsAndItems(thingRegistry, itemRegistry);
        return helper.getAsString();
    }

    // writes text to a file
    // was used for performance measurement
    @SuppressWarnings("unused")
    private static synchronized void writeToFile(String filename, String value) {
        File file = null;
        FileWriter writer = null;
        try {
            file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            writer = new FileWriter(file, true);
            writer.append("\n");
            writer.append(value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static double calcTimeDifInMs(long start, long end) {
        return (end - start) / 1e6;
    }
}
