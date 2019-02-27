/*
 * AbstractTransportingElement.java
 *
 */
package roborally.tiles.elements;

import java.util.EnumSet;
import java.util.Set;
import roborally.Direction;
import roborally.tiles.Tile;
import roborally.tiles.TileElement;
import roborally.effect.MoveEffect;
import roborally.effect.RotateLeftEffect;
import roborally.effect.RotateRightEffect;

/**
 *
 * @author Martin Groß
 */
public abstract class AbstractTransportingElement extends TileElement implements ContextElement {

    protected Set<Direction> predecessorDirections;
    protected Direction successorDirection;

    @Override
    public void finish() {        
        Tile successor = getTile().getAdjacentTile(successorDirection);
        if (successor != null) {
            AbstractTransportingElement successorElement = successor.getElementByType(AbstractTransportingElement.class);
            if (successorElement != null && successorElement.getPredecessorDirections().contains(successorDirection.reverse())) {
                //System.out.println("EW " + getTile());
            }
            if (successorElement != null && (successorElement.getClass().isAssignableFrom(getClass()) || getClass().isAssignableFrom(successorElement.getClass())) && successorElement.getPredecessorDirections().contains(successorDirection.reverse())) {
                switch ((successorElement.getSuccessorDirection().ordinal() - successorDirection.ordinal() + 4) % 4) {
                    case 1:
                        //System.out.println("EX1 " + getTile());
                        addElementMoveEffect(new RotateRightEffect(550, getTile()));
                        break;
                    case 3:
                        //System.out.println("EX2 " + getTile());
                        addElementMoveEffect(new RotateLeftEffect(550, getTile()));
                        break;
                    default:
                        //System.out.println("EX " + getTile());
                }
            }
        }
    }

    public Set<Direction> getPredecessorDirections() {
        return predecessorDirections;
    }

    public Direction getSuccessorDirection() {
        return successorDirection;
    }

    @Override
    public String format() {
        String result = super.format() + successorDirection.name().substring(0, 1).toLowerCase();
        for (Direction direction : predecessorDirections) {
            result += direction.name().substring(0, 1).toLowerCase();
        }
        return result;
    }

    @Override
    public boolean parse(String parameters) {
        if (parameters.isEmpty()) {
            return false;
        } else {
            successorDirection = null;
            for (Direction d : Direction.values()) {
                if (d.toString().toLowerCase().startsWith(parameters.substring(0, 1))) {
                    successorDirection = d;
                    addElementMoveEffect(new MoveEffect(500, getTile(), successorDirection));
                    predecessorDirections = EnumSet.of(successorDirection.reverse());
                    break;
                }
            }
            if (successorDirection == null) {
                return false;
            }
            if (parameters.length() > 1) {
                predecessorDirections = EnumSet.noneOf(Direction.class);
                for (int i = 1; i < parameters.length(); i++) {
                    Direction dir = null;
                    for (Direction d : Direction.values()) {
                        if (d.toString().toLowerCase().startsWith(parameters.substring(i, i + 1))) {
                            dir = d;
                            break;
                        }
                    }
                    if (dir == null) {
                        return false;
                    } else {
                        predecessorDirections.add(dir);
                    }
                }
            }
            return true;
        }
    }
}
