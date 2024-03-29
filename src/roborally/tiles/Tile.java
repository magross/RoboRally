/*
 * Tile.java
 *
 */
package roborally.tiles;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import roborally.Direction;
import roborally.Phase;
import roborally.Timing;
import roborally.effect.Effect;
import roborally.robot.Robot;
import roborally.tiles.elements.Pit;

/**
 *
 * @author Martin Groß
 */
public class Tile {

    private Tile[] adjacentTiles;
    private List<TileElement>[] borders;
    private List<TileElement> elements;
    private int column;
    private Phase phase;
    private Robot robot;
    private int row;

    public Tile(int column, int row) {
        adjacentTiles = new Tile[Direction.values().length];
        borders = new List[Direction.values().length];
        for (int i = 0; i < borders.length; i++) {
            borders[i] = new LinkedList<TileElement>();
        }
        elements = new LinkedList<TileElement>();
        this.column = column;
        this.row = row;
    }

    public void add(TileElement element) {
        elements.add(element);
    }

    public Tile getAdjacentTile(Direction direction) {
        return adjacentTiles[direction.ordinal()];
    }

    public void setAdjacentTile(Direction direction, Tile tile) {
        adjacentTiles[direction.ordinal()] = tile;
    }

    public List<TileElement> getBorder(Direction direction) {
        return borders[direction.ordinal()];
    }

    public void setBorder(Direction direction, List<TileElement> border) {
        borders[direction.ordinal()] = border;
    }

    public int getColumn() {
        return column;
    }

