/*
 * Wall.java
 *
 */
package roborally.tiles.elements;

import roborally.Direction;
import roborally.tiles.TileElement;
import roborally.effect.CancelMovement;

/**
 *
 * @author Martin Groß
 */
public class Wall extends TileElement implements BorderElement {

    protected Direction direction;

    @Override
    public boolean isMissileBlocking() {
        return true;
    }

    @Override
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public boolean parse(String parameters) {
        if (parameters.isEmpty()) {
            addRobotMoveEffect(new CancelMovement(1000, getTile()));
            return true;
        } else {
            return false;
        }
    }
}
