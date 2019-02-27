/*
 * MoveEffect.java
 *
 */
package roborally.effect;

import roborally.Direction;
import roborally.robot.Robot;
import roborally.tiles.Tile;

/**
 *
 * @author Martin Groß
 */
public class MoveEffect extends TileEffect {

    private Direction direction;
    private boolean reverse;

    public MoveEffect(int priority, Tile tile, Direction direction) {
        this(priority, tile, direction, false);
    }

    public MoveEffect(int priority, Tile tile, Direction direction, boolean reverse) {
        super(priority, tile);
        this.direction = direction;
        this.reverse = reverse;
    }

    @Override
    public void activate(Robot robot) {
        Direction dir = direction;
        if (dir == null) {
            dir = robot.getDirection();
        }
        if (reverse) {
            dir = dir.reverse();
        }
        Tile oldTile = robot.getTile();
        robot.move(dir);
        if (oldTile != robot.getTile()) {
            messages.add("ROBOT_MOVED | " + robot.getPlayer().getName() + " | " + dir);
        }
    }

    @Override
    public boolean isConflictingWith(Effect effect) {
        if (effect instanceof MoveEffect) {
            MoveEffect e = (MoveEffect) effect;
            if (getTile() == null || getTile().getRobot() == null) {
                return false;
            }
            if (e.getTile() == null || e.getTile().getRobot() == null) {
                return false;
            }
            Direction dir = direction;
            if (dir == null) {
                dir = getTile().getRobot().getDirection();
            }
            if (reverse) {
                dir = dir.reverse();
            }
            Direction dir2 = e.getDirection();
            if (dir2 == null) {
                dir2 = e.getTile().getRobot().getDirection();
            }
            if (e.isReverse()) {
                dir2 = dir2.reverse();
            }
            return getTile().getAdjacentTile(dir) == e.getTile().getAdjacentTile(dir2)
                    || (getTile().getAdjacentTile(dir) == e.getTile() && e.getTile().getAdjacentTile(dir2) == getTile())
                    || getTile() == e.getTile();
        } else {
            return false;
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isReverse() {
        return reverse;
    }
}
