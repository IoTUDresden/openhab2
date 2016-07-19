package org.openhab.binding.viccirobot.handler;

import org.eclipse.smarthome.core.thing.Thing;

import eu.vicci.driver.nao.Nao;

public class NaoHandler extends VicciRobotHandler {

    public NaoHandler(Thing thing, String host, int port) {
        super(thing, new Nao(host, port));
    }

}
