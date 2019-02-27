/*
 * Choice.java
 *
 */
package roborally;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Martin Groß
 */
public class Choice<T> {

    private static Random random = new Random();

    public static void setRandomNumberGenerator(Random random) {
        Choice.random = random;
    }

    private static int getRandomNumber(int number) {
        return random.nextInt(number);
    }

    public enum Type {
        ANNOUNCE_POWER_DOWN,
        PROGRAMMING(Visibility.RESULT_ONLY),
        REMAIN_POWERED_DOWN,
        SPAWN_TILE,
        SPAWN_DIRECTION;
        private final Visibility hidden;

        private Type() {
            this(Visibility.PUBLIC);
        }

        private Type(Visibility hidden) {
            this.hidden = hidden;
        }

        public Visibility getVisiblity() {
            return hidden;
        }
    }

    public enum Visibility {
        PRIVATE,
        RESULT_ONLY,
        PUBLIC;
    }

    public enum Status {
        DUPLICATE_PICKS,
        PICK_INDEX_OUT_OF_RANGE,
        PICK_NOT_PARSEABLE,
        TOO_FEW_PICKS,
        TOO_MANY_PICKS;
    }
    
    private List<T> choosableObjects;
    private boolean chosen;
    private List<Integer> chosenIndices;
    private List<T> chosenObjects;
    private CountDownLatch countdown;
    private int numberOfPicks;
    private Type type;

    public Choice(Type type, T... choosableObjects) {
        this(type, 1, Arrays.asList(choosableObjects));
    }

    public Choice(Type type, int numberOfPicks, T... choosableObjects) {
        this(type, numberOfPicks, Arrays.asList(choosableObjects));
    }

    public Choice(Type type, List<T> choosableObjects) {
        this(type, 1, choosableObjects);
    }

    public Choice(Type type, int numberOfPicks, List<T> choosableObjects) {
        this.choosableObjects = new LinkedList<T>(choosableObjects);
        this.chosen = false;
        this.chosenIndices = new LinkedList<Integer>();
        this.chosenObjects = new LinkedList<T>();
        this.countdown = new CountDownLatch(0);
        this.numberOfPicks = numberOfPicks;
        this.type = type;
        if (numberOfPicks == 1 && choosableObjects.size() == 1) {
            chosenIndices.add(0);
            chosenObjects.add(choosableObjects.get(0));
            this.chosen = true;
        } else if (numberOfPicks == 0) {
            chosen = true;
        }
    }

    public boolean await(long timeout, TimeUnit unit) {
        if (chosen) {
            return true;
        }
        countdown = new CountDownLatch(1);
        try {
            countdown.await(timeout, unit);
        } catch (InterruptedException ex) {
            System.out.println("Interrupted");
        }
        if (!chosen) {
            choose();
            return false;
        } else {
            return true;
        }
    }

    public void choose() {
        choose(new LinkedHashSet());
    }

    public boolean choose(String string) {
        if (chosen) {
            return false;
        }
        String[] indexStrings = string.split(",");
        List<Integer> indices = new LinkedList<Integer>();
        boolean result = true;
        for (String indexString : indexStrings) {
            indexString = indexString.trim();
            try {
                indices.add(Integer.parseInt(indexString));
            } catch (Exception e) {
                result = false;
            }
        }
        return choose(indices) & result;
    }

    public boolean choose(List<Integer> indices) {
        if (chosen) {
            return false;
        }
        Set<Integer> indicesWithoutDuplicates = new LinkedHashSet<Integer>();
        boolean result = true;
        for (int index : indices) {
            result &= indicesWithoutDuplicates.add(index);
        }
        return choose(indicesWithoutDuplicates) & result;
    }

    public boolean choose(Set<Integer> indices) {
        if (chosen) {
            return false;
        }
        boolean result = (indices.size() == numberOfPicks);
        for (int index : indices) {
            if (index >= 0 && index < choosableObjects.size()) {
                chosenIndices.add(index);
                chosenObjects.add(choosableObjects.get(index));
            } else {
                result = false;
            }
        }
        List<Integer> possibleIndices = new LinkedList<Integer>();
        for (int index = 0; index < choosableObjects.size(); index++) {
            possibleIndices.add(index);
        }
        possibleIndices.removeAll(chosenIndices);
        while (chosenObjects.size() < numberOfPicks) {
            int index = possibleIndices.remove(getRandomNumber(possibleIndices.size()));
            chosenIndices.add(index);
            chosenObjects.add(choosableObjects.get(index));
        }
        chosen = true;
        countdown.countDown();
        return result;
    }

    public List<T> getChosenObjects() {
        return chosenObjects;
    }

    public String getChosenObjectsAsString() {
        if (chosenObjects == null) {
            return "";
        } else {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (int index : chosenIndices) {
                if (first) {
                    first = false;
                } else {
                    result.append(", ");
                }
                result.append(index);
            }
            return result.toString();
        }
    }

    public boolean isChosen() {
        return chosen;
    }

    public List<T> getChoosableObjects() {
        return choosableObjects;
    }

    public int getNumberOfPicks() {
        return numberOfPicks;
    }

    public Type getType() {
        return type;
    }

    public Visibility getVisibilty() {
        return type.getVisiblity();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(type.name());
        result.append(": ");
        result.append(numberOfPicks);
        result.append(" of ");
        result.append(choosableObjects);
        result.append("; ");
        result.append(chosenIndices);
        result.append(" = ");
        result.append(chosenObjects);
        result.append(" (" + chosen + ")");
        return result.toString();
    }
}
