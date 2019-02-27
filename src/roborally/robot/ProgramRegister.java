/*
 * ProgramRegister.java
 *
 */

package roborally.robot;

/**
 *
 * @author Martin Groß
 */
public class ProgramRegister {

    private boolean active;
    private ProgramCard command;

    public ProgramRegister() {
        active = true;
        command = null;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ProgramCard getCard() {
        return command;
    }

    public void setCard(ProgramCard command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return active + " " + command;
    }
}
