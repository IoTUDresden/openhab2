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

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.viccirobot.internal.LocationUtil;
import org.openhab.binding.viccirobot.internal.MovementState;
import org.openhab.binding.viccirobot.internal.MovementState.ArrivedState;
import org.openhab.binding.viccirobot.internal.MovementState.ErrorState;
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
    private static final int connectCheckingInterval = 3000;

    private Robot robot;
    private volatile boolean wasConnected = false;

    public VicciRobotHandler(Thing thing, Robot robot) {
        super(thing);
        this.robot = robot;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (channelUID.getId().equals(CHANNEL_MOVE_TO_LOCATION)) {
            moveToLocation(command);
        } else if (channelUID.getId().equals(CHANNEL_SET_CURRENT_LOCATION)) {
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
            robot.moveTo(location); // TODO This Method should Block, after that, the robot should have reached his
                                    // position
            // maybe we need also to lock, till the robot is finished
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
        tryConnect(200);
        startConnectionStatePolling();
    }

    /**
     * Checks if connected, and starts reconnection if not connected
     */
    private void startConnectionStatePolling() {
        scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (robot.getIsConnected() && !wasConnected) {
                    updateStatus(ThingStatus.ONLINE);
                }

                if (!robot.getIsConnected() && wasConnected) {
                    boolean tryReconnect = wasConnected;
                    wasConnected = false;
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, MSG_NOT_CONNECTED);
                    if (tryReconnect) {
                        tryConnect(0);
                    }
                }
            }
        }, 2000, connectCheckingInterval, TimeUnit.MILLISECONDS);
    }

    /**
     * Trys to connect, till a connection could be established
     *
     * @param delay
     */
    private void tryConnect(int delay) {
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    robot.connect();
                    updateStatus(ThingStatus.ONLINE);
                    wasConnected = true;
                } catch (NotConnectedException e) {
                    logger.error(e.getMessage());
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, MSG_NOT_CONNECTED);
                    tryConnect(reconnectDelay);
                }
            }
        }, delay, TimeUnit.MILLISECONDS);
    }
}
