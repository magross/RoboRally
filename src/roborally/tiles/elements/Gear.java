/*
 * Gear.java
 *
 */
package roborally.tiles.elements;

import roborally.tiles.TileElement;
import roborally.Direction;
import roborally.tiles.Tile;
import roborally.effect.TileEffect;
import roborally.effect.RotateLeftEffect;
import roborally.effect.RotateRightEffect;
import roborally.effect.TurnAroundEffect;

/**
 *
 * @author Martin Groß
 */
public class Gear extends TileElement {

    private Type gearType;

    @Override
    public String format() {
        return super.format() + gearType.getAbbreviation();
    }

    @Override
    public boolean parse(String parameters) {
        for (Type t : Type.values()) {
            if (t.getAbbreviation().equals(parameters)) {
                gearType = t;
                addElementMoveEffect(gearType.createMoveEffect(getTile()));
                return true;
            }
        }
        return false;
    }

    public enum Type {

        ROTATE_LEFT("l") {

            @Override
            protected TileEffect createMoveEffect(Tile tile) {
                return new RotateLeftEffect(200, tile);
            }

            @Override
            protected Direction modifyDirection(Direction direction) {
                return direction.rotateLeft();
            }
        },
        ROTATE_RIGHT("r") {

            @Override
            protected TileEffect createMoveEffect(Tile tile) {
                return new RotateRightEffect(200, tile);
            }

            @Override
            protected Direction modifyDirection(Direction direction) {
                return direction.rotateRight();
            }
        },
        TURN_AROUND("t") {

            @Override
            protected TileEffect createMoveEffect(Tile tile) {
                return new TurnAroundEffect(200, tile);
            }

            @Override
            protected Direction modifyDirection(Direction direction) {
                return direction.reverse();
            }
        };
        private final String abbreviation;

        private Type(String abbreviation) {
            this.abbreviation = abbreviation;
        }

        public String getAbbreviation() {
            return abbreviation;
        }

        protected abstract TileEffect createMoveEffect(Tile tile);

        protected abstract Direction modifyDirection(Direction direction);
    }
}
