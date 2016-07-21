/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.viccirobot;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link VicciRobotBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Andre Kuehnert - Initial contribution
 */
public class VicciRobotBindingConstants {

    public static final String BINDING_ID = "viccirobot";

    // List of all Thing Type UIDs
    public final static ThingTypeUID THING_TYPE_ROBOT = new ThingTypeUID(BINDING_ID, "robot");

    // List of all Channel ids
    public final static String CHANNEL_CURRENT_LOCATION = "currentLocation";
    public final static String CHANNEL_MOVE_TO_LOCATION = "moveToLocation";
    public final static String CHANNEL_MOVEMENT_STATE = "movementState";

    // List of all parameters
    public final static String PARAMETER_ROBOT_TYPE = "robotType";
    public final static String PARAMETER_ROBOT_HOST = "robotHost";
    public final static String PARAMETER_ROBOT_PORT = "robotPort";

    public final static String ROBOT_TYPE_TURTLEBOT = "turtlebot";
    public final static String ROBOT_TYPE_NAO = "nao";
    public final static String ROBOT_TYPE_YOUBOT = "youbot";

}
