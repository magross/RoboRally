/*
 * DeepCurrent.java
 *
 */
package roborally.tiles.elements;

import roborally.Timing;
import roborally.effect.DamageEffect;

/**
 *
 * @author Martin Groß
 */
public class DeepCurrent extends Current {

    @Override
    public boolean parse(String parameters) {
        boolean result = super.parse(parameters);
        addEffect(Timing.LASER_FIRE, new DamageEffect(1000, getTile()));
        return result;
    }
}
