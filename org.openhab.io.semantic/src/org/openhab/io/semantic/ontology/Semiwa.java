package org.openhab.io.semantic.ontology;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * Vocabulary for http://semiwa.org/0.1/schema
 *
 * Automatically generated with TopBraid Composer.
 */
public class Semiwa {

    public final static String BASE_URI = "http://semiwa.org/0.1/schema";

    public final static String NS = BASE_URI + "#";

    public final static String PREFIX = "semiwa";


    public final static Resource AbstractDataComponent = ResourceFactory.createResource(NS + "AbstractDataComponent");

    public final static Resource Activity = ResourceFactory.createResource(NS + "Activity");

    public final static Resource ActivityBlock = ResourceFactory.createResource(NS + "ActivityBlock");

    public final static Resource ActivityCapability = ResourceFactory.createResource(NS + "ActivityCapability");

    public final static Resource ActivityProcedure = ResourceFactory.createResource(NS + "ActivityProcedure");

    public final static Resource Actuator = ResourceFactory.createResource(NS + "Actuator");

    public final static Resource ActuatorCharacteristic = ResourceFactory.createResource(NS + "ActuatorCharacteristic");

    public final static Resource ActuatorDeployment = ResourceFactory.createResource(NS + "ActuatorDeployment");

    public final static Resource ActuatorSensorCollection = ResourceFactory.createResource(NS + "ActuatorSensorCollection");

    public final static Resource CalibrationBlock = ResourceFactory.createResource(NS + "CalibrationBlock");

    public final static Resource Capability = ResourceFactory.createResource(NS + "Capability");

    public final static Resource Characteristic = ResourceFactory.createResource(NS + "Characteristic");

    public final static Resource Collection = ResourceFactory.createResource(NS + "Collection");

    public final static Resource CollectionBlock = ResourceFactory.createResource(NS + "CollectionBlock");

    public final static Resource CommonModule = ResourceFactory.createResource(NS + "CommonModule");

    public final static Resource ComplexConditionalValue = ResourceFactory.createResource(NS + "ComplexConditionalValue");

    public final static Resource ConditionalValue = ResourceFactory.createResource(NS + "ConditionalValue");

    public final static Resource Deployment = ResourceFactory.createResource(NS + "Deployment");

    public final static Resource DeploymentBlock = ResourceFactory.createResource(NS + "DeploymentBlock");

    public final static Resource DynamicModule = ResourceFactory.createResource(NS + "DynamicModule");

    public final static Resource MeasurementCapability = ResourceFactory.createResource(NS + "MeasurementCapability");

    public final static Resource Platform = ResourceFactory.createResource(NS + "Platform");

    public final static Resource PlatformCharacteristic = ResourceFactory.createResource(NS + "PlatformCharacteristic");

    public final static Resource PlatformCollection = ResourceFactory.createResource(NS + "PlatformCollection");

    public final static Resource PlatformDeployment = ResourceFactory.createResource(NS + "PlatformDeployment");

    public final static Resource Procedure = ResourceFactory.createResource(NS + "Procedure");

    public final static Resource SeMiWa = ResourceFactory.createResource(NS + "SeMiWa");

    public final static Resource Sensing = ResourceFactory.createResource(NS + "Sensing");

    public final static Resource SensingBlock = ResourceFactory.createResource(NS + "SensingBlock");

    public final static Resource SensingProcedure = ResourceFactory.createResource(NS + "SensingProcedure");

    public final static Resource Sensor = ResourceFactory.createResource(NS + "Sensor");

    public final static Resource SensorCalibration = ResourceFactory.createResource(NS + "SensorCalibration");

    public final static Resource SensorCharacteristic = ResourceFactory.createResource(NS + "SensorCharacteristic");

    public final static Resource SensorDeployment = ResourceFactory.createResource(NS + "SensorDeployment");

    public final static Resource Service = ResourceFactory.createResource(NS + "Service");

    public final static Resource StaticModule = ResourceFactory.createResource(NS + "StaticModule");

    public final static Resource Tasking = ResourceFactory.createResource(NS + "Tasking");

    public final static Resource TaskingBlock = ResourceFactory.createResource(NS + "TaskingBlock");

    public final static Resource TaskingService = ResourceFactory.createResource(NS + "TaskingService");

    public final static Property accuracy = ResourceFactory.createProperty(NS + "accuracy");

    public final static Property active = ResourceFactory.createProperty(NS + "active");

    public final static Property activity = ResourceFactory.createProperty(NS + "activity");

    public final static Property activityCapability = ResourceFactory.createProperty(NS + "activityCapability");

    public final static Property activityOnFeature = ResourceFactory.createProperty(NS + "activityOnFeature");

    public final static Property activityOnProperty = ResourceFactory.createProperty(NS + "activityOnProperty");

    public final static Property activityProcedure = ResourceFactory.createProperty(NS + "activityProcedure");

    public final static Property activityType = ResourceFactory.createProperty(NS + "activityType");

    public final static Property alternativeUnitOfMeasure = ResourceFactory.createProperty(NS + "alternativeUnitOfMeasure");

    public final static Property alternativeUnitOfOutput = ResourceFactory.createProperty(NS + "alternativeUnitOfOutput");

    public final static Property basedOn = ResourceFactory.createProperty(NS + "basedOn");

    public final static Property boundTo = ResourceFactory.createProperty(NS + "boundTo");

    public final static Property calibrated = ResourceFactory.createProperty(NS + "calibrated");

    public final static Property calibrationDate = ResourceFactory.createProperty(NS + "calibrationDate");

    public final static Property characteristic = ResourceFactory.createProperty(NS + "characteristic");

