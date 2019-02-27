/*
 * Pit.java
 *
 */
package roborally.tiles.elements;

import roborally.tiles.TileElement;
import roborally.effect.DestroyEffect;

/**
 *
 * @author Martin Groß
 */
public class Pit extends TileElement {

    @Override
    public boolean parse(String parameters) {
        if (parameters.isEmpty()) {
            addRobotMoveEffect(new DestroyEffect(1000, getTile()));
            return true;
        } else {
            return false;
        }
    }
}
