/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roborally.ai;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import roborally.Choice;
import roborally.server.ServerMessage;
import static roborally.server.ServerMessage.*;

/**
 *
 * @author gross
 */
public class Stub {

    public static String[] getAIProfiles() {
        return new String[]{"Test"};
    }

    public static String createAIClient(String profile, String address, int port, String serverPassword, String clientName, String gameName) {
        try {
            Socket socket = new Socket(address, port);
            AIClient client = new AIClient(socket, profile, serverPassword, clientName, gameName);
            client.start();
            return "";
        } catch (UnknownHostException ex) {
            Logger.getLogger(Stub.class.getName()).log(Level.SEVERE, null, ex);
            return ex.toString();
        } catch (IOException ex) {
            Logger.getLogger(Stub.class.getName()).log(Level.SEVERE, null, ex);
            return ex.toString();
        }
    }

    public static class AIClient extends Thread {

        private String aiName;
        private String gameName;
        private String gamePassword;
        private String profile;
        private String serverPassword;
        private Socket socket;

        public AIClient(Socket socket, String profile, String serverPassword, String aiName, String gameName) {
            this.aiName = aiName;
            this.gameName = gameName;
            this.serverPassword = serverPassword;
            this.profile = profile;
            this.socket = socket;
        }

        @Override
        public void run() {
            BufferedReader input = null;
            boolean running = true;
            PrintStream output = null;
            try {
                output = new PrintStream(socket.getOutputStream());
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = input.readLine();
                line = null;
                do {
                    if (line != null) {
                        System.out.println("Line: " + line);
                        System.out.println(Arrays.toString(line.split("\\|")));
                        ServerMessage message = ServerMessage.valueOf(line.split("\\|")[0].trim());
                        System.out.println(message);
                        switch (message) {
                            case AWAITING_REGISTRATION:
                                output.println("INTRODUCE | " + profile + " | " + serverPassword);
                                break;
                            case CHOOSE:
                                try {
                                    Choice.Type type = Choice.Type.valueOf(line.split("\\|")[1].trim());
                                    String result = "";
                                    switch (type) {
                                        case ANNOUNCE_POWER_DOWN:
                                            result = decidePowerDownAnnouncement();
                                            break;
                                        case PROGRAMMING:
                                            result = decideProgramming();
                                            break;
                                        case REMAIN_POWERED_DOWN:
                                            result = decideRemainPoweredDown();
                                            break;
                                        case SPAWN_DIRECTION:
                                            result = decideSpawnDirection();
                                            break;
                                        case SPAWN_TILE:
                                            result = decideSpawnTile();
                                            break;
                                    }
                                    output.println("GAME_CHOICE | " + type.name() + " | " + result);
                                } catch (IllegalArgumentException ex) {
                                }
                                break;
                            case GAME_NOT_FOUND:
                                output.println("CREATE_GAME | Risky Exchange | " + gameName);
                                output.println("START_GAME");
                                break;
                            case ROBOT_STATUS:
                                break;
                            case GAME_STATUS:
                                break;
                            case CONNECTION_CLOSED:
                            case GAME_OVER:
                            case PLAYER_ARRIVED:
                            case PLAYER_DESTROYED:
                                running = false;
                                break;
                            case INTRODUCTION_SUCCESSFUL:
                                output.println("REGISTER | " + aiName);
                                break;
                            case REGISTRATION_SUCCESSFUL:
                                output.println("JOIN_GAME | " + gameName);
                                break;
                        }
                    }
                    System.out.print("Reading... ");
                    line = input.readLine();
                    System.out.println(line);
                } while (running && line != null);
            } catch (SocketException e) {
            } catch (Exception ex) {
                Logger.getLogger(Stub.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                running = false;
                output.close();
                try {
                    input.close();
                } catch (Exception e) {
                    Logger.getLogger(Stub.class.getName()).log(Level.SEVERE, null, e);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    Logger.getLogger(Stub.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }

        private String decidePowerDownAnnouncement() {
            return "1";
        }

        private String decideRemainPoweredDown() {
            return "1";
        }

        private String decideSpawnTile() {
            return "0";
        }

        private String decideSpawnDirection() {
            return "0";
        }

        private String decideProgramming() {

            return "0,1,2,3,4";
        }
    }
}
