package org.openhab.binding.viccirobot.internal;

import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.types.Command;

import eu.vicci.driver.robot.util.Orientation;
import eu.vicci.driver.robot.util.Position;

public class LocationUtil {

    private LocationUtil() {
    }

    /**
     * Creates {@link Position} from String command.
     * Command should be a String command and look like the following (without quotes):
     * <br>
     * <br>
     * "P: 23,01 15,5 O: 15,0 13,34"
     * <br>
     * <br>
     * ( P: double x, double y O: double o_z, double o_w )
     *
     * @param command
     * @return
     */
    public static Position getPosition(Command command) {
        if (!(command instanceof StringType)) {
            return null;
        }
        String cmd = command.toString();
        int index = cmd.indexOf("O:");
        if (index < 0) {
            return null;
        }
        cmd = cmd.substring(0, index);
        index = cmd.indexOf("P:");
        if (index < 0) {
            return null;
        }

        cmd = cmd.substring(index + 2);
        cmd = cmd.trim();
        Double[] val = getXYPair(cmd);
        if (val == null) {
            return null;
        }
        return new Position(val[0], val[1]);
    }

    /**
     * Creates {@link Orientation} from String command.
     * Command should be a String command and look like the following (without quotes):
     * <br>
     * <br>
     * "P: 23,01 15,5 O: 15,0 13,34"
     * <br>
     * <br>
     * ( P: double x, double y O: double o_z, double o_w )
     *
     * @param command
     * @return
     */
    public static Orientation getOrientation(Command command) {
        if (!(command instanceof StringType)) {
            return null;
        }
        String cmd = command.toString();
        int index = cmd.indexOf("O:");
        if (index < 0) {
            return null;
        }

        cmd = cmd.substring(index + 2);
        cmd = cmd.trim();

        Double[] val = getXYPair(cmd);
        if (val == null) {
            return null;
        }
        return new Orientation(val[0], val[1]);
    }

    private static Double[] getXYPair(String cmd) {
        cmd = cmd.replaceAll(",", "."); // only dots in Double.valueOf allowed
        int index = cmd.indexOf(" ");
        if (index < 0) {
            return null;
        }
        String xS = cmd.substring(0, index);
        xS = xS.trim();
        String yS = cmd.substring(index, cmd.length());
        yS = yS.trim();

        try {
            Double xD = Double.valueOf(xS);
            Double yD = Double.valueOf(yS);
            return new Double[] { xD, yD };
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
