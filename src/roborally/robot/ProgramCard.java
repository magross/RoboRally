package roborally.robot;

/**
 *
 * @author Martin Groß
 */
public class ProgramCard implements Cloneable, Comparable<ProgramCard> {

    private ProgramCommand command;
    private int priority;
    private Robot robot;

    public ProgramCard(ProgramCommand command, int priority) {
        super();
        this.command = command;
        this.priority = priority;
    }

    public ProgramCommand getCommand() {
        return command;
    }

    public void setCommand(ProgramCommand command) {
        this.command = command;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Robot getRobot() {
        return robot;
    }

    public void setRobot(Robot robot) {
        this.robot = robot;
    }

    @Override
    public ProgramCard clone() {
        return new ProgramCard(command, priority);
    }

    @Override
    public int compareTo(ProgramCard card) {
        return card.priority - priority;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProgramCard other = (ProgramCard) obj;
        if (this.command != other.command) {
            return false;
        }
        if (this.priority != other.priority) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.command.hashCode();
        hash = 29 * hash + this.priority;
        return hash;
    }

    public String toMessage() {
        if (robot != null) {
            return String.format("%3$s | (%1$s, %2$s)", priority, command, robot.getPlayer().getName());
        } else {
            return String.format("(%1$s, %2$s)", priority, command);
        }
    }

    @Override
    public String toString() {
        if (robot != null) {
            return String.format("(%1$s, %2$s)", priority, command);
            //return String.format("%3$s: (%1$s, %2$s)", priority, command, robot.getPlayer().getName());
        } else {
            return String.format("(%1$s, %2$s)", priority, command);
        }        
    }
}
