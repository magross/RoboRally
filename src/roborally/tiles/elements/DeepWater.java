/*
 * DeepWater.java
 *
 */

package roborally.tiles.elements;

import roborally.Timing;
import roborally.effect.DamageEffect;

/**
 *
 * @author Martin Groß
 */
public class DeepWater extends Water {

    @Override
    public boolean parse(String parameters) {
        boolean result = super.parse(parameters);
        addEffect(Timing.LASER_FIRE, new DamageEffect(1000, getTile()));
        return result;
    }
}
