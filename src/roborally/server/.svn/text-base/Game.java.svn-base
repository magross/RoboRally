/*
 * Game.java
 *
 */
package roborally.server;

import roborally.ChoiceManager;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import roborally.Deck;
import roborally.robot.ProgramRegister;
import roborally.robot.ProgramCard;
import roborally.robot.Robot;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import roborally.AtomicList;
import roborally.Choice;
import roborally.Direction;
import roborally.Phase;
import roborally.Timing;
import roborally.tiles.Tile;
import roborally.tiles.TileGrid;
import roborally.effect.Effect;
import roborally.robot.ProgramCommand;
import roborally.Choice.Type;
import static roborally.server.ServerMessage.*;

/**
 * 
 * @author Martin Groß
 */
public class Game {

    /**
     * The name of this instance.
     */
    private final String name;
    /**
     * The password required to join this game.
     */
    private final String password;
    /**
     * The list of players participating in this game.
     */
    private final AtomicList<Player> players;
    private Random random;
    private Ruleset ruleset;
    /**
     * Whether this game is currently running.
     */
    private boolean running;
    private Server server;
    /**
     * The scenario this game is based on.
     */
    private final Scenario scenario;
    /**
     * The thread executing the actual game.
     */
    private final GameThread thread;

    /**
     * Creates a new game instance based on the specified scenario with the 
     * specified name.
     * @param scenario the scenario this game is based on.
     * @param name the name for this instance of the scenario.
     */
    public Game(Server server, Scenario scenario, String name) {
        this(server, scenario, name, "");
    }

    /**
     * Creates a new game instance based on the specified scenario without
     * password.
     * @param scenario the scenario this game is based on.
     * @param name the name for this instance of the scenario.
     * @param password the password required for joining this game.
     */
    public Game(Server server, Scenario scenario, String name, String password) {
        this.name = name;
        this.password = password;
        this.players = new AtomicList<Player>(Player.class);
        this.random = new Random();
        this.ruleset = new Ruleset();
        this.running = false;
        this.scenario = scenario;
        this.server = server;
        this.thread = new GameThread();
    }

    /**
     * Returns the name for this instance of the scenario.
     * @return the name for this instance of the scenario.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the password required for joining this game or an empty string if
     * no password has been specified.
     * @return the password required for joining this game or an empty string if
     * no password has been specified.
     */
    public String getPassword() {
        return password;
    }

    public Player[] getActivePlayers() {
        Player[] result;
        synchronized (players) {
            int count = players.size();
            for (Player player : players.elements()) {
                if (player.getGameState() != Player.GameState.ACTIVE) {
                    count--;
                }
            }
            result = new Player[count];
            count = 0;
            for (Player player : players.elements()) {
                if (player.getGameState() == Player.GameState.ACTIVE) {
                    result[count++] = player;
                }
            }
        }
        return result;
    }

    /**
     * Returns an array containing the players of the game. This array is a copy
     * of the list of players maintained internally.
     * @return an array containing the players of the game.
     */
    public Player[] getPlayers() {
        return players.elements();
    }

    public Ruleset getRuleset() {
        return ruleset;
    }

    /**
     * Returns the scenario this instance of the game is based on.
     * @return the scenario this instance of the game is based on.
     */
    public Scenario getScenario() {
        return scenario;
    }

    /**
     * Returns whether this game is currently running.
     * @return whether this game is currently running.
     */
    public synchronized boolean isRunning() {
        return running;
    }

    public boolean join(Player player) {
        synchronized (players) {
            if (scenario.getMaximumNumberOfPlayers() > players.size() && !running) {
                players.add(player);
                player.setGame(this);
                player.setState(PlayerState.PLAYING);
                thread.manager.broadcast(PLAYER_JOINED, player.getName());
                return true;
            } else {
                return false;
            }
        }
    }