    public final static Property component = ResourceFactory.createProperty(NS + "component");

    public final static Property condition = ResourceFactory.createProperty(NS + "condition");

    public final static Property conformanceCharacteristic = ResourceFactory.createProperty(NS + "conformanceCharacteristic");

    public final static Property connectionType = ResourceFactory.createProperty(NS + "connectionType");

    public final static Property contains = ResourceFactory.createProperty(NS + "contains");

    public final static Property context = ResourceFactory.createProperty(NS + "context");

    public final static Property dataComponent = ResourceFactory.createProperty(NS + "dataComponent");

    public final static Property dataDescription = ResourceFactory.createProperty(NS + "dataDescription");

    public final static Property dataValue = ResourceFactory.createProperty(NS + "dataValue");

    public final static Property deployedActuators = ResourceFactory.createProperty(NS + "deployedActuators");

    public final static Property deployedAt = ResourceFactory.createProperty(NS + "deployedAt");

    public final static Property deployedSensor = ResourceFactory.createProperty(NS + "deployedSensor");

    public final static Property deploymentLocation = ResourceFactory.createProperty(NS + "deploymentLocation");

    public final static Property deploys = ResourceFactory.createProperty(NS + "deploys");

    public final static Property description = ResourceFactory.createProperty(NS + "description");

    public final static Property detectionLimit = ResourceFactory.createProperty(NS + "detectionLimit");

    public final static Property drift = ResourceFactory.createProperty(NS + "drift");

    public final static Property executionTime = ResourceFactory.createProperty(NS + "executionTime");

    public final static Property extension = ResourceFactory.createProperty(NS + "extension");

    public final static Property fields = ResourceFactory.createProperty(NS + "fields");

    public final static Property frequency = ResourceFactory.createProperty(NS + "frequency");

    public final static Property homogeneous = ResourceFactory.createProperty(NS + "homogeneous");

    public final static Property implementedBy = ResourceFactory.createProperty(NS + "implementedBy");

    public final static Property label = ResourceFactory.createProperty(NS + "label");

    public final static Property latency = ResourceFactory.createProperty(NS + "latency");

    public final static Property locatedIn = ResourceFactory.createProperty(NS + "locatedIn");

    public final static Property manufacturer = ResourceFactory.createProperty(NS + "manufacturer");

    public final static Property measurementCapability = ResourceFactory.createProperty(NS + "measurementCapability");

    public final static Property method = ResourceFactory.createProperty(NS + "method");

    public final static Property mobile = ResourceFactory.createProperty(NS + "mobile");

    public final static Property model = ResourceFactory.createProperty(NS + "model");

    public final static Property modelNr = ResourceFactory.createProperty(NS + "modelNr");

    public final static Property name = ResourceFactory.createProperty(NS + "name");

    public final static Property observedFeature = ResourceFactory.createProperty(NS + "observedFeature");

    public final static Property observedProperty = ResourceFactory.createProperty(NS + "observedProperty");

    public final static Property operationArea = ResourceFactory.createProperty(NS + "operationArea");

    public final static Property operationalComponent = ResourceFactory.createProperty(NS + "operationalComponent");

    public final static Property operator = ResourceFactory.createProperty(NS + "operator");

    public final static Property passed = ResourceFactory.createProperty(NS + "passed");

    public final static Property physicalComponent = ResourceFactory.createProperty(NS + "physicalComponent");

    public final static Property precision = ResourceFactory.createProperty(NS + "precision");

    public final static Property provides = ResourceFactory.createProperty(NS + "provides");

    public final static Property qualifiedBy = ResourceFactory.createProperty(NS + "qualifiedBy");

    public final static Property range = ResourceFactory.createProperty(NS + "range");

    public final static Property reason = ResourceFactory.createProperty(NS + "reason");

    public final static Property resolution = ResourceFactory.createProperty(NS + "resolution");

    public final static Property responseTime = ResourceFactory.createProperty(NS + "responseTime");

    public final static Property responsibleParty = ResourceFactory.createProperty(NS + "responsibleParty");

    public final static Property restrictedBy = ResourceFactory.createProperty(NS + "restrictedBy");

    public final static Property selectivity = ResourceFactory.createProperty(NS + "selectivity");

    public final static Property senses = ResourceFactory.createProperty(NS + "senses");

    public final static Property sensingProcedure = ResourceFactory.createProperty(NS + "sensingProcedure");

    public final static Property sensitivity = ResourceFactory.createProperty(NS + "sensitivity");

    public final static Property siteCharacteristic = ResourceFactory.createProperty(NS + "siteCharacteristic");

    public final static Property summary = ResourceFactory.createProperty(NS + "summary");

    public final static Property taskingService = ResourceFactory.createProperty(NS + "taskingService");

    public final static Property tasks = ResourceFactory.createProperty(NS + "tasks");

    public final static Property test = ResourceFactory.createProperty(NS + "test");

    public final static Property type = ResourceFactory.createProperty(NS + "type");

    public final static Property uID = ResourceFactory.createProperty(NS + "uID");

    public final static Property unit = ResourceFactory.createProperty(NS + "unit");

    public final static Property unitOfMeasure = ResourceFactory.createProperty(NS + "unitOfMeasure");

    public final static Property unitOfOutput = ResourceFactory.createProperty(NS + "unitOfOutput");

    public final static Property validStates = ResourceFactory.createProperty(NS + "validStates");

    public final static Property validTime = ResourceFactory.createProperty(NS + "validTime");

    public final static Property validTransitions = ResourceFactory.createProperty(NS + "validTransitions");

    public final static Property value = ResourceFactory.createProperty(NS + "value");


    public static String getURI() {
        return NS;
    }
}
