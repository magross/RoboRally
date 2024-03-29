/*
 * Player.java
 *
 */
package roborally.server;

import roborally.robot.ProgramCard;
import roborally.robot.Robot;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Groß
 */
public class Player {

    public enum GameState {

        ACTIVE,
        COMPLETED,
        DESTROYED;
    }
    private GameState gameState;
    private int finalTurn;
    private BufferedReader input;
    private int lives;
    private boolean running;
    private Server server;
    private Socket socket;
    private String name;
    private Robot robot;
    private Game game;
    private EnumSet<PlayerState> state;
    private PrintWriter output;
    private String clientType;
    private Thread listener;
    private int position;
    private List<ProgramCard> cards;

    public Player(Server server, Socket socket) throws IOException {
        super();
        this.server = server;
        this.socket = socket;
        output = new PrintWriter(socket.getOutputStream(), true);
        state = EnumSet.of(PlayerState.WAITING);
        listener = new ListeningThread();
        listener.start();
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public int getFinalTurn() {
        return finalTurn;
    }

    public void setFinalTurn(int finalTurn) {
        this.finalTurn = finalTurn;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Robot getRobot() {
        return robot;
    }

    public void setRobot(Robot robot) {
        this.robot = robot;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        if (this.running && !running) {
            this.running = running;
            try {
                socket.close();
            } catch (Exception e) {
            }
        }
        this.running = running;
    }

    public Server getServer() {
        return server;
    }

    public void setState(PlayerState clientState) {
        state = EnumSet.of(clientState);
    }

    public void addState(PlayerState s) {
        state.add(s);
    }

    public void removeState(PlayerState s) {
        state.remove(s);
    }

    public EnumSet<PlayerState> getState() {
        return state;
    }

    public boolean isState(PlayerState s) {
        return state.contains(s);
    }

    public void sendMessage(ServerMessage message, Object... parameters) {
        if (running) {
            //System.out.println("Player: " + message.getMessage(parameters));
            output.println(message.getMessage(parameters));
        }
    }

    public List<ProgramCard> getProgramCards() {
        return cards;
    }

    public void setProgramCards(List<ProgramCard> cards) {
        this.cards = cards;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return getName();
    }

    public class ListeningThread extends Thread {

        private static final long THRESHOLD_TIME = 100000000, THRESHOLD_MESSAGES = 10;

        @Override
        public void run() {
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                running = true;
                sendMessage(ServerMessage.WELCOME, server.getMessage());
                sendMessage(ServerMessage.AWAITING_REGISTRATION);
                String line = null;
                long last = System.nanoTime(), now;
                int count = 0;
                do {
                    if (line != null) {
                        //System.out.println("Server: " + Player.this.getName() + ": " + line);
                        ClientMessage.handle(Player.this, line);
                    }
                    line = input.readLine();
                    now = System.nanoTime();
                    if (now < last + THRESHOLD_TIME) {
                        count++;
                    } else {
                        count = 0;
                    }
                    if (count > THRESHOLD_MESSAGES) {
                        count = 0;
                        sleep(1000);
                    }
                    last = now;
                } while (running && line != null);
            } catch (SocketException e) {
            } catch (Exception ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                server.unregisterPlayer(Player.this);
                running = false;
                output.close();
                try {
                    input.close();
                } catch (Exception e) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, e);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }
}