    public void leave(Player player) {
        synchronized (players) {
            if (players.contains(player)) {
                players.remove(player);
                if (player.getState().contains(PlayerState.GAME_MASTER) && !player.getGame().isRunning() && !players.isEmpty()) {
                    Player nextPlayer = players.getFirst();
                    nextPlayer.addState(PlayerState.GAME_MASTER);
                    thread.manager.broadcast(GAME_MASTER_STATUS_GRANTED, nextPlayer.getName());
                }
                player.setState(PlayerState.REGISTERED);
                player.setGame(null);
                thread.manager.decideAllOpenChoices(player);
                thread.manager.broadcast(PLAYER_LEFT, player.getName());
                player.sendMessage(PLAYER_LEFT, player.getName());
            }
            if (players.size() == 0) {
                server.stopGame(name);
                //thread.stop();
            }
        }
    }

    public void broadcastMessage(Player sender, String message) {
        for (Player player : players.elements()) {
            player.sendMessage(GAME_CHAT_MESSAGE, sender.getName(), message);
        }
    }

    public void broadcastMessage(ServerMessage message, Object... parameters) {
        thread.manager.broadcast(message, parameters);
    }

    public boolean sendChoice(Player player, String type, String choice) {
        try {
            Choice.Type ct = Choice.Type.valueOf(type.toUpperCase().trim());
            return thread.processChoice(player, ct, choice);
        } catch (Exception e) {
            player.sendMessage(UNKNOWN_CHOICE, type);
            return false;
        }
    }

    public synchronized void start() {
        if (running == true) {
            return;
        } else {
            running = true;
            thread.start();
        }
    }

    public String toMessage() {
        StringBuilder result = new StringBuilder();
        result.append(scenario.getDetailedMessage());
        for (Player player : getPlayers()) {
            result.append("; ");
            result.append(player.getName());
        }
        return result.toString();
    }

    public void setSeed(long seed) {
        random = new Random(seed);
        //players.setRandom(random);
    }
    private LinkedList<Player> destroyedInCurrentRound = new LinkedList<Player>();
    private LinkedList<Player> arrivedInCurrentRound = new LinkedList<Player>();

    public void addArrivedPlayer(Player player) {
        arrivedInCurrentRound.add(player);
    }

    public void addDestroyedPlayer(Player player) {
        destroyedInCurrentRound.add(player);
    }

    public int getNumberOfCheckpoints() {
        return thread.grid.getNumberOfCheckpoints();
    }

    private class GameThread extends Thread {

        private ChoiceManager manager;
        private Deck<ProgramCard> deck;
        private TileGrid grid;
        private int turn;
        private Ruleset rules;
        private List<Player> arrivedPlayers = new LinkedList<Player>();
        private List<Player> destroyedPlayers = new LinkedList<Player>();

        public GameThread() {
            manager = new ChoiceManager(Game.this.players);
            rules = Game.this.ruleset;
        }

        public synchronized boolean processChoice(Player player, Type type, String string) {
            return manager.process(player, type, string);
        }

        private void createDeck() {
            deck = new Deck<ProgramCard>(random);
            for (int priority = 10; priority <= 60; priority += 10) {
                deck.addCard(new ProgramCard(ProgramCommand.TURN_AROUND, priority));
            }
            for (int priority = 70; priority <= 410; priority += 20) {
                deck.addCard(new ProgramCard(ProgramCommand.ROTATE_LEFT, priority));
            }
            for (int priority = 80; priority <= 420; priority += 20) {
                deck.addCard(new ProgramCard(ProgramCommand.ROTATE_RIGHT, priority));
            }
            for (int priority = 430; priority <= 480; priority += 10) {
                deck.addCard(new ProgramCard(ProgramCommand.BACK, priority));
            }
            for (int priority = 490; priority <= 660; priority += 10) {
                deck.addCard(new ProgramCard(ProgramCommand.MOVE_1, priority));
            }
            for (int priority = 670; priority <= 780; priority += 10) {
                deck.addCard(new ProgramCard(ProgramCommand.MOVE_2, priority));
            }
            for (int priority = 790; priority <= 840; priority += 10) {
                deck.addCard(new ProgramCard(ProgramCommand.MOVE_3, priority));
            }
            deck.createStack();
        }

