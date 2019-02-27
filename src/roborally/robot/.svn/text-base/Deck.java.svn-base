/*
 * Deck.java
 *
 */
package roborally.robot;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Martin Groß
 */
public class Deck<T> {

    private List<T> cards;
    private LinkedList<T> discardStack;
    private LinkedList<T> drawStack;
    private Random random;

    public Deck() {
        this(new Random());
    }

    public Deck(long seed) {
        this(new Random(seed));
    }

    public Deck(Random random) {
        this.cards = new LinkedList<T>();
        this.discardStack = new LinkedList<T>();
        this.drawStack = new LinkedList<T>();
        this.random = random;
    }

    public void addCard(T card) {
        cards.add(card);
    }

    public void removeCard(T card) {
        cards.remove(card);
    }

    public void createStack() {
        discardStack = new LinkedList<T>(cards);
        shuffleStack();
    }

    public void discardStack() {
        discardStack.addAll(drawStack);
        drawStack.clear();
    }

    public void shuffleStack() {
        LinkedList<T> result = new LinkedList<T>();
        while (!discardStack.isEmpty()) {
            result.add(discardStack.remove(random.nextInt(discardStack.size())));
        }
        drawStack = result;
    }

    public T drawCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("No deck has been defined.");
        }
        if (drawStack.isEmpty()) {
            if (discardStack.isEmpty()) {
                createStack();
            } else {
                shuffleStack();
            }
        }
        return drawStack.poll();
    }

    public List<T> drawCards(int number) {
        List<T> result = new LinkedList<T>();
        for (int i = 0; i < number; i++) {
            result.add(drawCard());
        }
        return result;
    }

    public void discardCard(T card) {
        discardStack.add(card);
    }

    public void discardCards(T... cards) {
        for (T card : cards) {
            discardCard(card);
        }
    }

    public void discardCards(Iterable<T> cards) {
        for (T card : cards) {
            discardCard(card);
        }
    }
}
