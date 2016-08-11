package org.openhab.io.semantic.core.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.smarthome.io.rest.RESTResource;
import org.openhab.io.semantic.core.SemanticConfigService;
import org.openhab.io.semantic.core.util.Poi;
import org.openhab.io.semantic.core.util.SemanticLocation;
import org.openhab.io.semantic.core.util.SemanticPerson;
import org.openhab.io.semantic.core.util.SemanticThing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Path(SemanticConfigResource.PATH_SEMANTIC_CONFIG)
@Api(value = SemanticConfigResource.PATH_SEMANTIC_CONFIG)
public class SemanticConfigResource implements RESTResource {
    private static final Logger logger = LoggerFactory.getLogger(SemanticConfigResource.class);

    private SemanticConfigService configService;

    public static final String PATH_SEMANTIC_CONFIG = "semantic/extended";

    public void unsetConfigService() {
        configService = null;
    }

    public void setConfigService(SemanticConfigService configService) {
        this.configService = configService;
    }

    public void activate() {
    }

    public void deactivate() {
    }

    @GET
    @Path("/things")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Gets all known things semantic annotated.")
    public List<SemanticThing> getSemanticThings() {
        return configService.getSemanticThings();
    }

    @POST
    @Path("/things/{thingName: [a-zA-Z_0-9]*}/poi")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Sets/updates the poi for a given thing.")
    public Response updateThingPoi(@PathParam("thingName") String thingName, Poi newPoi) {
        if (newPoi == null || thingName == null || newPoi.getOrientation() == null || newPoi.getOrientation().isEmpty()
                || newPoi.getPosition() == null || newPoi.getPosition().isEmpty()) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        boolean success = configService.updateThingPoi(thingName, newPoi);
        return success ? Response.ok().build() : Response.status(Status.NOT_MODIFIED).build();
    }

    @GET
    @Path("/locations")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Gets all semantic locations")
    public List<SemanticLocation> getLocations() {
        return configService.getSemanticLocations();
    }

    @GET
    @Path("/persons")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Gets all known things semantic annotated.")
    public List<SemanticPerson> getSemanticPersons() {
        return configService.getSemanticPersons();
    }

    @GET
    @Path("/items/{itemName: [a-zA-Z_0-9]*}/poi")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Gets the Poi of the given item if exists.")
    public Poi getItemPoi(@PathParam("itemName") String itemName) {
        if (itemName == null) {
            return null; // TODO Bad Request
        }
        return configService.getItemPoi(itemName);
    }

    @POST
    @Path("/items/{itemName: [a-zA-Z_0-9]*}/poi")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Sets/updates the poi for a given item.")
    public Response updateItemPoi(@PathParam("itemName") String itemName, Poi newPoi) {
        if (newPoi == null || itemName == null || newPoi.getOrientation() == null || newPoi.getOrientation().isEmpty()
                || newPoi.getPosition() == null || newPoi.getPosition().isEmpty()) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        boolean success = configService.updateItemPoi(itemName, newPoi);
        return success ? Response.ok().build() : Response.status(Status.NOT_MODIFIED).build();
    }

}