        private void chooseStartingDirections() {
            for (Player player : getActivePlayers()) {
                Robot robot = player.getRobot();
                manager.requestChoice(player, new Choice<Direction>(Type.SPAWN_DIRECTION, robot.getTile().getPossibleSpawnDirections()));
            }
            manager.waitForAllOpenChoices(rules.getTimeout(Type.SPAWN_DIRECTION));
            for (Player player : getActivePlayers()) {
                Robot robot = player.getRobot();
                Choice<Direction> choice = manager.extractMadeChoice(player, Type.SPAWN_DIRECTION);
                List<Direction> directions = choice.getChosenObjects();
                Direction direction = directions.get(0);
                robot.setDirection(direction);
            }
        }

        private void respawnDestroyedRobots() {
            for (Player player : getActivePlayers()) {
                Robot robot = player.getRobot();
                if (robot.isDestroyed()) {
                    if (player.getLives() > 0) {
                        manager.requestChoice(player, new Choice<Tile>(Type.SPAWN_TILE, 1, robot.getArchive().getPossibleSpawnPoints()), rules.getTimeout(Type.SPAWN_TILE), TimeUnit.MILLISECONDS).get(0);
                        Tile spawnPoint = (Tile) manager.extractMadeChoice(player, Type.SPAWN_TILE).getChosenObjects().get(0);
                        robot.setTile(spawnPoint);
                        spawnPoint.setRobot(robot);
                        manager.requestChoice(player, new Choice<Direction>(Type.SPAWN_DIRECTION, 1, spawnPoint.getPossibleSpawnDirections()), rules.getTimeout(Type.SPAWN_DIRECTION), TimeUnit.MILLISECONDS).get(0);
                        Direction direction = (Direction) manager.extractMadeChoice(player, Type.SPAWN_DIRECTION).getChosenObjects().get(0);
                        robot.setDirection(direction);
                        robot.setCurrentHealth(rules.getHealthOnRespawn(robot));
                        robot.setDestroyed(false);
                        player.setLives(player.getLives() - 1);
                    }
                }
            }
        }

        private void askForContinuedPowerDowns() {
            for (Player player : getActivePlayers()) {
                Robot robot = player.getRobot();
                if (robot.isPoweredDown()) {
                    manager.requestChoice(player, new Choice<Boolean>(Type.REMAIN_POWERED_DOWN, true, false));
                }
            }
            manager.waitForAllOpenChoices(rules.getTimeout(Type.REMAIN_POWERED_DOWN));
            for (Player player : getActivePlayers()) {
                Robot robot = player.getRobot();
                if (robot.isPoweredDown()) {
                    Boolean remainPoweredDown = (Boolean) manager.extractMadeChoice(player, Type.REMAIN_POWERED_DOWN).getChosenObjects().get(0);
                    robot.setPowerDownAnnounced(remainPoweredDown);
                }
            }
        }

        private void processPowerDownAnnouncements() {
            for (Player player : getActivePlayers()) {
                Robot robot = player.getRobot();
                if (robot.isPowerDownAnnounced()) {
                    robot.setCurrentHealth(rules.getHealthOnPowerDown(robot));
                    robot.setPowerDownAnnounced(false);
                    robot.setPoweredDown(true);
                } else {
                    robot.setPoweredDown(false);
                }
            }
        }

        private void dealProgramCards() {
            for (Player player : getActivePlayers()) {
                Robot robot = player.getRobot();
                if (!robot.isPoweredDown()) {
                    List<ProgramCard> cards = deck.drawCards(rules.getNumberOfProgramCards(robot));
                    for (ProgramCard card : cards) {
                        card.setRobot(robot);
                    }
                    player.setProgramCards(cards);
                    manager.requestChoice(player, new Choice(Type.PROGRAMMING, rules.getNumberOfActiveRegisters(robot), cards));
                }
            }
        }

