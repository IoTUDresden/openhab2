package org.openhab.io.semantic.core.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean for the things semantic annotated
 *
 * @author andre
 *
 */
public final class SemanticThing {

    private List<SemanticItem> items = new ArrayList<>();
    private String semanticUri;
    private String openHabName;
    private SemanticLocation location;

    public SemanticThing(String semanticUri, String openHabName) {
        this(semanticUri, openHabName, null);
    }

    public SemanticThing(String semanticUri, String openHabName, SemanticLocation location) {
        this.semanticUri = semanticUri;
        this.openHabName = openHabName;
        this.location = location;
    }

    public List<SemanticItem> getItems() {
        return items;
    }

    public void addSemanticItem(SemanticItem item) {
        items.add(item);
    }

    public String getSemanticUri() {
        return semanticUri;
    }

    public void setSemanticUri(String semanticUri) {
        this.semanticUri = semanticUri;
    }

    public String getOpenHabName() {
        return openHabName;
    }

    public void setOpenHabName(String openHabName) {
        this.openHabName = openHabName;
    }

    public SemanticLocation getLocation() {
        return location;
    }

    public void setLocation(SemanticLocation location) {
        this.location = location;
    }
}