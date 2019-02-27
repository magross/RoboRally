/*
 * ChoiceManager.java
 *
 */
package roborally;

import roborally.Choice.Type;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import roborally.server.Player;
import roborally.server.ServerMessage;
import static roborally.server.ServerMessage.*;

/**
 *
 * @author Martin Groß
 */
public class ChoiceManager {

    private class ChoiceMap {

        private Map<Player, EnumMap<Choice.Type, Choice>> allChoices;

        public ChoiceMap() {
            this.allChoices = new HashMap<Player, EnumMap<Choice.Type, Choice>>();
        }

        public synchronized void addChoice(Player player, Choice choice) {
            if (!allChoices.containsKey(player)) {
                allChoices.put(player, new EnumMap<Choice.Type, Choice>(Choice.Type.class));
            }
            allChoices.get(player).put(choice.getType(), choice);
        }

        public synchronized Choice extractMadeChoice(Player player, Choice.Type type) {
            Choice result = null;
            if (allChoices.containsKey(player) && allChoices.get(player).containsKey(type) && allChoices.get(player).get(type).isChosen()) {
                result = allChoices.get(player).get(type);
                allChoices.get(player).remove(type);
            }
            return result;
        }

        public synchronized List<Choice> getMadeChoices(Player player) {
            if (allChoices.containsKey(player)) {
                LinkedList<Choice> result = new LinkedList<Choice>();
                for (Choice choice : allChoices.get(player).values()) {
                    if (choice.isChosen()) {
                        result.add(choice);
                    }
                }
                return result;
            } else {
                return new LinkedList<Choice>();
            }
        }

        public synchronized Iterable<Choice> getPendingChoices(Player player) {
            if (allChoices.containsKey(player)) {
                LinkedList<Choice> result = new LinkedList<Choice>();
                for (Choice choice : allChoices.get(player).values()) {
                    if (!choice.isChosen()) {
                        result.add(choice);
                    }
                }
                return result;
            } else {
                return new LinkedList<Choice>();
            }
        }

        public synchronized boolean isPending(Player player, Type type) {
            return allChoices.containsKey(player) && allChoices.get(player).containsKey(type) && !allChoices.get(player).get(type).isChosen();
        }

        public synchronized Choice getChoice(Player player, Type type) {
            return allChoices.get(player).get(type);
        }
    }
    private ChoiceMap choices;
    private CountDownLatch countdown;
    private List<String> log;
    private final AtomicList<Player> players;

    public ChoiceManager(AtomicList<Player> players) {
        this.countdown = new CountDownLatch(0);
        this.log = new LinkedList<String>();
        this.players = players;
        this.choices = new ChoiceMap();
    }

    public void decideAllOpenChoices(Player player) {
        for (Choice choice : choices.getPendingChoices(player)) {
            choice.choose();
            countdown.countDown();
            player.sendMessage(CHOSEN, player, choice.getType(), choice.getChosenObjects());
        }
    }

    public void broadcast(ServerMessage message, Object... parameters) {
        //System.out.println("Server: " + message.getMessage(parameters));
        log.add(message.getMessage(parameters));
        for (Player player : players.elements()) {
            player.sendMessage(message, parameters);
        }
    }

    public List<String> getLog() {
        return log;
    }

    public Choice extractMadeChoice(Player player, Choice.Type type) {
        return choices.extractMadeChoice(player, type);
    }

    public boolean process(Player player, Choice.Type type, String indexString) {
        boolean result = false;
        if (choices.isPending(player, type)) {
            Choice choice = choices.getChoice(player, type);
            result = choice.choose(indexString);
            countdown.countDown();
            player.sendMessage(CHOSEN, player, choice.getType(), choice.getChosenObjects());
        } else {
            player.sendMessage(NOT_WAITING_FOR_THIS_CHOICE, type);
        }
        return result;
    }

    public <T> List<T> requestChoice(Player player, Choice<T> choice) {
        return requestChoice(player, choice, 0, null);
    }

    public <T> List<T> requestChoice(Player player, Choice<T> choice, long timeout, TimeUnit unit) {
        List<T> result = null;
        if (choice.isChosen()) {
            registerChoiceRequest(player, choice);
            notifyAboutChoiceRequest(player, choice);
            player.sendMessage(CHOSEN, player, choice.getType(), choice.getChosenObjects());
            notifyAboutChoiceReply(player, choice);
            result = choice.getChosenObjects();
        } else {
            registerChoiceRequest(player, choice);
            notifyAboutChoiceRequest(player, choice);
            if (timeout > 0) {
                if (!choice.await(timeout, unit)) {
                    notifyAboutTimeout(player, choice);
                }
                player.sendMessage(CHOSEN, player, choice.getType(), choice.getChosenObjects());
                notifyAboutChoiceReply(player, choice);
                result = choice.getChosenObjects();
            }
        }
        return result;
    }

    public void waitForAllOpenChoices(long timeout) {
        int sum = 0;
        for (Player player : players.elements()) {
            for (Choice choice : choices.getPendingChoices(player)) {
                sum += (choice.isChosen()) ? 0 : 1;
            }
        }
        countdown = new CountDownLatch(sum);
        try {
            countdown.await(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            System.out.println("Interrupted");
        }
        for (Player player : players.elements()) {
            for (Choice choice : choices.getPendingChoices(player)) {
                if (!choice.isChosen()) {
                    choice.choose();
                    notifyAboutTimeout(player, choice);
                    player.sendMessage(CHOSEN, player, choice.getType(), choice.getChosenObjects());
                }
            }
        }
        for (Player player : players.elements()) {
            for (Choice choice : choices.getMadeChoices(player)) {
                notifyAboutChoiceReply(player, choice);
            }
        }
    }

    protected void registerChoiceRequest(Player player, Choice<?> choice) {
        choices.addChoice(player, choice);
    }

    protected void notifyAboutTimeout(Player player, Choice<?> choice) {
        for (Player p : players.elements()) {
            System.out.println("TIMEOUT " + player.getName() + " " + choice.getType());
            p.sendMessage(ServerMessage.TIMEOUT, player, choice.getType());
        }
    }

    protected void notifyAboutChoiceRequest(Player selector, Choice<?> choice) {
        for (Player player : players.elements()) {
            if (player == selector) {
                selector.sendMessage(CHOOSE, choice.getType(), choice.getNumberOfPicks(), choice.getChoosableObjects());
            } else {
                switch (choice.getVisibilty()) {
                    case PUBLIC:
                        player.sendMessage(CHOOSE, selector, choice.getType(), choice.getNumberOfPicks(), choice.getChoosableObjects());
                        break;
                    case RESULT_ONLY:
                        player.sendMessage(CHOOSE, selector, choice.getType());
                        break;
                    default:
                        new AssertionError(choice.getVisibilty() + " is not handled yet.");
                }
            }
        }
    }

    protected void notifyAboutChoiceReply(Player selector, Choice<?> choice) {
        log.add(CHOSEN.getMessage(selector, choice.getType(), choice.getChosenObjects()));
        for (Player player : players.elements()) {
            if (player == selector) {
            } else {
                switch (choice.getVisibilty()) {
                    case PUBLIC:
                    case RESULT_ONLY:
                        player.sendMessage(CHOSEN, selector, choice.getType(), choice.getChosenObjects());
                        break;
                    default:
                        new AssertionError(choice.getVisibilty() + " is not handled yet.");
                }
            }
        }
    }
}
