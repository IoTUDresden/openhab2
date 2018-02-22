package org.openhab.binding.viccirobot.internal;

import org.eclipse.smarthome.core.library.types.StringType;

public abstract class MovementState {
    public static final String STATE_ERROR = "ERROR";
    public static final String STATE_ARRIVED = "ARRIVED";
    public static final String STATE_MOVING = "MOVING";

    private String position;
    private String state;

    public MovementState(String state, String position) {
        this.position = position;
        this.state = state;
    }

    public StringType toStringType() {
        return new StringType(state + ": " + position);
    }

    public static class MovingState extends MovementState {
        public MovingState(String position) {
            super(STATE_MOVING, position);
        }
    }

    public static class ErrorState extends MovementState {
        public ErrorState(String position) {
            super(STATE_ERROR, position);
        }
    }

    public static class ArrivedState extends MovementState {
        public ArrivedState(String position) {
            super(STATE_ARRIVED, position);
        }
    }

}
