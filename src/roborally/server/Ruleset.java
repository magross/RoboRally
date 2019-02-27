/*
 * Ruleset.java
 *
 */

package roborally.server;

import java.util.EnumMap;
import roborally.robot.Robot;
import roborally.Choice.Type;

/**
 *
 * @author Martin Groß
 */
public class Ruleset {

    private EnumMap<Type, Long> timeouts;

    public Ruleset() {
        timeouts = new EnumMap<Type, Long>(Type.class);
        timeouts.put(Type.ANNOUNCE_POWER_DOWN, 60000L);
        timeouts.put(Type.PROGRAMMING, 300000L);
        timeouts.put(Type.REMAIN_POWERED_DOWN, 60000L);
        timeouts.put(Type.SPAWN_DIRECTION, 60000L);
        timeouts.put(Type.SPAWN_TILE, 1000L);
    }

    public int getNumberOfLives() {
        return 3;
    }

    public int getNumberOfProgramCards(Robot robot) {
        return 9 - (robot.getMaximumHealth() - robot.getCurrentHealth());
    }

    public int getHealthOnRespawn(Robot robot) {
        return 8;
    }

    public int getStartingHealth(Robot robot) {
        return 10;
    }

    public int getMaximumHealth(Robot robot) {
        return 10;
    }

    int getHealthOnPowerDown(Robot robot) {
        return 10;
    }

    public long getTimeout(Type choice) {
        return timeouts.get(choice);
    }

    public void setTimeout(Type type, long timeout) {
        timeouts.put(type, timeout);
    }

    int getNumberOfActiveRegisters(Robot robot) {
        return robot.getNumberOfActiveRegisters();
    }
}
