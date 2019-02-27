/*
 * Laser.java
 *
 */

package roborally.tiles.elements;

import roborally.Direction;
import roborally.Timing;
import roborally.effect.SpawnMissileEffect;
import roborally.missile.LaserMissile;
import roborally.tiles.TileElement;

/**
 *
 * @author Martin Groß
 */
public class Laser extends TileElement implements BorderElement {

    private Direction direction;

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public boolean parse(String parameters) {
        if (parameters.isEmpty()) {
            addEffect(Timing.LASER_FIRE, new SpawnMissileEffect(new LaserMissile(getTile(), direction.reverse())));
            return false;
        } else {
            return true;
        }
    }
}
