package org.openhab.binding.viccirobot.handler;

import org.eclipse.smarthome.core.thing.Thing;

import eu.vicci.driver.turtlebot.TurtleBot;

public class TurtlebotHandler extends VicciRobotHandler {

    public TurtlebotHandler(Thing thing, String host, int port) {
        super(thing, new TurtleBot(host, port));
    }

}
