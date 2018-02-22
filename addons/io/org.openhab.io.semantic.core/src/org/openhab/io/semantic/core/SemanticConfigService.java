package org.openhab.io.semantic.core;

import java.util.List;

import org.openhab.io.semantic.core.util.Poi;
import org.openhab.io.semantic.core.util.SemanticHealthSensor;
import org.openhab.io.semantic.core.util.SemanticLocation;
import org.openhab.io.semantic.core.util.SemanticPerson;
import org.openhab.io.semantic.core.util.SemanticRobot;
import org.openhab.io.semantic.core.util.SemanticThing;

public interface SemanticConfigService {

    /**
     * Adds a new Person
     *
     * @param person
     */
    boolean addPerson(SemanticPerson person);

    /**
     * Gets {@link SemanticPerson}s
     *
     * @return
     */
    List<SemanticPerson> getSemanticPersons();

    /**
     * Lists all {@link SemanticThing}s, in order to show, which is available through the semantic layer,
     * setting location...
     *
     * @return
     */
    List<SemanticThing> getSemanticThings();

    /**
     * Gets the {@link Poi} of an item if any exists.
     *
     * @param itemName
     * @return
     */
    Poi getItemPoi(String itemName);

    /**
     * Updates the {@link Poi} for a given item. If the newPoi is empty
     * (orientation || position == null) or null, an existing poi for the thing will be deleted
     *
     * @param itemName
     * @param newPoi
     * @return true if succeeded
     */
    boolean updateItemPoi(String itemName, Poi newPoi);

    /**
     * Updates the {@link Poi} of a given Thing.
     * If newPoi == null, the existing poi for the fing is deleted.
     *
     * @param thingName
     * @param newPoi
     * @return true if update request succeeded
     */
    boolean updateThingPoi(String thingName, Poi newPoi);

    /**
     * Gets all {@link SemanticLocation}s
     *
     * @return
     */
    List<SemanticLocation> getSemanticLocations();

    /**
     * Gets the {@link SemanticLocation} for a given thing.
     *
     * @param thingName
     * @return null if thing has no location
     */
    SemanticLocation getSemanticLocationForThing(String thingName);

    /**
     * Updates the {@link SemanticLocation} for an item. If location == null or has no semanticUri,
     * the existing location of the thing will be deleted.
     *
     * @param thingName
     * @param location
     * @return
     */
    boolean updateSemanticLocationForThing(String thingName, SemanticLocation location);

    /**
     * Gets all {@link SemanticRobot}s.
     *
     * @return
     */
    List<SemanticRobot> getSemanticRobots();

    /**
     * Gets all {@link SemanticHealthSensor}s
     *
     * @return
     */
    List<SemanticHealthSensor> getSemanticHealthSensors();

}
