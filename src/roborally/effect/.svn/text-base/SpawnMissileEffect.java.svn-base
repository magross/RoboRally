/*
 * SpawnMissileEffect.java
 *
 */
package roborally.effect;

import roborally.missile.Missile;
import roborally.robot.Robot;

/*
 *
 * @author Martin Groß
 */
public class SpawnMissileEffect extends Effect {

    private Missile missile;

    public SpawnMissileEffect(Missile missile) {
        this.missile = missile;
    }

    @Override
    public void activate() {
        //System.out.println("Activate: " + toString());
        missile.clone().move();
    }

    @Override
    public void activate(Robot robot) {
        activate();
    }

    @Override
    public String toString() {
        return "SpawnMissileEffect: " + missile.toString();
    }
}
