/*
 * Server.java
 *
 */
package roborally.server;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import roborally.AtomicSortedMap;
import roborally.ai.AI;
import roborally.ai.AIClientCreatorNotFoundException;

/**
 *
 * @author Martin Groß
 */
public class Server {

    public static String getVersion() {
        return "1.2.1 (Turnier RC 2)";
    }
    private String adminPassword;
    private String gameMasterPassword;
    private boolean listening;
    private ListeningThread listeningThread;
    private String message;
    private String name;
    private String password;
    private int port;
    private AtomicSortedMap<Game> games;
    private AtomicSortedMap<AI> aiProfiles;
    private AtomicSortedMap<Player> players;
    private AtomicSortedMap<Replay> replays;
    private String aiString;
    private String replayString;
    private AtomicSortedMap<Scenario> scenarios;
    private String scenarioString;
    private ServerSocket serverSocket;
    private HashMap<String, String> namePasswords;

    public Server() {
        adminPassword = "";
        gameMasterPassword = "";
        name = "CoMa Prime";
        password = "";
        aiProfiles = new AtomicSortedMap<AI>(AI.class);
        games = new AtomicSortedMap<Game>(Game.class);
        players = new AtomicSortedMap<Player>(Player.class);
        replays = new AtomicSortedMap<Replay>(Replay.class);
        scenarios = new AtomicSortedMap<Scenario>(Scenario.class);
        message = "Willkommen!";

        namePasswords = new HashMap<String, String>();
    }

    public Server(String toString) {
        this();
        this.message = toString;
    }

    public String getAIString() {
        return aiString;
    }

    public void setAIString(String aiString) {
        this.aiString = aiString;
    }

    public String getReplayString() {
        return replayString;
    }

    public void setReplayString(String replayString) {
        this.replayString = replayString;
    }

    public String getScenarioString() {
        return scenarioString;
    }

    public void setScenarioString(String scenarioString) {
        this.scenarioString = scenarioString;
    }

    public void loadAIs() {
        loadAIs(new File(aiString));
    }

    public void loadReplays() {
        loadReplays(new File(replayString));
    }

    public void loadScenarios() {
        loadScenarios(new File(scenarioString));
    }

