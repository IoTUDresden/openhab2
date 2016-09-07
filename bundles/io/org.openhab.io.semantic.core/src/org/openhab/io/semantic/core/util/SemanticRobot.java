package org.openhab.io.semantic.core.util;

public class SemanticRobot {
    private String uid = "";
    private String positionUid = "";
    private String movementUid = "";
    private String moveUid = "";

    public SemanticRobot() {

    }

    public SemanticRobot(String uid, String positionUid, String movementUid, String moveUid) {
        this.uid = uid;
        this.positionUid = positionUid;
        this.movementUid = movementUid;
        this.moveUid = moveUid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPositionUid() {
        return positionUid;
    }

    public void setPositionUid(String positionUid) {
        this.positionUid = positionUid;
    }

    public String getMovementUid() {
        return movementUid;
    }

    public void setMovementUid(String movementUid) {
        this.movementUid = movementUid;
    }

    public String getMoveUid() {
        return moveUid;
    }

    public void setMoveUid(String moveUid) {
        this.moveUid = moveUid;
    }

}
