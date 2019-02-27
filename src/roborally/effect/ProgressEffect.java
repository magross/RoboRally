/*
 * ProgressEffect.java
 *
 */
package roborally.effect;

import roborally.robot.Robot;
import roborally.tiles.Tile;

/**
 *
 * @author Martin Groß
 */
public class ProgressEffect extends TileEffect {

    private int stage;

    public ProgressEffect(int priority, Tile tile, int stage) {
        super(priority, tile);
        this.stage = stage;
    }

    public int getStage() {
        return stage;
    }

    @Override
    public void activate(Robot robot) {
        if (robot.getProgress() == stage) {
            messages.add("CHECKPOINT_REACHED | " + robot.getPlayer().getName());
            robot.setProgress(stage + 1);
        }
    }
}
