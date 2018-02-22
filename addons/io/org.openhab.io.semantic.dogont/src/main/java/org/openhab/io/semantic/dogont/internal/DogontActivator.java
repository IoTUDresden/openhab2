package org.openhab.io.semantic.dogont.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activator for the semantic access layer implementation bundle. In this case, the <a
 * href="http://lov.okfn.org/dataset/lov/vocabs/dogont">Dogont</a> Ontology is used.
 * 
 * @author André Kühnert
 *
 */
public class DogontActivator implements BundleActivator {
    private static final Logger logger = LoggerFactory.getLogger(DogontActivator.class);

    private static BundleContext context;

    static BundleContext getContext() {
        return context;
    }

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        DogontActivator.context = bundleContext;
        logger.debug("startet dogont bundle");
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        DogontActivator.context = null;
        logger.debug("stopped dogont bundle");
    }
}
