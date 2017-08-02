package org.openhab.io.semantic.tests;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {
	private static final Logger logger = LoggerFactory.getLogger(Activator.class);
	
	private static BundleContext context;	

	static BundleContext getContext() {
		return context;
	}	

	public void start(BundleContext context) throws Exception {
		logger.debug("activate semantic test bundle");
		Activator.context= context;
	}

	public void stop(BundleContext context) throws Exception {
		logger.debug("deactivate semantic test bundle");
		context = null;
	}

}
