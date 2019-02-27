/*
 * DamageEffect.java
 *
 */
package roborally.effect;

import roborally.robot.Robot;
import roborally.tiles.Tile;

/**
 *
 * @author Martin Groß
 */
public class DamageEffect extends TileEffect {

    private int amount;

    public DamageEffect(int priority, Tile tile) {
        this(priority, tile, 1);
    }

    public DamageEffect(int priority, Tile tile, int amount) {
        super(priority, tile);
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public void activate(Robot robot) {
        messages.add("ROBOT_DAMAGED | " + robot.getPlayer().getName() + " | " + amount);
        robot.setCurrentHealth(robot.getCurrentHealth() - amount);
        if (robot.getCurrentHealth() == 0) {
            messages.add("ROBOT_DESTROYED | " + robot.getPlayer().getName());
        }
    }
}
