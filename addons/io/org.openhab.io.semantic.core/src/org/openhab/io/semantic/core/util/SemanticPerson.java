package org.openhab.io.semantic.core.util;

public final class SemanticPerson {

    private String firstName = "";
    private String lastName = "";
    private String age = "";
    private String gender = "";
    private String uid = ""; // we use the model id, which should be unique
    private String healthMonitorUid = "";

    public SemanticPerson() {
    }

    public SemanticPerson(String uid, String firstName, String lastName, String age, String gender,
            String healthMonitorUid) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
        this.healthMonitorUid = healthMonitorUid;
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHealthMonitorUid() {
        return healthMonitorUid;
    }

    public void setHealthMonitorUid(String healthMonitorUid) {
        this.healthMonitorUid = healthMonitorUid;
    }
}
