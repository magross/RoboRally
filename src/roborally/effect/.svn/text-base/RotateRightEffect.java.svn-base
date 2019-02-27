/*
 * RotateRightEffect.java
 *
 */
package roborally.effect;

import roborally.robot.Robot;
import roborally.tiles.Tile;

/**
 *
 * @author Martin Groß
 */
public class RotateRightEffect extends TileEffect {

    public RotateRightEffect(int priority, Tile tile) {
        super(priority, tile);
    }

    @Override
    public void activate(Robot robot) {
        messages.add("ROTATE_RIGHT | " + robot.getPlayer().getName());
        robot.setDirection(robot.getDirection().rotateRight());
    }
}
