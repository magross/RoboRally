/*
 * StartingPoint.java
 *
 */
package roborally.tiles.elements;

import roborally.tiles.TileElement;

/**
 *
 * @author Martin Groß
 */
public class StartingPoint extends TileElement {

    private int number;

    public int getNumber() {
        return number;
    }

    @Override
    public String format() {
        return "" + (number + 1);
    }

    @Override
    public boolean parse(String parameters) {
        if (parameters.length() == 1 && Character.isDigit(parameters.charAt(0))) {
            try {
                number = Integer.parseInt(parameters) - 1;
                return true;
            } catch (Exception nfe) {
                return false;
            }
        } else {
            return false;
        }
    }
}
