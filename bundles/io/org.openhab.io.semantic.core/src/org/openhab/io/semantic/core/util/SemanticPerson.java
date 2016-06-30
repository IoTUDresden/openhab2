package org.openhab.io.semantic.core.util;

public final class SemanticPerson {

    private String firstName;
    private String uid;

    public SemanticPerson(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
