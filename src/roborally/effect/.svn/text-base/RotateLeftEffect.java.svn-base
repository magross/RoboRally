/*
 * RotateLeftEffect.java
 *
 */
package roborally.effect;

import roborally.robot.Robot;
import roborally.tiles.Tile;

/**
 *
 * @author Martin Groß
 */
public class RotateLeftEffect extends TileEffect {

    public RotateLeftEffect(int priority, Tile tile) {
        super(priority, tile);
    }

    @Override
    public void activate(Robot robot) {
        messages.add("ROTATE_LEFT | " + robot.getPlayer().getName());
        robot.setDirection(robot.getDirection().rotateLeft());
    }
}
