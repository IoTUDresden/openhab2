package org.openhab.io.goal.core.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.smarthome.io.rest.RESTResource;
import org.openhab.io.goal.core.ExecuteGoalCommandBean;
import org.openhab.io.goal.core.Goal;
import org.openhab.io.goal.core.GoalService;
import org.openhab.io.goal.core.Quality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Path(GoalResource.PATH_GOAL)
@Api(value = GoalResource.PATH_GOAL)
public class GoalResource implements RESTResource {
    private static final Logger logger = LoggerFactory.getLogger(GoalResource.class);

    public static final String PATH_GOAL = "goal";

    // TODO all stuff which should be accessible over rest goes here

    private GoalService goalService;

    public void setGoalService(GoalService goalService) {
        this.goalService = goalService;
    }

    public void unsetGoalService() {
        goalService = null;
    }

    public void activate() {
        logger.debug("GoalResource activated");
    }

    public void deactivate() {
        logger.debug("GoalResource deactivated");
    }

    @GET
    @Path("goals")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Gets all available goals.")
    public List<Goal> getGoals() {
        return goalService.getGoals();
    }

    @GET
    @Path("qualities")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Gets all available qualities.")
    public List<Quality> getQualities() {
        return goalService.getQualities();
    }

    @POST
    @Path("/execute/goal")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Executes the given goal.")
    public Response executeGoal(ExecuteGoalCommandBean command) {
        if (!checkCommandParameter(command)) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        boolean success = goalService.executeGoal(command);
        return success ? Response.ok().build() : Response.status(Status.NOT_MODIFIED).build();
    }

    private boolean checkCommandParameter(ExecuteGoalCommandBean command) {
        return command != null && command.goal != null && command.goal.name != null && !command.goal.name.isEmpty();
    }

}
