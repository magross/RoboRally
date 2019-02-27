/*
 * TileEffect.java
 *
 */
package roborally.effect;

import roborally.robot.Robot;
import roborally.tiles.Tile;

/**
 *
 * @author Martin Groß
 */
public abstract class TileEffect extends Effect {

    private Robot robot;
    private Tile tile;

    public TileEffect(int priority, Tile tile) {
        super(priority);
        this.tile = tile;
    }

    @Override
    public void prepare() {
        super.prepare();
        if (tile != null) {
            robot = tile.getRobot();
        } else {
            robot = null;
        }
    }

    public void activate() {
        if (robot != null) {
            activate(robot);
            //messages.add(this.getClass().getSimpleName() + " | " + robot.toMessage());
        } else {
            if (r != null) {
                activate(r);
                //messages.add(this.getClass().getSimpleName() + " | " + r.toMessage());
            } 
        }
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }
}
