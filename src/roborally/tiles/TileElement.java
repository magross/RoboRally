/*
 * TileElement.java
 *
 */
package roborally.tiles;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import roborally.Direction;
import roborally.effect.EffectManager;
import roborally.Phase;
import roborally.Timing;
import roborally.effect.Effect;
import roborally.effect.TileEffect;
import roborally.tiles.elements.BorderElement;
import roborally.tiles.elements.Checkpoint;
import roborally.tiles.elements.Conveyor;
import roborally.tiles.elements.Crusher;
import roborally.tiles.elements.Current;
import roborally.tiles.elements.DeepCurrent;
import roborally.tiles.elements.DeepWater;
import roborally.tiles.elements.Empty;
import roborally.tiles.elements.ExpressConveyor;
import roborally.tiles.elements.Flamer;
import roborally.tiles.elements.Gear;
import roborally.tiles.elements.Laser;
import roborally.tiles.elements.Pit;
import roborally.tiles.elements.Pusher;
import roborally.tiles.elements.Radiation;
import roborally.tiles.elements.RadioactiveWaste;
import roborally.tiles.elements.Repair;
import roborally.tiles.elements.SpikeWall;
import roborally.tiles.elements.StartingPoint;
import roborally.tiles.elements.Upgrade;
import roborally.tiles.elements.Wall;
import roborally.tiles.elements.Water;

/**
 * 
 * @author Martin Groß
 */
public abstract class TileElement {

    public enum Type {

        CHECKPOINT(Checkpoint.class, "CP"),
        CONVEYOR(Conveyor.class, "C"),
        CRUSHER(Crusher.class, "CR"),
        CURRENT(Current.class, "CU"),
        DEEP_CURRENT(DeepCurrent.class, "DC"),
        DEEP_WATER(DeepWater.class, "DW"),
        EMPTY(Empty.class, "_"),
        EXPRESS_CONVEYOR(ExpressConveyor.class, "E"),
        FLAMER(Flamer.class, "F"),
        GEAR(Gear.class, "G"),
        LASER(Laser.class, "L"),
        PIT(Pit.class, "P"),
        PUSHER(Pusher.class, "PU"),
        RADIATION(Radiation.class, "RA"),
        RADIOACTIVE_WASTE(RadioactiveWaste.class, "RW"),
        REPAIR(Repair.class, "R"),
        SPIKE_WALL(SpikeWall.class, "S"),
        STARTING_POINT(StartingPoint.class, "SP"),
        UPGRADE(Upgrade.class, "U"),
        WALL(Wall.class, "W"),
        WATER(Water.class, "WA");
        private final String abbreviation;
        private final Class<? extends TileElement> type;

        private Type(Class<? extends TileElement> type, String abbreviation) {
            this.abbreviation = abbreviation;
            this.type = type;
        }

        public String getAbbreviation() {
            return abbreviation;
        }

        public Class<? extends TileElement> getType() {
            return type;
        }
    }

    public static TileElement create(String abbreviation, String parameters, Tile tile) {
        return create(abbreviation, parameters, tile, null);
    }

    public static TileElement create(String abbreviation, String parameters, Tile tile, Direction direction) {
        for (Type type : Type.values()) {
            if (type.getAbbreviation().equals(abbreviation)) {
                try {
                    Constructor<? extends TileElement> constructor = type.getType().getConstructor();
                    TileElement element = constructor.newInstance();
                    if (element instanceof BorderElement) {
                        ((BorderElement) element).setDirection(direction);
                    }
                    element.setTile(tile);
                    element.setType(type);
                    element.parse(parameters);
                    return element;
                } catch (InstantiationException ex) {
                    Logger.getLogger(TileElement.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(TileElement.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(TileElement.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(TileElement.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchMethodException ex) {
                    Logger.getLogger(TileElement.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SecurityException ex) {
                    Logger.getLogger(TileElement.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println(abbreviation + " " + parameters + " " + tile);
                return null;
            }
        }
        System.out.println(abbreviation + " " + parameters + " " + tile);
        return null;
    }
    private EnumSet<Phase> activePhases;
    private EffectManager effects;
    private Tile tile;
    private Type type;

    public TileElement() {
        this.activePhases = EnumSet.allOf(Phase.class);
        this.effects = new EffectManager();
    }

    public void finish() {
    }

    public void addElementMoveEffect(TileEffect effect) {
        effects.add(Timing.BOARD_ELEMENT_MOVE, effect);
    }

    public void addRobotMoveEffect(TileEffect effect) {
        effects.add(Timing.ROBOT_MOVE, effect);
    }

    public void addRobotStartEffect(TileEffect effect) {
        effects.add(Timing.ROBOT_START, effect);
    }

    public void addEffect(Timing timing, Effect effect) {
        effects.add(timing, effect);
    }

    public EnumSet<Phase> getActivePhases() {
        return activePhases;
    }

    protected void setActivePhases(EnumSet<Phase> activePhases) {
        this.activePhases = activePhases;
    }

    public List<Effect> getEffects(Timing timing) {
        return effects.get(timing);
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isMissileBlocking() {
        return false;
    }

    public String format() {
        if (activePhases.equals(EnumSet.allOf(Phase.class))) {
            return "";
        } else {
            StringBuilder result = new StringBuilder(type.getAbbreviation());
            for (Phase phase : getActivePhases()) {
                result.append("" + (phase.ordinal() + 1));
            }
            return result.toString();
        }
    }

    public boolean parse(String parameters) {
        return parameters.isEmpty();
    }

    @Override
    public String toString() {
        return type.getAbbreviation() + format();
    }
}
