package org.openhab.io.semantic.core;

import java.util.List;

import org.openhab.io.semantic.core.util.SemanticThing;

public interface SemanticConfigService {

    // TODO
    void addPerson();

    /**
     * Lists all {@link SemanticThing}s, in order to show, which is available through the semantic layer,
     * setting location...
     *
     * @return
     */
    List<SemanticThing> getSemanticThings();

}
