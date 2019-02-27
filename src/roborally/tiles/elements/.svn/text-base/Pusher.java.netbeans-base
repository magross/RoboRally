/*
 * Pusher.java
 *
 */
package roborally.tiles.elements;

import roborally.tiles.TileElement;
import java.util.EnumSet;
import roborally.Direction;
import roborally.Phase;
import roborally.Timing;
import roborally.effect.MoveEffect;

/**
 *
 * @author Martin Groß
 */
public class Pusher extends TileElement implements BorderElement {

    private Direction direction;

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

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
        addEffect(Timing.BOARD_ELEMENT_MOVE, new MoveEffect(300, getTile(), direction.reverse()));
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
