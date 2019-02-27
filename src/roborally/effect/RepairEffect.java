/*
 * RepairEffect.java
 *
 */
package roborally.effect;

import roborally.robot.Robot;
import roborally.tiles.Tile;

/**
 *
 * @author Martin Groß
 */
public class RepairEffect extends TileEffect {

    private int amount;

    public RepairEffect(int priority, Tile tile) {
        this(priority, tile, 1);
    }

    public RepairEffect(int priority, Tile tile, int amount) {
        super(priority, tile);
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public void activate(Robot robot) {
        messages.add("ROBOT_REPAIRED | " + robot.getPlayer().getName());
        robot.setCurrentHealth(robot.getCurrentHealth() + amount);
    }
}
