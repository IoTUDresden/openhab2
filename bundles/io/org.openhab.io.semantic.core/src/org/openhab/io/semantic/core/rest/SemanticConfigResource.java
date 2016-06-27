package org.openhab.io.semantic.core.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.smarthome.io.rest.RESTResource;
import org.openhab.io.semantic.core.SemanticConfigService;
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

    @GET
    @Path("/persons")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Gets all known things semantic annotated.")
    public List<SemanticPerson> getSemanticPersons() {
        return configService.getSemanticPersons();
    }

}
