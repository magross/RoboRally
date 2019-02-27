/*
 * ExpressConveyor.java
 *
 */
package roborally.tiles.elements;

import roborally.effect.MoveEffect;
import roborally.effect.RotateLeftEffect;
import roborally.effect.RotateRightEffect;
import roborally.tiles.Tile;

/**
 *
 * @author Martin Groß
 */
public class ExpressConveyor extends Conveyor {

    @Override
    public void finish() {
        super.finish();
        Tile successor = getTile().getAdjacentTile(successorDirection);
        if (successor != null) {
            AbstractTransportingElement successorElement = successor.getElementByType(AbstractTransportingElement.class);
            if (successorElement != null && (successorElement.getClass().isAssignableFrom(getClass()) || getClass().isAssignableFrom(successorElement.getClass())) && successorElement.getPredecessorDirections().contains(successorDirection.reverse())) {
                switch ((successorElement.getSuccessorDirection().ordinal() - successorDirection.ordinal() + 4) % 4) {
                    case 1:
                        addElementMoveEffect(new RotateRightEffect(650, getTile()));
                        break;
                    case 3:
                        addElementMoveEffect(new RotateLeftEffect(650, getTile()));
                        break;
                    default:
                }
            }
        }
    }

    @Override
    public boolean parse(String parameters) {
        boolean result = super.parse(parameters);
        if (result) {
            addElementMoveEffect(new MoveEffect(600, getTile(), successorDirection));
        }
        return result;
    }
}