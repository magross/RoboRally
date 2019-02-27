/*
 * Replay.java
 *
 */
package roborally.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Groß
 */
public class Replay extends LinkedList<String> {

    private File file;

    public Replay(File file) {
        this.file = file;
    }

    public Replay(List<String> lines) {
        super(lines);
    }

    public boolean isLoaded() {
        return !isEmpty();
    }

    public void load() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            List<String> lines = new LinkedList<String>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            addAll(lines);
            reader.close();
        } catch (IOException ex) {
            System.out.println("On-Demand-Laden fehlgeschlagen - es trat eine " + ex.getClass().getSimpleName() + " auf.");
            Logger.getLogger(Replay.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getDetailedMessage() {
        if (!isLoaded()) {
            load();
        }
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (String line : this) {
            if (first) {
                first = false;
            } else {
                result.append(" || ");
            }
            result.append(line);
        }
        return result.toString();
    }
}
