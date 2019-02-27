/*
 * Robot.java
 *
 */
package roborally.robot;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import roborally.Direction;
import roborally.Timing;
import roborally.effect.Effect;
import roborally.effect.ExecuteMovementEffect;
import roborally.missile.LaserMissile;
import roborally.robot.Movement.Type;
import roborally.server.Player;
import roborally.tiles.Tile;
import roborally.tiles.TileElement;

/**
 *
 * @author Martin Groß
 */
public class Robot {

    private Tile archive;
    private int currentHealth;
    private boolean destroyed;
    private Direction direction;
    private int maximumHealth;
    private LinkedList<Movement> movements;
    private Player player;
    private boolean powerDownAnnouced;
    private boolean poweredDown;
    private int progress;
    private ProgramRegister[] registers;
    private Tile tile;

    public Robot(Player player) {
        this.movements = new LinkedList<Movement>();
        this.player = player;
        this.registers = new ProgramRegister[5];
        for (int i = 0; i < registers.length; i++) {
            registers[i] = new ProgramRegister();
        }
    }

    protected List<String> messages = new LinkedList<String>();

    public List<String> getMessages() {
        return messages;
    }

    public void executeProgramCard(ProgramCommand command) {
        messages.clear();
        movements.clear();
        switch (command) {
            case BACK:
                movements.add(new Movement(Type.BACK, true));
                break;
            case MOVE_1:
                movements.add(new Movement(Type.FORWARD, true));
                break;
            case MOVE_2:
                movements.add(new Movement(Type.FORWARD, true));
                movements.add(new Movement(Type.FORWARD, true));
                break;
            case MOVE_3:
                movements.add(new Movement(Type.FORWARD, true));
                movements.add(new Movement(Type.FORWARD, true));
                movements.add(new Movement(Type.FORWARD, true));
                break;
            case ROTATE_LEFT:
                movements.add(new Movement(Type.ROTATE_LEFT));
                break;
            case ROTATE_RIGHT:
                movements.add(new Movement(Type.ROTATE_RIGHT));
                break;
            case TURN_AROUND:
                movements.add(new Movement(Type.TURN_AROUND));
                break;
        }
        executeMovements(true);
    }

    public void move(Direction direction) {
        messages.clear();
        movements.clear();
        switch (direction) {
            case WEST:
                movements.add(new Movement(Direction.WEST));
                break;
            case NORTH:
                movements.add(new Movement(Direction.NORTH));
                break;
            case EAST:
                movements.add(new Movement(Direction.EAST));
                break;
            case SOUTH:
                movements.add(new Movement(Direction.SOUTH));
                break;
        }
        executeMovements(false);
    }

    public void executeMovements(boolean ownMovement) {
        if (movements.isEmpty()) {
            return;
        }
        LinkedList<Effect> effects = new LinkedList<Effect>();
        Tile destination = null;
        if (ownMovement) {
            effects.addAll(tile.getEffects(Timing.ROBOT_START));
            activateEffects(effects);
            effects.clear();
        }
        if (destroyed) {
            return;
        }
        //System.out.println("Robot: Movements " + movements);
        //for (int index = 0; index < movements.size(); index++) {
        while (!movements.isEmpty()) {
            //Movement movement = movements.get(index);
            Movement movement = movements.get(0);
            if (movement.isStationary()) {
                effects.add(new ExecuteMovementEffect(this));
            } else {
                effects.addAll(tile.getEffects(Timing.ROBOT_MOVE));
                effects.addAll(tile.getBorderEffects(movement.getDirection(this), Timing.ROBOT_MOVE));
                destination = movement.modifyTile(this);
                if (destination != null) {
                    effects.addAll(destination.getBorderEffects(movement.getDirection(this).reverse(), Timing.ROBOT_MOVE, this));
                    effects.add(new ExecuteMovementEffect(this));
                    effects.addAll(destination.getEffects(Timing.ROBOT_MOVE));
                } else {
                    effects.add(new ExecuteMovementEffect(this));
                }
            }
            //System.out.println("Effects: " + effects);
            activateEffects(effects);
            effects.clear();
            if (destroyed) {
                return;
            }
        }
        if (ownMovement && destination != null) {
            effects.addAll(destination.getEffects(Timing.ROBOT_STOP));
            activateEffects(effects);
            effects.clear();
        }
        activateEffects(effects);
    }

    protected void activateEffects(Queue<Effect> effects) {
        while (!effects.isEmpty() && !destroyed) {
            Effect effect = effects.poll();
            //System.out.println("Activate: " + effect.getClass().getSimpleName());
            effect.prepare();
            effect.activate();
            messages.addAll(effect.getMessages());
        }
    }

    public List<ProgramCard> extractProgramCards() {
        LinkedList<ProgramCard> result = new LinkedList<ProgramCard>();
        for (ProgramRegister register : getProgramableRegisters()) {
            ProgramCard card = register.getCard();
            if (card != null) {
                card.setRobot(null);
                register.setCard(null);
                result.add(card);
            }
        }
        return result;
    }

