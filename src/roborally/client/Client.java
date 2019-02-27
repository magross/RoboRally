/*
 * Client.java
 *
 */
package roborally.client;

/**
 *
 * @author Martin Groß
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    public static class InputThread extends Thread {

        boolean running = true;
        PrintWriter o;

        public InputThread(PrintWriter o) {
            super();
            this.o = o;
        }

        @Override
        public void run() {
            o.println("INTRODUCE | Test");
            o.println("REGISTER | Martin");
            o.println("CREATE_GAME | Cannery Row | Test");
            o.println("SERVER_VERSION");
            o.println("SUMMON_AI | Jan | A");
            o.println("SUMMON_AI | Jan | B");
            o.println("SUMMON_AI | Jan | C");
            o.println("SUMMON_AI | Jan | D");
            o.println("SUMMON_AI | Jan | E");
            o.println("SUMMON_AI | Jan | F");
            o.println("SUMMON_AI | Jan | G");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            o.println("START_GAME");            
            o.println("LEAVE_GAME");
            o.println("CLOSE_CONNECTION");
            if (1 == 1) return;

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            while (running) {
                String fromUser = null;
                try {
                    fromUser = stdIn.readLine();
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (fromUser != null && running) {
                    System.out.println("Client: " + fromUser);
                    o.println(fromUser);
                }
            }
            try {
                stdIn.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private void sendMessage(String string) {
            o.println(string);
            
        }
    }

    public static void main(String[] args) throws IOException {

        Socket kkSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            //kkSocket = new Socket("goldeneye.math.tu-berlin.de", 55555);
            kkSocket = new Socket("127.0.0.1", 4444);
            out = new PrintWriter(kkSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: taranis.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: taranis.");
            System.exit(1);
        }

        
        String fromServer;
        System.out.println("Server: " + in.readLine());

        InputThread t = new InputThread(out);
        t.start();



        while ((fromServer = in.readLine()) != null) {
            System.out.println("Server: " + fromServer);
            if (fromServer.startsWith("CHOOSE | SPAWN_DIRECTION")) {
                //t.sendMessage("GAME_CHOICE | SPAWN_DIRECTION | 0");
            }
            if (fromServer.startsWith("CHOOSE | ANNOUNCE_POWER_DOWN")) {
                //t.sendMessage("GAME_CHOICE | ANNOUNCE_POWER_DOWN | 1");
            }
            if (fromServer.startsWith("CHOOSE | REMAIN_POWERED_DOWN")) {
                //t.sendMessage("GAME_CHOICE | REMAIN_POWER_DOWN | 1");
            }
            if (fromServer.startsWith("CHOOSE | PROGRAMMING")) {
                //t.sendMessage("GAME_CHOICE | PROGRAMMING | 1");
            }
            if (fromServer.startsWith("CONNECTION_CLOSED")) {
                t.running = false;
                t.interrupt();
                break;
            }
        }

        out.close();
        in.close();

        kkSocket.close();
    }
}

