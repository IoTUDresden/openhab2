package org.openhab.io.semantic.core.util;

public class Poi {
    private String position;
    private String orientation;

    public Poi() {
    }

    public Poi(String position, String orientation) {
        this.position = position;
        this.orientation = orientation;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }
}
