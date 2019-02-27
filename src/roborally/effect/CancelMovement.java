/*
 * CancelMovement.java
 *
 */

package roborally.effect;

import roborally.robot.Robot;
import roborally.tiles.Tile;

/**
 *
 * @author Martin Groß
 */
public class CancelMovement extends TileEffect {

    public CancelMovement(int priority, Tile tile) {
        super(priority, tile);
    }

    @Override
    public void activate(Robot robot) {
        //System.out.println("  CM Before: " + robot.getMovements());
        while (!robot.getMovements().isEmpty() && !robot.getMovements().get(0).isStationary()) {
            messages.add("MOVEMENT_BLOCKED | " + robot.getPlayer().getName());
            robot.getMovements().remove(0);
        }
        //System.out.println("  CM After: " + robot.getMovements());
    }
}
