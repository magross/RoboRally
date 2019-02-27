/*
 * Checkpoint.java
 *
 */
package roborally.tiles.elements;

import roborally.Timing;
import roborally.tiles.TileElement;
import roborally.effect.ArchiveEffect;
import roborally.effect.ProgressEffect;
import roborally.effect.RepairEffect;

/**
 *
 * @author Martin Groß
 */
public class Checkpoint extends TileElement {

    private int number;

    @Override
    public String format() {
        return super.format() + (number + 1);
    }

    @Override
    public boolean parse(String parameters) {
        if (parameters.length() == 1 && Character.isDigit(parameters.charAt(0))) {
            number = Integer.parseInt(parameters) - 1;
            addEffect(Timing.ROBOT_MOVE, new ArchiveEffect(1000, getTile()));
            addEffect(Timing.ROBOT_MOVE, new ProgressEffect(1000, getTile(), number));
            addEffect(Timing.END_OF_TURN, new RepairEffect(1000, getTile()));
            return true;
        } else {
            return false;
        }
    }
}
