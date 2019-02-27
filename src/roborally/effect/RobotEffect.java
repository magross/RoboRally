/*
 * RobotEffect.java
 *
 */

package roborally.effect;

import roborally.robot.Robot;

/**
 *
 * @author Martin Groß
 */
public abstract class RobotEffect extends Effect {

    private Robot robot;

    public RobotEffect(Robot robot) {
        this.robot = robot;
    }

    @Override
    public void activate() {
        activate(robot);        
    }
}
