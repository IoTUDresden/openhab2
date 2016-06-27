package org.openhab.io.semantic.core;

import java.util.List;

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

}
