/*
 * Water.java
 *
 */

package roborally.tiles.elements;

import roborally.Timing;
import roborally.effect.SlowMovement;
import roborally.tiles.TileElement;

/**
 *
 * @author Martin Groß
 */
public class Water extends TileElement {

    @Override
    public boolean parse(String parameters) {
        if (parameters.isEmpty()) {
            addEffect(Timing.ROBOT_START, new SlowMovement(1000, getTile()));
            return true;
        } else {
            return false;
        }
    }
}