    private void loadAIs(File folder) {
        if (folder == null) {
            return;
        }
        System.out.print(" Durchsuche " + getCanonicalPath(folder) + " nach AIs...");
        if (!folder.exists()) {
            System.out.println(" Ordner nicht gefunden.");
            return;
        } else {
            System.out.println("");
        }
        for (File file : folder.listFiles()) {
            if (file.isHidden()) {
                continue;
            }
            if (file.isDirectory()) {
                //loadReplays(folder);
            } else if (file.isFile() && file.getPath().endsWith(".jar")) {
                System.out.println("  Lade " + getCanonicalPath(file) + "...");
                try {
                    AI ai = new AI(getCanonicalPath(file));
                    for (String profile : ai.getAIProfiles()) {
                        if (!aiProfiles.contains(profile)) {
                            aiProfiles.set(profile, ai);
                            System.out.println("   " + profile + " geladen.");
                        } else {
                            System.out.println("   Eine AI mit dem Namen " + profile + " existiert bereits.");
                        }
                    }
                    System.out.println("  Fertig.");
                } catch (AIClientCreatorNotFoundException ex) {
                    System.out.println("  Fehlgeschlagen - es wurde keine Klasse mit statischen createAIClient(String, String, int, String, String, String, String) & getAiProfiles() Methoden gefunden.");
                } catch (IOException ex) {
                    System.out.println("  Fehlgeschlagen - es trat eine " + ex.getClass().getSimpleName() + " auf.");
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void loadReplays(File folder) {
        if (folder == null) {
            return;
        }
        System.out.print(" Durchsuche " + getCanonicalPath(folder) + " nach Replays...");
        if (!folder.exists()) {
            System.out.println(" Ordner nicht gefunden.");
            return;
        } else {
            System.out.println("");
        }
        for (File file : folder.listFiles()) {
            if (file.isHidden()) {
                continue;
            }
            if (file.isDirectory()) {
                //loadReplays(folder);
            } else if (file.isFile()) {
                System.out.print("  Lade " + getCanonicalPath(file) + "...");
                //try {
                    //BufferedReader reader = new BufferedReader(new FileReader(file));
                    //List<String> lines = new LinkedList<String>();
                    //String line;
                    //while ((line = reader.readLine()) != null) {
                        //lines.add(line);
                    //}
                    //Replay replay = new Replay(lines);
                    Replay replay = new Replay(file);
                    replays.set(file.getName(), replay);
                    System.out.println(" fertig.");
                    //reader.close();
                //} catch (IOException ex) {
                //    System.out.println(" fehlgeschlagen - es trat eine " + ex.getClass().getSimpleName() + " auf.");
                //    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                //}
            }
        }
    }

    private String getCanonicalPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException ex) {
            return file.getAbsolutePath();
        }
    }

    private void loadScenarios(File folder) {
        if (folder == null) {
            return;
        }
        System.out.print(" Durchsuche " + getCanonicalPath(folder) + " nach Szenarien...");
        if (!folder.exists()) {
            System.out.println(" Ordner nicht gefunden.");
            return;
        } else {
            System.out.println("");
        }
        for (File file : folder.listFiles()) {
            if (file.isHidden() || file.equals(folder)) {
                continue;
            }
            if (file.isDirectory()) {
                //loadScenarios(folder);
            } else if (file.isFile() && file.getAbsolutePath().endsWith(".txt")) {
                System.out.print("  Lade " + getCanonicalPath(file) + "...");
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    List<String> lines = new LinkedList<String>();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.matches("\\s*")) {
                            lines.add(line);
                        }
                    }
                    Scenario scenario = new Scenario(lines.toArray(new String[0]));
                    if (!scenarios.contains(scenario.getName())) {
                        scenarios.set(scenario.getName(), scenario);
                        System.out.println(" fertig.");
                    } else {
                        System.out.println(" fehlgeschlagen - ein Szenario mit dem Namen " + scenario.getName() + " existiert bereits.");
                    }
                    reader.close();
                } catch (Exception ex) {
                    System.out.println(" fehlgeschlagen - es trat eine " + ex.getClass().getSimpleName() + " auf.");
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
      if (adminPassword == null) {
            adminPassword = "";
        }
        this.adminPassword = adminPassword;
    }

    public String getGameMasterPassword() {
        return gameMasterPassword;
    }

    public void setGameMasterPassword(String gameMasterPassword) {
        this.gameMasterPassword = gameMasterPassword;
    }

    public String getNamePassword(String name) {
        if (namePasswords.containsKey(name)) {
            return namePasswords.get(name);
        } else {
            return "";
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null) {
            password = "";
        }
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isListening() {
        return listening;
    }

    public boolean startListening(int port, int numberOfConnections) {
        if (listening) {
            return false;
        } else {
            try {
                listening = true;
                serverSocket = new ServerSocket(port, numberOfConnections);
                listeningThread = new ListeningThread();
                listeningThread.start();
                return true;
            } catch (IOException ex) {
                //Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
    }

    public void stopListening() {
        listening = false;
        if (listeningThread != null) {
            listeningThread.interrupt();
        }
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    synchronized void broadcastMessage(ServerMessage message, Object... parameters) {
        for (Player player : players.listValues()) {
            player.sendMessage(message, parameters);
        }
    }

    public String[] getGames() {
        return games.listKeys();
    }

    public String[] getPlayers() {
        return players.listKeys();
    }

    public String[] getSaves() {
        return new String[0];
    }

    public boolean registerPlayer(String nickname, Player player) {
        return players.set(nickname, player);
    }

    public boolean unregisterNickname(String nickname) {
        return players.remove(nickname);
    }

    synchronized void terminate() {
        listening = false;
        try {
            serverSocket.close();
            if (players != null) {
                for (Player player : players.listValues()) {
                    player.setRunning(false);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String[] getAIs() {
        return aiProfiles.listKeys();
    }

    public String[] getReplays() {
        return replays.listKeys();
    }

    public String[] getScenarios() {
        return scenarios.listKeys();
    }

    public Player getRegisteredPlayer(String string) {
        return players.get(string);
    }

    public void unregisterPlayer(Player player) {
        if (player.getName() != null && players.contains(player.getName())) {
            if (player.getGame() != null) {
                player.getGame().leave(player);
            }
            if (player.getName() != null) {
                broadcastMessage(ServerMessage.PLAYER_DISCONNECTED, player.getName());
                players.remove(player.getName());
            }
        }
    }

    void setNamePassword(String name, String string) {
        namePasswords.put(name, string);
    }

    public AI getAI(String string) {
        return aiProfiles.get(string);
    }

    public Game getGame(String string) {
        return games.get(string);
    }

    public Replay getReplay(String string) {
        return replays.get(string);
    }

    public Scenario getScenario(String string) {
        return scenarios.get(string);
    }

    public Game createGame(Scenario scenario, String name) {
        return createGame(scenario, name, "", "");
    }

    public Game createGame(Scenario scenario, String name, String password, String seed) {
        Game game = new Game(this, scenario, name, password);
        if (!seed.isEmpty()) {
            game.setSeed(Long.parseLong(seed));
        }
        if (games.set(name, game)) {
            return game;
        } else {
            return null;
        }
    }

    void stopGame(String name) {
        games.remove(name);
    }

    public class ListeningThread extends Thread {

        @Override
        public void run() {
            while (listening) {
                try {
                    Socket socket = serverSocket.accept();
                    socket.setTcpNoDelay(true);
                    new Player(Server.this, socket);
                } catch (SocketException e) {
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public String getMessage() {
        return message;
    }
    private static boolean PRINT_STATISTICS;

    public static boolean printStatistics() {
        return PRINT_STATISTICS;
    }

    public static void main(String[] args) throws IOException, JSAPException {
        JSAP jsap = new JSAP();

        FlaggedOption opt1 = new FlaggedOption("connections").setStringParser(JSAP.INTEGER_PARSER).setDefault("50").setRequired(false).setShortFlag('c').setLongFlag("connections");
        opt1.setHelp("Die maximale Anzahl gleichzeitig akzeptierter Verbindungen.");

        FlaggedOption opt2 = new FlaggedOption("password").setStringParser(JSAP.STRING_PARSER).setDefault("").setRequired(false).setShortFlag('x').setLongFlag("password");
        opt2.setHelp("Setzt ein Passwort das von Clienten bei der Registrierung gefordert wird.");

        FlaggedOption opt3 = new FlaggedOption("port").setStringParser(JSAP.INTEGER_PARSER).setDefault("4444").setRequired(false).setShortFlag('p').setLongFlag("port");
        opt3.setHelp("Gibt den Port an, auf der Server auf Clienten wartet.");

        FlaggedOption opt4 = new FlaggedOption("replay").setStringParser(JSAP.STRING_PARSER).setDefault("replay").setRequired(false).setShortFlag('r').setLongFlag("replay");
        opt4.setHelp("Gibt den Ordner an, in dem der Server nach Replays sucht / Replays speichert.");

        FlaggedOption opt5 = new FlaggedOption("scenario").setStringParser(JSAP.STRING_PARSER).setDefault("scenario").setRequired(false).setShortFlag('s').setLongFlag("scenario");
        opt5.setHelp("Gibt den Ordner an, in dem der Server nach Szenarien sucht.");

        UnflaggedOption opt6 = new UnflaggedOption("message").setStringParser(JSAP.STRING_PARSER).setDefault("Willkommen!").setRequired(false).setGreedy(true);
        opt6.setHelp("Setzt die Willkommens-Nachricht des Servers. Die einzelnen Argumente werden mit Leerzeichen als Trenner zusammengesetzt.");

        FlaggedOption opt7 = new FlaggedOption("ai").setStringParser(JSAP.STRING_PARSER).setDefault("ai").setRequired(false).setShortFlag('a').setLongFlag("ai");
        opt7.setHelp("Gibt den Ordner an, in dem der Server nach AIs sucht.");

        FlaggedOption opt8 = new FlaggedOption("master-password").setStringParser(JSAP.STRING_PARSER).setDefault("").setRequired(false).setShortFlag('m').setLongFlag("admin");
        opt8.setHelp("Gibt das Master-Passwort an, mit dem man als registrierter Spieler Admin-Rechte erhalten kann. Ohne Passwort kann kein Spieler Admin werden.");

        Switch sw1 = new Switch("statistics").setLongFlag("statistics");
        sw1.setHelp("Gibt an, ob der Server Statistiken zu Replays speichert (z.B. welcher Roboter wann angekommen ist).");

        Switch sw2 = new Switch("help").setShortFlag('h').setLongFlag("help");
        sw2.setHelp("Zeigt diese Hilfe an.");

        jsap.registerParameter(opt1);
        jsap.registerParameter(opt2);
        jsap.registerParameter(opt3);
        jsap.registerParameter(opt4);
        jsap.registerParameter(opt5);
        jsap.registerParameter(opt7);
        jsap.registerParameter(opt8);
        jsap.registerParameter(sw1);
        jsap.registerParameter(sw2);
        jsap.registerParameter(opt6);
        
        JSAPResult config = jsap.parse(args);

        if (!config.success()) {

            System.err.println();

            for (java.util.Iterator errs = config.getErrorMessageIterator();
                    errs.hasNext();) {
                System.err.println("Error: " + errs.next());
            }

            System.err.println();
            System.err.println("Usage: java -jar Server.jar");
            System.err.println("                " + jsap.getUsage());
            System.err.println();
            System.err.println(jsap.getHelp());
        } else if (config.getBoolean("help")) {
            System.out.println("Usage: java -jar Server.jar");
            System.out.println("                " + jsap.getUsage());
            System.out.println();
            System.out.println(jsap.getHelp());
        } else {
            PRINT_STATISTICS = config.getBoolean("statistics");

            int connections = config.getInt("connections");
            String message = config.getString("message");
            String password = config.getString("password");
            int port = config.getInt("port");

            System.out.println("Starte Server (Version " + getVersion() + ")...");
            Server server = new Server(message);
            server.setAdminPassword(config.getString("master-password"));
            server.setPassword(password);
            server.setPort(port);
            System.out.println("Lade AIs...");
            server.setAIString(config.getString("ai"));
            server.loadAIs();
            System.out.println("Lade Replays...");
            server.setReplayString(config.getString("replay"));
            server.loadReplays();
            System.out.println("Lade Szenarien...");
            server.setScenarioString(config.getString("scenario"));
            server.loadScenarios();
            if (server.startListening(port, connections)) {
                System.out.println("Server laeuft auf Port " + port + "; max. " + connections + " Verbindungen gleichzeitig. Druecke <ENTER> um den Server zu beenden.");



                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
                stdIn.readLine();
                server.terminate();
                System.out.println("Server beendet.");
            } else {
                System.out.println("Server konnte nicht auf Port " + port + " gestartet werden. Laeuft bereits ein Server auf diesem Port?");
            }
        }
    }
}

