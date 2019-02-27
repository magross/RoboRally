/*
 * TurnAroundEffect.java
 *
 */
package roborally.effect;

import roborally.robot.Robot;
import roborally.tiles.Tile;

/**
 *
 * @author Martin Groß
 */
public class TurnAroundEffect extends TileEffect {

    public TurnAroundEffect(int priority, Tile tile) {
        super(priority, tile);
    }

    @Override
    public void activate(Robot robot) {
        messages.add("TURN_AROUND | " + robot.getPlayer().getName());
        robot.setDirection(robot.getDirection().reverse());
    }
}
