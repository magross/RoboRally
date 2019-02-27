/*
 * Missile.java
 *
 */
package roborally.missile;

import java.util.LinkedList;
import java.util.List;
import roborally.Direction;
import roborally.effect.Effect;
import roborally.robot.Robot;
import roborally.tiles.Tile;
import roborally.tiles.TileElement;

/**
 *
 * @author Martin Groß
 */
public abstract class Missile {

    protected boolean destroyed;
    protected Direction direction;
    protected List<Effect> effects;
    protected int speed;
    protected Tile tile;

    public Missile(Tile tile, Direction direction) {
        this(tile, direction, new LinkedList<Effect>(), 1000);
    }

    public Missile(Tile tile, Direction direction, List<Effect> effects) {
        this(tile, direction, effects, 1000);
    }

    public Missile(Tile tile, Direction direction, List<Effect> effects, int speed) {
        this.destroyed = (tile == null);
        this.direction = direction;
        this.effects = effects;
        this.speed = speed;
        this.tile = tile;
    }

    @Override
    public abstract Missile clone();

    public void activateEffects(Robot robot) {
        for (Effect effect : effects) {
            effect.activate(robot);
        }
    }

    public void move() {
        int count = speed;
        Tile t = tile;
        while (count > 0 && !destroyed) {
            if (t == null) {
                destroyed = true;
            } else if (t.getRobot() != null) {
                activateEffects(t.getRobot());
                destroyed = true;
            } else {
                for (TileElement element : t.getBorder(direction)) {
                    if (element.isMissileBlocking()) {
                        destroyed = true;
                    }
                }
                t = t.getAdjacentTile(direction);
                if (t != null) {
                    for (TileElement element : t.getBorder(direction.reverse())) {
                        if (element.isMissileBlocking()) {
                            destroyed = true;
                        }
                    }
                }
            }
            count--;
        }
    }

    @Override
    public String toString() {
        return tile.toString() + " " + direction.toString();
    }
}
