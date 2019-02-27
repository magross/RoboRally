/*
 * DestroyEffect.java
 *
 */
package roborally.effect;

import roborally.robot.Robot;
import roborally.tiles.Tile;

/**
 *
 * @author Martin Groß
 */
public class DestroyEffect extends TileEffect {

    public DestroyEffect(int priority, Tile tile) {
        super(priority, tile);
    }

    @Override
    public void activate(Robot robot) {
        messages.add("ROBOT_DESTROYED | " + robot.getPlayer().getName());
        robot.setDestroyed(true);
    }
}