        private void preparePrograms() {
            for (Player player : getActivePlayers()) {
                Robot robot = player.getRobot();
                if (!robot.isPoweredDown()) {
                    List<ProgramCard> cards = manager.extractMadeChoice(player, Type.PROGRAMMING).getChosenObjects();
                    int indexX = 0;
                    for (ProgramRegister register : robot.getProgramableRegisters()) {
                        ProgramCard card = cards.get(indexX++);
                        register.setCard(card);
                    }
                    player.getProgramCards().removeAll(cards);
                    deck.discardCards(player.getProgramCards());
                }
            }
        }

        private void askForPowerDownAnnouncements() {
            for (Player player : getActivePlayers()) {
                Robot robot = player.getRobot();
                if (!robot.isDestroyed() && !robot.isPoweredDown()) {
                    manager.requestChoice(player, new Choice<Boolean>(Type.ANNOUNCE_POWER_DOWN, true, false));
                }
            }
            manager.waitForAllOpenChoices(rules.getTimeout(Type.ANNOUNCE_POWER_DOWN));
            for (Player player : getActivePlayers()) {
                Robot robot = player.getRobot();
                if (!robot.isDestroyed() && !robot.isPoweredDown()) {
                    Boolean announcePowerDown = (Boolean) manager.extractMadeChoice(player, Type.ANNOUNCE_POWER_DOWN).getChosenObjects().get(0);
                    robot.setPowerDownAnnounced(announcePowerDown);
                }
            }
        }

        private void updateTilePhase(Phase phase) {
            for (Tile tile : grid.getTiles()) {
                tile.setPhase(phase);
            }
        }

        private List<Player> executeProgramCards(Phase phase) {
            LinkedList<ProgramCard> queue = new LinkedList<ProgramCard>();
            LinkedList<Player> order = new LinkedList<Player>();
            for (Player player : getActivePlayers()) {
                Robot robot = player.getRobot();
                if (!robot.isDestroyed() && !robot.isPoweredDown()) {
                    queue.add(robot.getRegisters()[phase.ordinal()].getCard());
                }
            }
            Collections.sort(queue);
            LinkedList<String> messages = new LinkedList<String>();
            for (ProgramCard card : queue) {
                Robot robot = card.getRobot();
                order.add(robot.getPlayer());
                if (!robot.isDestroyed()) {
                    manager.broadcast(EXECUTING_PROGRAM_CARD, card.toMessage());
                    robot.executeProgramCard(card.getCommand());
                    messages.addAll(robot.getMessages());
                }
            }
            for (String message : messages) {
                manager.broadcast(EFFECT_OCCURRED, message);
            }
            return order;
        }

        private List<String> processEffects(Timing timing) {
            List<String> messages = new LinkedList<String>();
            PriorityQueue<Effect> effects = new PriorityQueue<Effect>();
            for (Tile tile : grid.getTiles()) {
                effects.addAll(tile.getEffects(timing));
            }
            while (!effects.isEmpty()) {
                Effect effect = effects.poll();
                List<Effect> currentEffects = new LinkedList<Effect>();
                currentEffects.add(effect);
                while (!effects.isEmpty() && effects.peek().getPriority() == effect.getPriority()) {
                    Effect nextEffect = effects.poll();
                    boolean add = true;
                    for (int i = currentEffects.size() - 1; i >= 0; i--) {
                        if (nextEffect.isConflictingWith(currentEffects.get(i))) {
                            currentEffects.remove(i);
                            add = false;
                        }
                    }
                    if (add) {
                        currentEffects.add(nextEffect);
                    }
                }
                for (Effect e : currentEffects) {
                    e.prepare();
                }
                for (Effect e : currentEffects) {
                    e.activate();
                    messages.addAll(e.getMessages());
                }
            }
            return messages;
        }

