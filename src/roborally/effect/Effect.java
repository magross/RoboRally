/*
 * Effect.java
 *
 */

package roborally.effect;

import java.util.LinkedList;
import java.util.List;
import roborally.robot.Robot;

/**
 *
 * @author Martin Groß
 */
public abstract class Effect implements Comparable<Effect> {

    protected Robot r;
    private int priority;
    protected List<String> messages;

    public Effect() {
        messages = new LinkedList<String>();
    }

    public Effect(int priority) {
        this();
        this.priority = priority;
    }

    public void setRobot(Robot robot) {
        this.r = robot;
    }

    public void prepare() {
        messages.clear();
    }

    public abstract void activate();

    public abstract void activate(Robot robot);

    public int compareTo(Effect effect) {
        return effect.priority - priority;
    }

    public boolean isConflictingWith(Effect effect) {
        return false;
    }

    public List<String> getMessages() {
        return messages;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
