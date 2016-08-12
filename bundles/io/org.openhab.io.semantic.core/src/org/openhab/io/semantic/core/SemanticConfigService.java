package org.openhab.io.semantic.core;

import java.util.List;

import org.openhab.io.semantic.core.util.Poi;
import org.openhab.io.semantic.core.util.SemanticLocation;
import org.openhab.io.semantic.core.util.SemanticPerson;
import org.openhab.io.semantic.core.util.SemanticThing;

public interface SemanticConfigService {

    /**
     * Adds a new Person
     *
     * @param person
     */
    void addPerson(SemanticPerson person);

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
     * Updates the {@link Poi} for a given item.
     *
     * @param itemName
     * @param newPoi
     * @return true if succeeded
     */
    boolean updateItemPoi(String itemName, Poi newPoi);

    /**
     * Updates the {@link Poi} of a given Thing
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

}
