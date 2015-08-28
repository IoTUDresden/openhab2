package org.openhab.io.semantic.dogont.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DogontActivator implements BundleActivator {
	private static final Logger logger = LoggerFactory.getLogger(DogontActivator.class);

	private static BundleContext context;	

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		DogontActivator.context = bundleContext;
        logger.debug("startet dogont bundle");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		DogontActivator.context = null;
		logger.debug("stopped dogont bundle");
	}

}
