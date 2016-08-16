package org.openhab.binding.viccirobot.internal;

import eu.vicci.driver.robot.location.Location;
import eu.vicci.driver.robot.location.UnnamedLocation;
import eu.vicci.driver.robot.util.Orientation;
import eu.vicci.driver.robot.util.Position;

//TODO remove me if not needed anymore
public class TestClass {

    public static void main(String[] args) {
        String cmd = "P: 12.13 24.2 O: 23.11 78.1";
        String cmd2 = "P: 12.13 24.2 -1,222222222222 O: 23.11 78.1 -0.2223232323232 -0.00001";

        LocationUtil util = new LocationUtil(cmd);
        LocationUtil util2 = new LocationUtil(cmd2);

        Position position = util.getPosition();
        Orientation orientation = util.getOrientation();
        Location l1 = new UnnamedLocation(position, orientation);

        Position position2 = util2.getPosition();
        Orientation orientation2 = util2.getOrientation();
        Location l2 = new UnnamedLocation(position2, orientation2);

        System.out.println("L1:");
        System.out.println(LocationUtil.getFormatedString(l1));
        System.out.println("\nL2:");
        System.out.println(LocationUtil.getFormatedString(l2));
    }

}
