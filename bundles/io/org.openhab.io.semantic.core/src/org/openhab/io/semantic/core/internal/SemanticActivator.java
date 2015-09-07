package org.openhab.io.semantic.core.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activator for activating the semantic access layer core bundle
 * 
 * @author André Kühnert
 *
 */
public class SemanticActivator implements BundleActivator {
	private static final Logger logger = LoggerFactory.getLogger(SemanticActivator.class);

	private static BundleContext context;
	

	static BundleContext getContext() {
		return context;
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		SemanticActivator.context = bundleContext;
        logger.debug("startet semantic access layer");
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		SemanticActivator.context = null;
		logger.debug("stopped semantic access layer");
	}

}