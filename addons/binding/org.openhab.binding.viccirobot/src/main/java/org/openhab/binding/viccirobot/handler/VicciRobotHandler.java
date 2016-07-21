/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.viccirobot.handler;

import static org.openhab.binding.viccirobot.VicciRobotBindingConstants.*;

import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.viccirobot.internal.LocationUtil;
import org.openhab.binding.viccirobot.internal.MovementState;
import org.openhab.binding.viccirobot.internal.MovementState.ArrivedState;
import org.openhab.binding.viccirobot.internal.MovementState.ErrorState;
import org.openhab.binding.viccirobot.internal.MovementState.MovingState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.vicci.driver.robot.Robot;
import eu.vicci.driver.robot.exception.CannotMoveToMovementTargetException;
import eu.vicci.driver.robot.exception.NotConnectedException;
import eu.vicci.driver.robot.location.Location;
import eu.vicci.driver.robot.location.UnnamedLocation;
import eu.vicci.driver.robot.util.Orientation;
import eu.vicci.driver.robot.util.Position;

/**
 * The {@link VicciRobotHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Andre Kuehnert - Initial contribution
 */
public class VicciRobotHandler extends BaseThingHandler {
    private Logger logger = LoggerFactory.getLogger(VicciRobotHandler.class);
    private static final String MSG_NOT_CONNECTED = "Cant establish a connection to robot";
    private static final String MSG_CONVERSION_FAILED = "Cant convert the move_to_location command to a position: {}";

    // ms for trying the reconnect
    private static final int reconnectDelay = 3000;

    // ms for the interval to check the connection state of the robot
    private static final int connectCheckingInterval = 1000;

    private Robot robot;
    private Location lastLocation;
    private volatile boolean wasConnected = false;

    public VicciRobotHandler(Thing thing, Robot robot) {
        super(thing);
        this.robot = robot;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command == RefreshType.REFRESH) {
            // sometimes refresh command is send at startup, will cause a error message
            return;
        }

        if (channelUID.getId().equals(CHANNEL_MOVE_TO_LOCATION)) {
            moveToLocation(command);
        } else if (channelUID.getId().equals(CHANNEL_CURRENT_LOCATION)) {
            setCurrentLocation(command);
        }
    }

    private void setCurrentLocation(Command command) {
        Location location = getLocation(command);
        if (location == null) {
            return;
        }
        try {
            robot.setLocation(location);
        } catch (NotConnectedException e) {
            logger.error(e.getMessage());
        }
    }

    private void moveToLocation(Command command) {
        Location location = getLocation(command);
        if (location == null) {
            updateMovementState(new ErrorState(command.toString()));
            return;
        }

        try {
            updateMovementState(new MovingState(command.toString()));
            robot.moveTo(location);
            // TODO after moving we must check if the robot has arrived the given location.
            // There is no error if the robot abort moving
            updateMovementState(new ArrivedState(command.toString()));
        } catch (NotConnectedException | CannotMoveToMovementTargetException e) {
            logger.error(e.getMessage());
            updateMovementState(new ErrorState(command.toString()));
        }
    }

    private void updateMovementState(MovementState state) {
        updateState(CHANNEL_MOVEMENT_STATE, state.toStringType());
    }

    private Location getLocation(Command command) {
        Position position = LocationUtil.getPosition(command);
        Orientation orientation = LocationUtil.getOrientation(command);
        if (position == null || orientation == null) {
            logger.error(MSG_CONVERSION_FAILED, command.toString());
            return null;
        }
        return new UnnamedLocation(position, orientation);
    }

    @Override
    public void initialize() {
        super.initialize();
        updateStatus(ThingStatus.INITIALIZING);
        tryConnect(200, true);
        startConnectionAndLocationPolling();
    }

    /**
     * Checks if connected, and starts reconnection if not connected
     */
    private void startConnectionAndLocationPolling() {
        scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                updateLocation();
                updateConnectStatus();
            }
        }, 2000, connectCheckingInterval, TimeUnit.MILLISECONDS);
    }

    private void updateLocation() {
        if (robot.getIsConnected()) {
            Location l = robot.getLocation();
            if (l != null && locationHasChanged(l)) {
                lastLocation = l;
                updateState(CHANNEL_CURRENT_LOCATION, new StringType(l.toString()));
            }

            if (l == null) {
                updateState(CHANNEL_CURRENT_LOCATION, new StringType("ERROR"));
            }
        }
    }

    private boolean locationHasChanged(Location newLocation) {
        if (lastLocation == null) {
            return true;
        }
        return !newLocation.equals(lastLocation);
    }

    private void updateConnectStatus() {
        if (robot.getIsConnected() && !wasConnected) {
            updateStatus(ThingStatus.ONLINE);
        }

        if (!robot.getIsConnected() && wasConnected) {
            boolean tryReconnect = wasConnected;
            wasConnected = false;
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, MSG_NOT_CONNECTED);
            if (tryReconnect) {
                tryConnect(0, false);
            }
        }
    }

    /**
     * Tries to connect, till a connection could be established
     *
     * @param delay
     */
    private void tryConnect(int delay, final boolean updateStatus) {
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    robot.connect();
                    updateStatus(ThingStatus.ONLINE);
                    wasConnected = true;
                } catch (NotConnectedException e) {
                    logger.error(e.getMessage());
                    if (updateStatus) {
                        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, MSG_NOT_CONNECTED);
                    }
                    tryConnect(reconnectDelay, false);
                }
            }
        }, delay, TimeUnit.MILLISECONDS);
    }
}
