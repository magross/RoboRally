/*
 * SlowMovement.java
 *
 */

package roborally.effect;

import roborally.robot.Robot;
import roborally.tiles.Tile;

/**
 *
 * @author Martin Groß
 */
public class SlowMovement extends TileEffect {

    public SlowMovement(int priority, Tile tile) {
        super(priority, tile);
    }

    @Override
    public void activate(Robot robot) {
        if (!robot.getMovements().isEmpty() && !robot.getMovements().get(0).isStationary()) {
            robot.getMovements().remove(0);
        }
    }
}