    public List<TileElement> getElements() {
        return elements;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public Robot getRobot() {
        return robot;
    }

    public void setRobot(Robot robot) {
        this.robot = robot;
    }

    public int getRow() {
        return row;
    }

    public List<Direction> getPossibleSpawnDirections() {
        List<Direction> result = new LinkedList<Direction>();
        for (Direction direction : Direction.values()) {
            Tile tile = this;
            boolean free = true;
            for (int i = 0; i < 3; i++) {
                boolean blocked = false;
                for (TileElement element : tile.getBorder(direction)) {
                    if (element.isMissileBlocking()) {
                        blocked = true;
                        break;
                    }
                }
                tile = getAdjacentTile(direction);
                if (tile != null) {
                    for (TileElement element : tile.getBorder(direction.reverse())) {
                        if (element.isMissileBlocking()) {
                            blocked = true;
                            break;
                        }
                    }
                }
                if (blocked) {
                    break;
                }
                if (tile == null) {
                    free = true;
                    break;
                } else if (tile.getRobot() != null) {
                    free = false;
                    break;
                }
            }
            if (free) {
                result.add(direction);
            }
        }
        return result;
    }

    public boolean isValidSpawnPoint() {
        if (getRobot() != null) {
            return false;
        }
        for (TileElement element : elements) {
            if (element instanceof Pit) {
                return false;
            }
        }
        return !getPossibleSpawnDirections().isEmpty();
    }

    public String getDetailedMessage() {
        StringBuilder result = new StringBuilder();
        if (elements.isEmpty()) {
            result.append("_");
        } else if (elements.size() == 1) {
            result.append(elements.get(0).toString());
        } else {
            result.append("(");
            boolean first = true;
            for (TileElement element : elements) {
                if (first) {
                    first = false;
                } else {
                    result.append(" ");
                }
                result.append(element.toString());
            }
            result.append(")");
        }
        StringBuilder border = new StringBuilder("");
        border.append("[");
        boolean first = true;
        for (Direction direction : Direction.values()) {
            if (first) {
                first = false;
            } else {
                //border.append(" ");
            }
            if (getBorder(direction).isEmpty()) {
                border.append("_");
            } else if (getBorder(direction).size() == 1) {
                border.append(getBorder(direction).get(0).toString());
            } else {
                border.append("(");
                boolean f = true;
                for (TileElement element : getBorder(direction)) {
                    if (f) {
                        f = false;
                    } else {
                        //border.append(" ");
                    }
                    border.append(element.toString());
                }
                border.append(")");
            }
        }
        border.append("]");
        if (!border.toString().equals("[____]")) {
            result.append(border.toString());
        }
        return result.toString();
    }

    public List<Tile> getPossibleSpawnPoints() {
        List<Tile> result = new LinkedList<Tile>();
        Tile tile = this;
        if (getRobot() == null) {
            result.add(tile);
            return result;
        } else {
            List<Tile> possibleTiles = new LinkedList<Tile>();
            Queue<Tile> nextQueue = new LinkedList<Tile>();
            Map<Tile, Boolean> visited = new HashMap<Tile, Boolean>();
            nextQueue.add(tile);
            visited.put(tile, true);
            while (possibleTiles.isEmpty()) {
                Queue<Tile> currentQueue = new LinkedList<Tile>();
                currentQueue.addAll(nextQueue);
                nextQueue.clear();
                while (!currentQueue.isEmpty()) {
                    tile = currentQueue.poll();
                    for (Direction direction : Direction.values()) {
                        Tile adjacent = tile.getAdjacentTile(direction);
                        if (adjacent == null || visited.containsKey(adjacent)) {
                            continue;
                        }
                        if (adjacent.getRobot() == null && adjacent.isValidSpawnPoint()) {
                            possibleTiles.add(adjacent);
                        } else {
                            nextQueue.add(adjacent);
                        }
                        visited.put(adjacent, true);
                    }
                }
            }
            return possibleTiles;
        }
    }

    @Override
    public Tile clone() {
        Tile clone = new Tile(column, row);
        clone.adjacentTiles = adjacentTiles;
        clone.borders = borders;
        clone.elements = elements;
        return clone;
    }

    @Override
    public String toString() {
        if (1 == 1) {
            return "(" + column + ", " + row + ")";
        }
        StringBuilder result = new StringBuilder();
        if (elements.isEmpty()) {
            result.append("_");
        } else if (elements.size() == 1) {
            result.append(elements.get(0).toString());
        } else {
            result.append("(");
            boolean first = true;
            for (TileElement element : elements) {
                if (first) {
                    first = false;
                } else {
                    result.append(" ");
                }
                result.append(element.toString());
            }
            result.append(")");
        }
        StringBuilder border = new StringBuilder("");
        border.append("[");
        boolean first = true;
        for (Direction direction : Direction.values()) {
            if (first) {
                first = false;
            } else {
                border.append(" ");
            }
            if (getBorder(direction).isEmpty()) {
                border.append("_");
            } else if (getBorder(direction).size() == 1) {
                border.append(getBorder(direction).get(0).toString());
            } else {
                border.append("(");
                boolean f = true;
                for (TileElement element : getBorder(direction)) {
                    if (f) {
                        f = false;
                    } else {
                        border.append(" ");
                    }
                    border.append(element.toString());
                }
                border.append(")");
            }
        }
        border.append("]");
        if (!border.toString().equals("[_ _ _ _]")) {
            result.append(border.toString());
        }
        return result.toString();
    }

    public <T extends TileElement> T getElementByType(Class<T> elementType) {
        for (TileElement e : elements) {
            if (elementType.isAssignableFrom(e.getClass())) {
                return (T) e;
            }
        }
        return null;
    }

    public List<Effect> getEffects(Timing timing) {
        List<Effect> result = new LinkedList<Effect>();
        for (TileElement element : elements) {
            if (element.getActivePhases().contains(phase)) {
                result.addAll(element.getEffects(timing));
            }
        }
        if (timing == Timing.BOARD_ELEMENT_MOVE || timing == Timing.LASER_FIRE) {
            for (Direction direction : Direction.values()) {
                for (TileElement element : getBorder(direction)) {
                    if (element.getActivePhases().contains(phase)) {
                        result.addAll(element.getEffects(timing));
                    }
                }
            }
        }
        Collections.sort(result);
        return result;
    }

    public List<Effect> getBorderEffects(Direction direction, Timing timing) {
        List<Effect> result = new LinkedList<Effect>();
        for (TileElement element : getBorder(direction)) {
            if (element.getActivePhases().contains(phase)) {
                result.addAll(element.getEffects(timing));
            }
        }
        Collections.sort(result);
        return result;
    }

    public List<Effect> getBorderEffects(Direction direction, Timing timing, Robot robot) {
        List<Effect> result = new LinkedList<Effect>();
        for (TileElement element : getBorder(direction)) {
            if (element.getActivePhases().contains(phase)) {
                result.addAll(element.getEffects(timing));
            }
        }
        Collections.sort(result);
        for (Effect effect : result) {
            effect.setRobot(robot);
        }
        return result;
    }
}
