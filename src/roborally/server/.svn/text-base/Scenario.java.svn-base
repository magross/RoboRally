/*
 * Scenario.java
 *
 */
package roborally.server;

import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import roborally.tiles.TileGrid;

/**
 *
 * @author Martin Groß
 */
public class Scenario {

    private String author;
    private String[] course;
    private String description;
    private Difficulty difficulty;    
    private TileGrid grid;
    private Length length;
    private String name;
    private int maxPlayers;
    private int minPlayers;

    public Scenario() {
    }

    public Scenario(String[] lines) throws ParseException {
        Token current = null;
        StringBuilder value = new StringBuilder();
        for (String line : lines) {
            if (current != null && !line.contains(":")) {
                value.append("\n" + line);
            } else {
                for (Token token : Token.values()) {
                    if (line.trim().startsWith(token.getToken())) {
                        process(current, value);
                        current = token;
                        line = line.substring(line.indexOf(":") + 1).trim();
                        value = new StringBuilder();
                        break;
                    }
                }
                if (value.length() > 0) {
                    value.append("\n");
                }
                value.append(line);
            }
        }
        process(current, value);
    }

    private void process(Token token, StringBuilder value) throws ParseException {
        if (token == null) {
            return;
        }
        switch (token) {
            case AUTHOR:
                author = value.toString();
                break;
            case COURSE:
                course = value.toString().split("\n");
                grid = new TileGrid(course);
                break;
            case DESCRIPTION:
                description = value.toString();
                break;
            case DIFFICULTY:
                difficulty = Difficulty.valueOf(value.toString().toUpperCase());
                break;
            case LENGTH:
                length = Length.valueOf(value.toString().toUpperCase());
                break;
            case MAX_PLAYERS:
                maxPlayers = Integer.parseInt(value.toString());
                break;
            case MIN_PLAYERS:
                minPlayers = Integer.parseInt(value.toString());
                break;
            case NAME:
                name = value.toString();
                break;
        }
    }

    public TileGrid createTileGrid() {
        try {
            return new TileGrid(course);
        } catch (ParseException ex) {
            Logger.getLogger(Scenario.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public int getHeight() {
        return grid.getHeight();
    }

    public Length getLength() {
        return length;
    }

    public void setLength(Length length) {
        this.length = length;
    }

    public int getMaximumNumberOfPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWidth() {
        return grid.getWidth();
    }

    public String getDetailedMessage() {
        StringBuilder result = new StringBuilder();
        result.append(name + " | " + grid.getWidth() + " | " + grid.getHeight() + " | " + difficulty + " | " + length + " | " + minPlayers + " | " + maxPlayers + " | " + author);
        result.append(" | " + description + " | ");
        result.append(grid.getDetailedMessage());
        return result.toString();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(name);
        return result.toString();
    }

    public enum Difficulty {

        EASY, MEDIUM, EXPERT;
    }

    public enum Length {

        SHORT, MEDIUM, LONG;
    }

    private enum Token {

        AUTHOR("Author:"),
        COURSE("Course:"),
        DESCRIPTION("Description:"),
        DIFFICULTY("Difficulty:"),
        HEIGHT("Height:"),
        LENGTH("Length:"),
        MAX_PLAYERS("Max. Players:"),
        MIN_PLAYERS("Min. Players:"),
        NAME("Name:"),
        WIDTH("Width:");
        private final String token;

        private Token(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }
    }
}
