/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package roborally.tiles.elements;

import roborally.effect.CancelMovement;
import roborally.effect.DamageEffect;

/**
 *
 * @author gross
 */
public class SpikeWall extends Wall {

    @Override
    public boolean parse(String parameters) {
        boolean result = super.parse(parameters);
        if (result) {
            addRobotMoveEffect(new DamageEffect(900, getTile(), 1));
            return true;
        } else {
            return false;
        }
    }
}
