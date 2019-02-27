/*
 * Movement.java
 *
 */
package roborally.robot;

import java.util.EnumSet;
import roborally.Direction;
import roborally.tiles.Tile;

/**
 *
 * @author Martin Groß
 */
public class Movement {

    public enum Type {

        BACK,
        DIRECTION,
        FORWARD,
        ROTATE_LEFT,
        ROTATE_RIGHT,
        TURN_AROUND;
    }
    private final Direction direction;
    private final boolean pushing;
    private final Type type;

    public Movement(Direction direction) {
        this(Type.DIRECTION, direction, false);
    }

    public Movement(Type type) {
        this(type, null, false);
    }

    public Movement(Type type, boolean pushing) {
        this(type, null, pushing);
    }

    public Movement(Type type, Direction direction, boolean pushing) {
        this.direction = direction;
        this.pushing = pushing;
        this.type = type;
    }

    public Direction modifyDirection(Robot robot) {
        switch (type) {
            case ROTATE_LEFT:
                return robot.getDirection().rotateLeft();
            case ROTATE_RIGHT:
                return robot.getDirection().rotateRight();
            case TURN_AROUND:
                return robot.getDirection().reverse();
            default:
                return robot.getDirection();
        }
    }

    public Tile modifyTile(Robot robot) {
        if (getDirection(robot) == null) {
            return robot.getTile();
        } else {
            return robot.getTile().getAdjacentTile(getDirection(robot));
        }
    }

    public Direction getDirection(Robot robot) {
        switch (type) {
            case BACK:
                return robot.getDirection().reverse();
            case DIRECTION:
                return direction;
            case FORWARD:
                return robot.getDirection();
            default:
                return null;
        }
    }

    public boolean isStationary() {
        return EnumSet.of(Type.ROTATE_LEFT, Type.ROTATE_RIGHT, Type.TURN_AROUND).contains(type);
    }

    public boolean isPushing() {
        return pushing;
    }

    public String toString() {
        return type.toString();
    }
}
