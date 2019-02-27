/*
 * ExecuteMovementEffect.java
 *
 */
package roborally.effect;

import roborally.Direction;
import roborally.robot.Movement;
import roborally.robot.Robot;
import roborally.tiles.Tile;

/**
 *
 * @author Martin Groß
 */
public class ExecuteMovementEffect extends RobotEffect {

    public ExecuteMovementEffect(Robot robot) {
        super(robot);
    }

    @Override
    public void activate(Robot robot) {
        //System.out.println("  EME: Movements: " + robot.getMovements());
        if (!robot.getMovements().isEmpty()) {
            Movement movement = robot.getMovements().remove(0);
            //System.out.println("  Movements after extraction: " + robot.getMovements());
            //System.out.println("  Current Movement: " + movement);
            //System.out.println("  Before: " + robot.getDirection() + " " + robot.getTile());
            Direction direction = movement.modifyDirection(robot);
            Tile tile = movement.modifyTile(robot);
            messages.add("EXECUTE_MOVEMENT | " + robot.getPlayer().getName() + " | " + movement);
            //System.out.println("  Trying to move: " + direction + " " + tile);
            if (tile == null) {
                //System.out.println("  Destroyed");
                messages.add("ROBOT_DESTROYED | " + robot.getPlayer().getName());
                robot.setDestroyed(true);
            } else {
                if (tile.getRobot() != null && movement.isPushing() && !movement.isStationary()) {
                    //System.out.println("  Pushing");
                    Tile oldTile = tile;
                    Robot otherRobot = tile.getRobot();
                    tile.getRobot().move(direction);
                    if (otherRobot.getTile() != oldTile) {
                        messages.add("PUSH_ROBOT | " + otherRobot.getPlayer().getName() + " | " + direction);
                    }
                }
                if (tile.getRobot() == null || movement.isStationary()) {
                    //System.out.println("  Moving");
                    robot.setDirection(direction);
                    robot.setTile(tile);
                    messages.add("MOVEMENT_SUCCESSFUL | " + robot.getPlayer().getName() + " | " + movement);
                } else {
                    messages.add("MOVEMENT_BLOCKED | " + robot.getPlayer().getName() + " | " + movement);
                }
            }
            //System.out.println("  After: " + robot.getDirection() + " " + robot.getTile());
            //messages.add(this.getClass().getSimpleName() + " | " + robot.toMessage());
        }
        //System.out.println(messages);
    }
}
