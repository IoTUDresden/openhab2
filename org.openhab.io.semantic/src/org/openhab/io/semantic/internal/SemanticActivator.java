package org.openhab.io.semantic.internal;

import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SemanticActivator implements BundleActivator {
	private static final Logger logger = LoggerFactory.getLogger(SemanticActivator.class);

	private static BundleContext context;
	
    @SuppressWarnings("rawtypes")
	private ServiceTracker thingRegistryServiceTracker;
	private ThingRegistry thingRegistry;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void start(BundleContext bundleContext) throws Exception {
		SemanticActivator.context = bundleContext;
		
		thingRegistryServiceTracker = new ServiceTracker(SemanticActivator.context, ThingRegistry.class.getName(), null){
            @Override
            public Object addingService(final ServiceReference reference) {
                thingRegistry = (ThingRegistry) context.getService(reference);
                return thingRegistry;
            }

            @Override
            public void removedService(final ServiceReference reference, final Object service) {
                synchronized (SemanticActivator.this) {
                    thingRegistry = null;
                }
            }
        };
        thingRegistryServiceTracker.open();
        
        //TODO
        // get semantic information
        // register handler
        // start server

	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		SemanticActivator.context = null;
		thingRegistry = null;
		thingRegistryServiceTracker.close();
	}

}