    public void fireWeapons() {
        if (tile != null && !isPoweredDown() && !isDestroyed()) {
            for (TileElement element : tile.getBorder(direction)) {
                if (element.isMissileBlocking()) {
                    return;
                }
            }
            Tile nextTile = tile.getAdjacentTile(direction);
            if (nextTile != null) {
                for (TileElement element : nextTile.getBorder(direction.reverse())) {
                    if (element.isMissileBlocking()) {
                        return;
                    }
                }
                LaserMissile missile = new LaserMissile(nextTile, direction);
                missile.move();
            }
        }
    }

    public Tile getArchive() {
        return archive;
    }

    public void setArchive(Tile tile) {
        if (!isPoweredDown() && !isDestroyed()) {
            archive = tile;
        }
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(int health) {
        if (health > maximumHealth) {
            health = maximumHealth;
        }
        currentHealth = health;
        if (currentHealth == 0) {
            setDestroyed(true);
        } else {
            for (int index = 4; index >= currentHealth - 1; index--) {
                registers[index].setActive(false);
            }
            for (int index = 0; index < currentHealth - 1 && index < 5; index++) {
                registers[index].setActive(true);
            }
        }
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
        if (destroyed) {
            currentHealth = 0;
            movements.clear();
            tile.setRobot(null);
            tile = null;
            if (player.getLives() == 0) {
                player.getGame().addDestroyedPlayer(player);
            }
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getMaximumHealth() {
        return maximumHealth;
    }

    public void setMaximumHealth(int maximumHealth) {
        this.maximumHealth = maximumHealth;
    }

    public List<Movement> getMovements() {
        return movements;
    }

    public int getNumberOfActiveRegisters() {
        int count = 0;
        for (ProgramRegister register : registers) {
            count += (register.isActive()) ? 1 : 0;
        }
        return count;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isPowerDownAnnounced() {
        return powerDownAnnouced;
    }

    public void setPowerDownAnnounced(boolean powerDownAnnouced) {
        this.powerDownAnnouced = powerDownAnnouced;
    }

    public boolean isPoweredDown() {
        return poweredDown;
    }

    public void setPoweredDown(boolean poweredDown) {
        this.poweredDown = poweredDown;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        if (!isPoweredDown() && !isDestroyed()) {
            this.progress = progress;
            if (player.getGame().getNumberOfCheckpoints() == progress) {
                player.getGame().addArrivedPlayer(player);
            }
        }
    }

    public ProgramRegister[] getProgramableRegisters() {
        ProgramRegister[] result = new ProgramRegister[getNumberOfActiveRegisters()];
        int index = 0;
        for (ProgramRegister register : registers) {
            if (register.isActive()) {
                result[index++] = register;
            }
        }
        return result;
    }

    public ProgramRegister[] getRegisters() {
        return registers;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        if (this.tile != null) {
            this.tile.setRobot(null);
        }
        this.tile = tile;
        if (this.tile == null) {
            setDestroyed(true);
        } else {
            this.tile.setRobot(this);
        }
    }

    public String toMessage() {
        if (tile == null) {
            return String.format("%1$s | %2$s | %3$s | %4$s | %5$s | %6$s | (%7$s, %8$s) | (%9$s,%10$s) | %11$s | %12$s | %13$s",
                    player.getName(),
                    player.getLives(),
                    currentHealth,
                    maximumHealth,
                    destroyed,
                    progress,
                    archive.getColumn(), archive.getRow(),
                    "null", "null",
                    direction,
                    powerDownAnnouced,
                    poweredDown);
        } else {
            return String.format("%1$s | %2$s | %3$s | %4$s | %5$s | %6$s | (%7$s, %8$s) | (%9$s,%10$s) | %11$s | %12$s | %13$s",
                    player.getName(),
                    player.getLives(),
                    currentHealth,
                    maximumHealth,
                    destroyed,
                    progress,
                    archive.getColumn(), archive.getRow(),
                    tile.getColumn(), tile.getRow(),
                    direction,
                    powerDownAnnouced,
                    poweredDown);
        }
    }

    @Override
    public String toString() {
        if (tile == null) {
            return String.format("Roboter von %1$s: %2$s/%3$s HP (%10$s), %4$s Checkpoints (%8$s, %9$s), (%5$s, %6$s) %7$s", player.getName(), currentHealth, maximumHealth, progress, "null", "null", direction, archive.getColumn(), archive.getRow(), destroyed);
        } else {
            return String.format("Roboter von %1$s: %2$s/%3$s HP (%10$s), %4$s Checkpoints (%8$s, %9$s), (%5$s, %6$s) %7$s", player.getName(), currentHealth, maximumHealth, progress, tile.getColumn(), tile.getRow(), direction, archive.getColumn(), archive.getRow(), destroyed);
        }
    }
}
