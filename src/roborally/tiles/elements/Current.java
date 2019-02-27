/*
 * Current.java
 *
 */
package roborally.tiles.elements;

import roborally.effect.SlowMovement;

/**
 *
 * @author Martin Groß
 */
public class Current extends AbstractTransportingElement {

    @Override
    public boolean parse(String parameters) {
        boolean result = super.parse(parameters);
        addRobotStartEffect(new SlowMovement(1000, getTile()));
        return result;
    }
}
