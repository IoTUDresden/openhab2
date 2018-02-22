package org.openhab.io.semantic.core.util;

public class SemanticHealthSensor {

    private String uid = "";
    private String heartRate = "";
    private String bloodOxygenSaturation = "";
    private String heartRateUid = "";
    private String bloodOxygenSaturationUid = "";

    public SemanticHealthSensor() {
    }

    public SemanticHealthSensor(String uid, String heartRate, String bloodOxygenSaturation, String heartRateUid,
            String bloodOxygenSaturationUid) {
        this.uid = uid;
        this.heartRate = heartRate;
        this.bloodOxygenSaturation = bloodOxygenSaturation;
        this.heartRateUid = heartRateUid;
        this.bloodOxygenSaturationUid = bloodOxygenSaturationUid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getHeartReate() {
        return heartRate;
    }

    public void setHeartReate(String heartReate) {
        this.heartRate = heartReate;
    }

    public String getBloodOxygenSaturation() {
        return bloodOxygenSaturation;
    }

    public void setBloodOxygenSaturation(String bloodOxygenSaturation) {
        this.bloodOxygenSaturation = bloodOxygenSaturation;
    }

    public String getHeartRateUid() {
        return heartRateUid;
    }

    public void setHeartRateUid(String heartRateUid) {
        this.heartRateUid = heartRateUid;
    }

    public String getBloodOxygenSaturationUid() {
        return bloodOxygenSaturationUid;
    }

    public void setBloodOxygenSaturationUid(String bloodOxygenSaturationUid) {
        this.bloodOxygenSaturationUid = bloodOxygenSaturationUid;
    }
}
