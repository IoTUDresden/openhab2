package org.openhab.ui.semanticui.internal;

import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This component registers the Semantic UI Webapp.
 *
 * @author Andre Kuehnert
 */
public final class SemanticUIApp {

    public static final String WEBAPP_ALIAS = "/semanticui";
    private final Logger logger = LoggerFactory.getLogger(SemanticUIApp.class);

    private HttpService httpService;

    public void activate() {
        try {
            httpService.registerResources(WEBAPP_ALIAS, "web", null);
            logger.info("Started Semantic UI at " + WEBAPP_ALIAS);
        } catch (NamespaceException e) {
            logger.error("Error during servlet startup", e);
        }
    }

    public void deactivate() {
        httpService.unregister(WEBAPP_ALIAS);
        logger.info("Stopped Semantic UI");
    }

    public void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    public void unsetHttpService(HttpService httpService) {
        this.httpService = null;
    }

}
