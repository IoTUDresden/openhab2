package org.openhab.io.goal.impl;

import java.util.ArrayList;
import java.util.List;

import org.openhab.io.goal.core.Goal;
import org.openhab.io.goal.core.GoalService;
import org.openhab.io.goal.core.Quality;
import org.openhab.io.semantic.core.SemanticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoalServiceImpl implements GoalService {
    private static final Logger logger = LoggerFactory.getLogger(GoalServiceImpl.class);

    private SemanticService semanticService; // ready to use sal

    public void activate() {
        logger.debug("GoalService activated");
    }

    public void deactivate() {
        logger.debug("GoalService deactivated");
    }

    public void unsetSemanticService() {
        semanticService = null;
    }

    public void setSemanticService(SemanticService semanticService) {
        this.semanticService = semanticService;
    }

    // TODO here goes the service impl

    @Override
    public List<Goal> getGoals() {
        List<Goal> goals = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Goal g = new Goal();
            g.name = "Dummy-Goal_" + i;
            goals.add(g);

        }
        return goals;
    }

    @Override
    public List<Quality> getQualities() {
        List<Quality> qualities = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Quality q = new Quality();
            q.name = "Dummy-Quality_" + i;
            qualities.add(q);

        }
        return qualities;
    }

}
