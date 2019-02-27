/*
 * Direction.java
 *
 */

package roborally;

/**
 *
 * @author Martin Groß
 */
public enum Direction {    
    NORTH,
    EAST,
    SOUTH,
    WEST;

    public Direction rotateLeft() {
        return Direction.values()[(ordinal() + values().length - 1) % values().length];
    }

    public Direction rotateRight() {
        return Direction.values()[(ordinal() + 1) % values().length];
    }

    public Direction reverse() {
        return Direction.values()[(ordinal() + values().length / 2) % values().length];
    }
}
