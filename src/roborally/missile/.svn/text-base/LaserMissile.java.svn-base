/*
 * LaserMissile.java
 *
 */

package roborally.missile;

import roborally.Direction;
import roborally.effect.DamageEffect;
import roborally.tiles.Tile;

/**
 *
 * @author Martin Groß
 */
public class LaserMissile extends Missile {

    public LaserMissile(Tile tile, Direction direction) {
        super(tile, direction);
        effects.add(new DamageEffect(1000, tile));
    }

    @Override
    public Missile clone() {
        return new LaserMissile(tile, direction);
    }

}
