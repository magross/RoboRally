/*
 * Repair.java
 *
 */
package roborally.tiles.elements;

import roborally.Timing;
import roborally.tiles.TileElement;
import roborally.effect.ArchiveEffect;
import roborally.effect.RepairEffect;

/**
 *
 * @author Martin Groß
 */
public class Repair extends TileElement {

    @Override
    public boolean parse(String parameters) {
        if (parameters.isEmpty()) {
            addEffect(Timing.ROBOT_MOVE, new ArchiveEffect(1000, getTile()));
            addEffect(Timing.END_OF_TURN, new RepairEffect(1000, getTile()));
            return true;
        } else {
            return false;
        }
    }
}
