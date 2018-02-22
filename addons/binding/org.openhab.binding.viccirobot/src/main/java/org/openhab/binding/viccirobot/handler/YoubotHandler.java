package org.openhab.binding.viccirobot.handler;

import org.eclipse.smarthome.core.thing.Thing;

import eu.vicci.driver.youbot.YouBot;

public class YoubotHandler extends VicciRobotHandler {

    public YoubotHandler(Thing thing, String host, int port) {
        super(thing, new YouBot(host, port));
    }

}
