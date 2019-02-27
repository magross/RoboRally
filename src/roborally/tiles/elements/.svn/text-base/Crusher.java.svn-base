/*
 * Crusher.java
 *
 */
package roborally.tiles.elements;

import roborally.tiles.TileElement;
import java.util.EnumSet;
import roborally.Phase;
import roborally.effect.DestroyEffect;

/**
 *
 * @author Martin Groß
 */
public class Crusher extends TileElement {

    @Override
    public String format() {
        if (getActivePhases().equals(EnumSet.noneOf(Phase.class))) {
            return "";
        } else {
            StringBuilder result = new StringBuilder();
            for (Phase phase : getActivePhases()) {
                result.append("" + (phase.ordinal() + 1));
            }
            return result.toString();
        }
    }

    @Override
    public boolean parse(String parameters) {
        addElementMoveEffect(new DestroyEffect(100, getTile()));
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
