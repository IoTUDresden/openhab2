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
    private String clazz;
    private SemanticLocation location;
    private Poi poi;

    public SemanticThing() {
    }

    public SemanticThing(String semanticUri, String openHabName, String clazz) {
        this(semanticUri, openHabName, clazz, null, null);
    }

    public SemanticThing(String semanticUri, String openHabName, String clazz, SemanticLocation location, Poi poi) {
        this.semanticUri = semanticUri;
        this.openHabName = openHabName;
        this.clazz = clazz;
        this.location = location;
        this.poi = poi;

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

    public Poi getPoi() {
        return poi;
    }

    public void setPoi(Poi poi) {
        this.poi = poi;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }
}
