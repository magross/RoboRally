/*
 * ServerMessage.java
 *
 */
package roborally.server;

/**
 *
 * @author Martin Groß
 */
public enum ServerMessage {

    PLAYER_DISCONNECTED("%1$s"),
    ALLOWED_COMMANDS("%1$s") {
        @Override
        public String getMessage(Object... parameters) {
            return name() + " | " + formatIterable((Iterable<?>) parameters[0]);
        }
    },
    TIMEOUTS_MUST_NOT_BE_NEGATIVE,
    TIMEOUTS_COULD_NOT_BE_PARSED,
    TIMEOUTS("ANNOUNCE_POWER_DOWN=%1$s | PROGRAMMING=%2$s | REMAIN_POWERED_DOWN=%3$s | SPAWN_DIRECTION=%4$s | SPAWN_TILE=%5$s"),
    VERSION("%1$s"),
    GAME("%1$s | %2$s | %3$s | %4$s | %5$s"),
    RANDOM_SEED_USED("%1$s"),
    RANDOM_SEED_COULD_NOT_BE_PARSED("%1$s"),
    JOINING_FAILED("%1$s"),
    PLAYER_JOINED("%1$s"),
    PLAYER_ARRIVED("%1$s"),
    PLAYER_DESTROYED("%1$s"),
    NOT_WAITING_FOR_THIS_CHOICE("%1$s"),
    UNKNOWN_CHOICE("%1$s"),
    CARDS("%1$s"),
    CHOOSE {

        @Override
        public String getMessage(Object... parameters) {
            if (parameters.length == 4) {
                return name() + " | " + parameters[0].toString() + " | " + parameters[1].toString() + " | " + parameters[2].toString() + " | " + formatIterable((Iterable<?>) parameters[3]);
            } else if (parameters.length == 3) {
                return name() + " | " + parameters[0].toString() + " | " + parameters[1].toString() + " | " + formatIterable((Iterable<?>) parameters[2]);
            } else {
                return name() + " | " + parameters[0].toString() + " | " + parameters[1].toString();
            }
        }
    },TIMEOUT("%1$s | %2$s"),ILLEGAL_CHOICE,NEW_PHASE("%1$s"),EXECUTING_PROGRAMS,EXECUTING_PROGRAM_CARD("%1$s"),

    GAME_OVER("%1$s") {
        @Override
        public String getMessage(Object... parameters) {
            return name() + " | " + formatIterable((Iterable<?>) parameters[0]);
        }
    },
    
    CHOSEN {

        @Override
        public String getMessage(Object... parameters) {
            if (parameters.length == 3) {
                return name() + " | " + parameters[0].toString() + " | " + parameters[1].toString() + " | " + formatIterable((Iterable<?>) parameters[2]);
            } else {
                return name() + " | " + parameters[0].toString() + " | " + parameters[1].toString();
            }
        }
    },

EFFECT_OCCURRED("%1$s"),ROBOT_STATUS("%1$s"),
GAME_MASTER_STATUS_GRANTED("%1$s"),
    ADMIN_PRIVILEGES_DENIED,
    ADMIN_PRIVILEGES_GRANTED,
    SAVES("%1$s"),
    AIS("%1$s"),
    GAMES("%1$s"),
    SCENARIOS("%1$s"),
    REPLAYS("%1$s"),
    REPLAY("%1$s"),
    REPLAY_NOT_FOUND,
    SCENARIO("%1$s"),
    GAME_CREATED("%1$s"),
    GAME_PLAYERS("%1$s"),
    GAME_STATUS("%1$s"),
    PLAYER_LEFT("%1$s"),
    NEW_TURN("%1$s"),
    AWAITING_REGISTRATION,
    CONNECTION_CLOSED("%1$s"),
    GAME_CHAT_MESSAGE("%1$s | %2$s"),
    INCORRECT_NUMBER_OF_PARAMETERS("%1$s | %2$s | %3$s"),
    INTRODUCTION_SUCCESSFUL,
    MESSAGE_NOT_ALLOWED_IN_CURRENT_STATE,
    NAME_ALREADY_IN_USE("%1$s"),
    NO_MESSAGE,
    PLAYER_KICKED("%1$s | %2$s"),
    PLAYER_NOT_FOUND("%1$s"),
    GAME_NOT_FOUND("%1$s"),
    AI_NOT_FOUND("%1$s"),
    NEW_PLAYER("%1$s"),
    GAME_STARTED("%1$s"),
    GAME_IS_ALREADY_RUNNING,
    PRIVATE_CHAT_MESSAGE("%1$s | %2$s | %3$s"),
    PLAYERS("%1$s"),
    REGISTRATION_DENIED,
    REGISTRATION_SUCCESSFUL,
    SCENARIO_NOT_FOUND("%1$s"),
    SERVER_ACCESS_DENIED,
    SERVER_CHAT_MESSAGE("%1$s | %2$s"),
    UNKNOWN_MESSAGE("%1$s"),
    WELCOME("%1$s");
    private String string;

    private ServerMessage() {
        this("");
    }

    private ServerMessage(String string) {
        this.string = string;
    }

    public String getMessage(Object... parameters) {
        if (parameters == null || parameters.length == 0) {
            return name();
        } else {
            return String.format(name() + " | " + string, parameters);
        }
    }

    protected String formatIterable(Iterable<?> objects) {
        StringBuilder result = new StringBuilder();
        int index = 0;
        for (Object object : objects) {
            if (index > 0) {
                result.append(" | ");
            }
            result.append(object.toString());
            index++;
        }
        return result.toString();
    }
}
