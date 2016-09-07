package org.openhab.io.semantic.core.util;

public class SemanticRobot {
    private String uid = "";

    public SemanticRobot() {

    }

    public SemanticRobot(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
