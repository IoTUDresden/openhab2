/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.viccirobot.internal;

import static org.openhab.binding.viccirobot.VicciRobotBindingConstants.*;

import java.util.Collections;
import java.util.Set;

import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.openhab.binding.viccirobot.handler.NaoHandler;
import org.openhab.binding.viccirobot.handler.TurtlebotHandler;
import org.openhab.binding.viccirobot.handler.YoubotHandler;

/**
 * The {@link VicciRobotHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Andre Kuehnert - Initial contribution
 */
public class VicciRobotHandlerFactory extends BaseThingHandlerFactory {

    private final static Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.singleton(THING_TYPE_ROBOT);

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(THING_TYPE_ROBOT)) {
            Object roboType = thing.getConfiguration().get(PARAMETER_ROBOT_TYPE);
            int port = (int) thing.getConfiguration().get(PARAMETER_ROBOT_PORT);
            String host = (String) thing.getConfiguration().get(PARAMETER_ROBOT_HOST);
            return createHandler(thing, roboType, host, port);
        }
        return null;
    }

    private ThingHandler createHandler(Thing thing, Object roboType, String host, int port) {
        if (ROBOT_TYPE_TURTLEBOT.equals(roboType)) {
            return new TurtlebotHandler(thing, host, port);
        }
        if (ROBOT_TYPE_YOUBOT.equals(roboType)) {
            return new YoubotHandler(thing, host, port);
        }
        if (ROBOT_TYPE_NAO.equals(roboType)) {
            return new NaoHandler(thing, host, port);
        }
        return null;
    }
}
