/*
 * ArchiveEffect.java
 *
 */
package roborally.effect;

import roborally.robot.Robot;
import roborally.tiles.Tile;

/**
 *
 * @author Martin Groß
 */
public class ArchiveEffect extends TileEffect {

    public ArchiveEffect(int priority, Tile tile) {
        super(priority, tile);
    }

    @Override
    public void activate(Robot robot) {
        messages.add("ARCHIVE_REACHED | " + robot.getPlayer().getName());
        robot.setArchive(robot.getTile());
    }
}
