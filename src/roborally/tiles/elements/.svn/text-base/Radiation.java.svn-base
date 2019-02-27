/*
 * Radiation.java
 *
 */

package roborally.tiles.elements;

import roborally.Timing;
import roborally.effect.DamageEffect;
import roborally.tiles.TileElement;

/**
 *
 * @author Martin Groß
 */
public class Radiation extends TileElement {

    @Override
    public boolean parse(String parameters) {
        if (parameters.isEmpty()) {
            addEffect(Timing.END_OF_TURN, new DamageEffect(1000, getTile()));
            return true;
        } else {
            return false;
        }
    }
}
