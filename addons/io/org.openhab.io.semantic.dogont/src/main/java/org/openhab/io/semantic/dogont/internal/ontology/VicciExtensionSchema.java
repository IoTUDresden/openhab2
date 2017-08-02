package org.openhab.io.semantic.dogont.internal.ontology;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * Vocabulary for http://openhab-semantic/0.1/dogont-vicci-extension
 *
 * Automatically generated with TopBraid Composer.
 */
public class VicciExtensionSchema {

    public final static String BASE_URI = "http://openhab-semantic/0.1/dogont-vicci-extension";

    public final static String NS = BASE_URI + "#";

    public final static String PREFIX = "vicci";


    public final static Resource POI = ResourceFactory.createResource(NS + "POI");

    public final static Resource RobotPosition = ResourceFactory.createResource(NS + "RobotPosition");

    public final static Property hasOrientation = ResourceFactory.createProperty(NS + "hasOrientation");

    public final static Property hasPosition = ResourceFactory.createProperty(NS + "hasPosition");

    public final static Property hasRobotPosition = ResourceFactory.createProperty(NS + "hasRobotPosition");


    public static String getURI() {
        return NS;
    }
}
