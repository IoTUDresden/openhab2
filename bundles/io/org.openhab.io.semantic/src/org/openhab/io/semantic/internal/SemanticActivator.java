package org.openhab.io.semantic.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SemanticActivator implements BundleActivator {
	private static final Logger logger = LoggerFactory.getLogger(SemanticActivator.class);

	private static BundleContext context;
	

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		SemanticActivator.context = bundleContext;
        logger.debug("startet semantic access layer");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		SemanticActivator.context = null;
		logger.debug("stopped semantic access layer");
	}

}
