/*
 * Flamer.java
 *
 */

package roborally.tiles.elements;

import java.util.EnumSet;
import roborally.Phase;
import roborally.Timing;
import roborally.effect.DamageEffect;
import roborally.tiles.TileElement;

/**
 *
 * @author Martin Groß
 */
public class Flamer extends TileElement {

    @Override
    public boolean parse(String parameters) {
        addEffect(Timing.LASER_FIRE, new DamageEffect(1000, getTile()));
        setActivePhases(EnumSet.noneOf(Phase.class));
        for (char c : parameters.toCharArray()) {
            if (Character.isDigit(c) && Character.getNumericValue(c) - 1 < Phase.values().length) {
                getActivePhases().add(Phase.values()[Character.getNumericValue(c) - 1]);
            } else {
                return false;
            }
        }
        return true;
    }
}