        @Override
        public void run() {
            grid = scenario.createTileGrid();

            createDeck();
            players.shuffle();

            Player[] p = getPlayers();
            for (int i = 0; i < p.length; i++) {
                p[i].setPosition(i);
            }

            int index = 0;
            for (Player player : getPlayers()) {
                Robot robot = new Robot(player);
                Tile tile = grid.getStartingPoint(index);
                index++;
                player.setGameState(Player.GameState.ACTIVE);
                player.setLives(rules.getNumberOfLives());
                player.setRobot(robot);
                robot.setArchive(tile);
                robot.setMaximumHealth(rules.getMaximumHealth(robot));
                robot.setCurrentHealth(rules.getStartingHealth(robot));
                robot.setDestroyed(false);
                robot.setDirection(null);
                robot.setPoweredDown(false);
                robot.setTile(tile);
                tile.setRobot(robot);
            }

            manager.broadcast(GAME_STARTED, name);
            manager.broadcast(GAME_PLAYERS, players);
            manager.broadcast(GAME_STATUS, scenario.getDetailedMessage());
            manager.broadcast(CARDS, deck.toMessage());

            // Inform players about the current robot status
            for (Player player : getActivePlayers()) {
                Robot robot = player.getRobot();
                manager.broadcast(ROBOT_STATUS, robot.toMessage());
            }

            // Let the players determine starting positions for their robots
            chooseStartingDirections();

            while (getActivePlayers().length > 0) {
                turn++;
                manager.broadcast(NEW_TURN, turn);

                // Deal with destroyed robots
                respawnDestroyedRobots();

                // Ask powered down robots whether they want to stay that way
                askForContinuedPowerDowns();

                // Deal with power down announcements
                processPowerDownAnnouncements();

                // Collect all free program cards and return them to the discard stack
                List<ProgramCard> programCards = new LinkedList<ProgramCard>();
                for (Player player : getPlayers()) {
                    Robot robot = player.getRobot();
                    if (!robot.isPoweredDown()) {
                        programCards.addAll(robot.extractProgramCards());
                    }
                }
                deck.discardCards(programCards);

                // Deal program cards
                dealProgramCards();

                // Wait for the programming to be finished
                manager.waitForAllOpenChoices(rules.getTimeout(Type.PROGRAMMING));
                preparePrograms();

                // Ask for power down announcements
                askForPowerDownAnnouncements();

                // Execute the programs
                manager.broadcast(EXECUTING_PROGRAMS);
                for (Phase phase : Phase.values()) {
                    manager.broadcast(NEW_PHASE, phase.ordinal() + 1);

                    // Update the tile state
                    updateTilePhase(phase);

                    // Get the commands for the current phase and order them by priority
                    List<Player> fireOrder = executeProgramCards(phase);

                    // Prepare a list to collect all messages
                    List<String> messages = new LinkedList<String>();

                    // Move factory elements
                    messages.addAll(processEffects(Timing.BOARD_ELEMENT_MOVE));

                    // Fire Robot Lasers
                    for (Player player : fireOrder) {
                        Robot robot = player.getRobot();
                        robot.fireWeapons();
                    }

                    // Laser fire
                    messages.addAll(processEffects(Timing.LASER_FIRE));

                    // Touch checkpoints
                    messages.addAll(processEffects(Timing.TOUCH_CHECKPOINT));

                    destroyedInCurrentRound.removeAll(arrivedInCurrentRound);
                    for (Player player : destroyedInCurrentRound) {
                        player.setGameState(Player.GameState.DESTROYED);
                        player.setFinalTurn(turn);
                        destroyedPlayers.add(0, player);
                        manager.broadcast(PLAYER_DESTROYED, player.getName());
                    }
                    destroyedInCurrentRound.clear();

                    for (Player player : arrivedInCurrentRound) {
                        Robot robot = player.getRobot();
                        Tile tile = robot.getTile();
                        if (tile != null) {
                            tile.setRobot(null);
                        }
                        player.setGameState(Player.GameState.COMPLETED);
                        player.setFinalTurn(turn);
                        arrivedPlayers.add(player);
                        manager.broadcast(PLAYER_ARRIVED, player.getName());
                    }
                    arrivedInCurrentRound.clear();

                    // Inform players about important effects
                    for (String message : messages) {
                        manager.broadcast(EFFECT_OCCURRED, message);
                    }

                    // Inform players about the current robot status
                    for (Player player : getActivePlayers()) {
                        Robot robot = player.getRobot();
                        manager.broadcast(ROBOT_STATUS, robot.toMessage());
                    }
                }

                // Process end of turn effects and broadcast messages
                List<String> messages = processEffects(Timing.END_OF_TURN);
                for (String message : messages) {
                    manager.broadcast(EFFECT_OCCURRED, message);
                }

                // Broadcast robot status messages
                for (Player player : getActivePlayers()) {
                    Robot robot = player.getRobot();
                    manager.broadcast(ROBOT_STATUS, robot.toMessage());
                }
            }

            List<Player> ranking = new LinkedList<Player>();
            ranking.addAll(arrivedPlayers);
            Collections.sort(destroyedPlayers, new Comparator<Player>() {

                @Override
                public int compare(Player o1, Player o2) {
                    int result = o2.getRobot().getProgress() - o1.getRobot().getProgress();
                    return result;
                }
            });
            ranking.addAll(destroyedPlayers);
            manager.broadcast(GAME_OVER, ranking);

            BufferedWriter writer = null;
            BufferedWriter writer2 = null;

            System.out.println("Replay");
            try {
                File replayFolder = new File(server.getReplayString());
                if (!replayFolder.exists()) {
                    replayFolder.mkdir();
                }
                String filename = String.format("%1$s%2$s%3$s.txt", server.getReplayString(), File.separator, name);
                int count = 1;
                File file = new File(filename);
                while (file.exists()) {
                    count++;
                    filename = String.format("%1$s%2$s%3$s (%4$s).txt", server.getReplayString(), File.separator, name, count);
                    file = new File(filename);
                }
                writer = new BufferedWriter(new FileWriter(file));
                LinkedList<String> log = new LinkedList(manager.getLog());
                System.out.println("Writing");
                for (String line : log) {
                    System.out.println(line);
                    writer.write(line);
                    writer.newLine();
                }
                System.out.println("Done");
                if (Server.printStatistics()) {
                    if (count == 1) {
                        filename = String.format("%1$s%2$sStatictics for %3$s.txt", server.getReplayString(), File.separator, name);
                    } else {
                        filename = String.format("%1$s%2$sStatictics for %3$s (%4$s).txt", server.getReplayString(), File.separator, name, count);
                    }
                    writer2 = new BufferedWriter(new FileWriter(new File(filename)));
                    int i = 1;
                    for (Player player : ranking) {
                        if (player.getRobot().getProgress() < grid.getNumberOfCheckpoints()) {
                            writer2.write(String.format("%1$s. %2$s (%6$s): %3$s von %5$s Checkpoints erreicht, Tod nach %4$s Zügen", i, player.getName(), player.getRobot().getProgress(), player.getFinalTurn(), grid.getNumberOfCheckpoints(), player.getClientType()));
                            writer2.newLine();
                        } else {
                            writer2.write(String.format("%1$s. %2$s (%4$s): nach %3$s Zügen angekommen", i, player.getName(), player.getFinalTurn(), player.getClientType()));
                            writer2.newLine();
                        }
                        i++;
                    }
                }
            } catch (IOException ex) {
                System.out.println(ex);
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (writer != null) {
                    try {
                        writer.flush();
                        writer.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (writer2 != null) {
                    try {
                        writer2.flush();
                        writer2.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            for (Player player : getPlayers()) {
                leave(player);
            }
        }
    }
}
