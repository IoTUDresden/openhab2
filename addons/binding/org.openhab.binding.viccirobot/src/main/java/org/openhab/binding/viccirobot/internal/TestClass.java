package org.openhab.binding.viccirobot.internal;

import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.types.Command;

import eu.vicci.driver.robot.util.Orientation;
import eu.vicci.driver.robot.util.Position;

//TODO remove me if not needed anymore
public class TestClass {

    public static void main(String[] args) {
        String cmd = "P: 12.13 24.2 O: 23.11 78.1";
        Command c = new StringType(cmd);
        Position position = LocationUtil.getPosition(c);
        Orientation orientation = LocationUtil.getOrientation(c);
    }

}
