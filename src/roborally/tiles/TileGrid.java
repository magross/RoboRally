/*
 * TileGrid.java
 *
 */
package roborally.tiles;

import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import roborally.Direction;
import roborally.tiles.elements.Checkpoint;
import roborally.tiles.elements.StartingPoint;

/**
 *
 * @author Martin Groß
 */
public class TileGrid {

    private Tile[][] grid;
    private Tile[] startingPoints;
    private int numberOfCheckpoints;

    public TileGrid() {
        grid = new Tile[0][0];
        startingPoints = new Tile[8];
    }

    public TileGrid(String[] lines) throws ParseException {
        this();
        int rows = 0;
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            rows++;
        }
        grid = new Tile[rows][];
        int row = 0;
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            List<String> tokens = splitIntoTokens(line);
            List<Tile> tiles;
            try {
                tiles = createTiles(tokens, row);
                grid[row++] = tiles.toArray(new Tile[0]);
            } catch (Exception e) {
                throw new ParseException("Could not parse: " + line, 0);
            }
        }
        finishGrid();
    }

    public int getNumberOfCheckpoints() {
        return numberOfCheckpoints;
    }

    private void finishGrid() {
        numberOfCheckpoints = 0;
        for (int row = 0; row < grid.length; row++) {
            for (int column = 0; column < grid[row].length; column++) {
                if (row > 0) {
                    grid[row][column].setAdjacentTile(Direction.NORTH, get(row - 1, column));
                }
                if (column < grid[row].length - 1) {
                    grid[row][column].setAdjacentTile(Direction.EAST, get(row, column + 1));
                }
                if (row < grid.length - 1) {
                    grid[row][column].setAdjacentTile(Direction.SOUTH, get(row + 1, column));
                }
                if (column > 0) {
                    grid[row][column].setAdjacentTile(Direction.WEST, get(row, column - 1));
                }
                for (TileElement element : grid[row][column].getElements()) {
                    if (element instanceof StartingPoint) {
                        if (((StartingPoint) element).getNumber() >= startingPoints.length) {
                            Tile[] temp = new Tile[((StartingPoint) element).getNumber() + 1];
                            System.arraycopy(startingPoints, 0, temp, 0, startingPoints.length);
                            startingPoints = temp;
                        }
                        startingPoints[((StartingPoint) element).getNumber()] = grid[row][column];
                    }
                }
                for (TileElement element : grid[row][column].getElements()) {
                    if (element instanceof Checkpoint) {
                        numberOfCheckpoints++;
                    }
                }
            }
        }
        for (int row = 0; row < grid.length; row++) {
            for (int column = 0; column < grid[row].length; column++) {
                for (TileElement element : grid[row][column].getElements()) {
                    element.finish();
                }
            }
        }
    }

    @Override
    public TileGrid clone() {
        TileGrid clone = new TileGrid();
        if (grid.length > 0) {
            clone.grid = new Tile[grid.length][grid[0].length];
            for (int row = 0; row < grid.length; row++) {
                for (int column = 0; column < grid[0].length; column++) {
                    clone.grid[row][column] = grid[row][column].clone();
                }
            }
            clone.startingPoints = new Tile[8];
        }
        return clone;
    }

    public Tile get(int row, int column) {
        return grid[row][column];
    }

    public int getHeight() {
        return grid.length;
    }

    public Tile getStartingPoint(int index) {
        return startingPoints[index];
    }

    public int getWidth() {
        return (grid.length == 0) ? 0 : grid[0].length;
    }

    private void split(List<String> tokens, String section) {
        String c = "";
        if (section.contains("[")) {
            c = "[";
        } else if (section.contains("]")) {
            c = "]";
        } else if (section.contains("(")) {
            c = "(";
        } else if (section.contains(")")) {
            c = ")";
        } else {
            tokens.add(section);
            return;
        }
        String first = section.substring(0, section.indexOf(c));
        String second = section.substring(section.indexOf(c) + 1);
        if (!first.isEmpty()) {
            split(tokens, first);
        }
        tokens.add(c);
        if (!second.isEmpty()) {
            split(tokens, second);
        }
    }

    private List<String> splitIntoTokens(String string) {
        String[] sections = string.split("\\s+");
        LinkedList<String> tokens = new LinkedList<String>();
        for (String section : sections) {
            split(tokens, section);
        }
        return tokens;
    }

    private List<Tile> createTiles(List<String> tokens, int row) {
        boolean border = false;
        boolean group = false;
        int tileIndex = 0, borderIndex = 0;
        Tile tile = null;
        List<Tile> tiles = new LinkedList<Tile>();
        for (String string : tokens) {
            if (string.equals("[")) {
                border = true;
            } else if (string.equals("]")) {
                border = false;
            } else if (string.equals("(")) {
                group = true;
                if (!border) {
                    if (tile != null) {
                        tiles.add(tile);
                    }
                    tile = new Tile(tileIndex++, row);
                    borderIndex = 0;
                }
            } else if (string.equals(")")) {
                group = false;
                if (border) {
                    borderIndex++;
                }
            } else {
                if (!group && !border) {
                    if (tile != null) {
                        tiles.add(tile);
                    }
                    tile = new Tile(tileIndex++, row);
                    borderIndex = 0;
                }
                String abbreviation = "", parameters = "";
                for (char c : string.toCharArray()) {
                    if (Character.isDigit(c) || Character.isLowerCase(c)) {
                        parameters += c;
                    } else {
                        abbreviation += c;
                    }
                }
                if (border) {
                    tile.getBorder(Direction.values()[borderIndex]).add(TileElement.create(abbreviation, parameters, tile, Direction.values()[borderIndex]));
                    if (!group) {
                        borderIndex++;
                    }
                } else {
                    TileElement element = TileElement.create(abbreviation, parameters, tile);
                    tile.add(element);
                }
            }
        }
        tiles.add(tile);
        return tiles;
    }

    public String getDetailedMessage() {
        StringBuilder result = new StringBuilder();
        for (Tile[] row : grid) {
            boolean first = true;
            for (Tile tile : row) {
                if (first) {
                    first = false;
                } else {
                    result.append(" ");
                }
                result.append(tile.getDetailedMessage());
            }
            result.append(" | ");
        }
        return result.toString();
    }

    public Iterable<Tile> getTiles() {
        return new Iterable<Tile>() {

            @Override
            public Iterator<Tile> iterator() {
                return new Iterator<Tile>() {

                    private int column = -1;
                    private int row = 0;

                    @Override
                    public boolean hasNext() {
                        return !(row == grid.length - 1 && column == grid[row].length - 1);
                    }

                    @Override
                    public Tile next() {
                        column++;
                        if (column >= grid[0].length) {
                            column = 0;
                            row++;
                        }
                        return grid[row][column];
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("This needs to be implemented.");
                    }
                };
            }
        };
    }
}
