package org.openhab.io.semantic.dogont.internal.performance;

import org.openhab.io.semantic.dogont.internal.ontology.DogontSchema;
import org.openhab.io.semantic.dogont.internal.util.ModelCopier;
import org.openhab.io.semantic.dogont.internal.util.SemanticConstants;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;

public class ModelCopierForPerformanceTest extends ModelCopier {
    private Dataset dataset;

    public ModelCopierForPerformanceTest(Dataset dataset) {
        super(dataset);
        this.dataset = dataset;
    }

    public void addLivingRoomWithName(String name, String instanceName) {
        try {
            dataset.begin(ReadWrite.WRITE);
            UpdateRequest req = UpdateFactory.create(getAddLivingRoomQuery(name, instanceName));
            UpdateAction.execute(req, dataset);
            dataset.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dataset.end();
        }

    }

    public void updateLocationForThing(String thingName, String locationInstanceName) {
        try {
            dataset.begin(ReadWrite.WRITE);
            UpdateRequest req = UpdateFactory.create(getUpdateLivingRoomQuery(thingName, locationInstanceName));
            UpdateAction.execute(req, dataset);
            dataset.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dataset.end();
        }
    }

    private static String getUpdateLivingRoomQuery(String thingName, String locationInstanceName) {
        StringBuilder builder = new StringBuilder();
        builder.append("PREFIX dogont: <" + DogontSchema.NS + "> ");
        builder.append("PREFIX rdf: <" + SemanticConstants.NS_RDF_SYNTAX + "> ");
        builder.append("PREFIX instance <" + SemanticConstants.NS_INSTANCE + "> ");
        builder.append("INSERT { GRAPH <" + SemanticConstants.GRAPH_NAME_INSTANCE + "> { ");
        builder.append("  instance:" + thingName + " dogont:isIn instance:" + locationInstanceName + ".");
        builder.append("}} WHERE {}");
        return builder.toString();
    }

    private static String getAddLivingRoomQuery(String livingRoomName, String instanceName) {
        StringBuilder builder = new StringBuilder();
        builder.append("PREFIX dogont: <" + DogontSchema.NS + "> ");
        builder.append("PREFIX rdf: <" + SemanticConstants.NS_RDF_SYNTAX + "> ");
        builder.append("PREFIX instance <" + SemanticConstants.NS_INSTANCE + "> ");
        builder.append("INSERT { GRAPH <" + SemanticConstants.GRAPH_NAME_INSTANCE + "> { ");
        builder.append("  instance:" + instanceName + " rdf:type dogont:LivingRoom ; ");
        builder.append("    rdfs:label \"" + livingRoomName + "\"^^xsd:string. ");
        builder.append("}} WHERE {}");
        return builder.toString();
    }
}
