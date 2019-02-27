/*
 * ClientMessage.java
 *
 */
package roborally.server;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import roborally.Choice.Type;
import roborally.ai.AI;
import static roborally.server.PlayerState.*;
import static roborally.server.ServerMessage.*;

/**
 *
 * @author Martin Groß
 */
public enum ClientMessage {

    LIST_SUMMONABLE_AIS(0, 0, PlayerState.REGISTERED, PlayerState.PLAYING, PlayerState.GAME_MASTER) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            sender.sendMessage(AIS, formatArray(server.getAIs()));
        }
    },
    ASSIGN_AI(3, 3, PlayerState.ADMIN) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            AI ai = server.getAI(params[1]);
            if (ai == null) {
                sender.sendMessage(AI_NOT_FOUND, params[1]);
            } else {
                ai.createAIClient(params[1], "127.0.0.1", server.getPort(), server.getPassword(), params[2], params[3]);
            }
        }
    },
    SUMMON_AI(2, 2, PlayerState.GAME_MASTER) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            AI ai = server.getAI(params[1]);
            if (ai == null) {
                sender.sendMessage(AI_NOT_FOUND, params[1]);
            } else {
                String name;
                if (params.length == 3) {
                    name = params[2];
                } else {
                    name = params[1];
                }
                ai.createAIClient(params[1], "127.0.0.1", server.getPort(), server.getPassword(), name, sender.getGame().getName());
            }
        }
    },
    LEAVE_GAME(0, 0, PlayerState.PLAYING) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            sender.getGame().leave(sender);
        }
    },
    GET_ALLOWED_COMMANDS(0, 0, PlayerState.WAITING, PlayerState.REGISTERED, PlayerState.PLAYING, PlayerState.INTRODUCED) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            List<String> allowedCommands = new LinkedList<String>();
            for (ClientMessage message : ClientMessage.values()) {
                boolean allowed = false;
                for (PlayerState state : sender.getState()) {
                    allowed |= message.getAllowedStates().contains(state);
                }
                if (allowed) {
                    allowedCommands.add(message.name());
                }
            }
            Collections.sort(allowedCommands);
            sender.sendMessage(ServerMessage.ALLOWED_COMMANDS, allowedCommands);
        }
    },
    SERVER_VERSION(0, 0, PlayerState.WAITING, PlayerState.REGISTERED, PlayerState.PLAYING, PlayerState.INTRODUCED) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            sender.sendMessage(ServerMessage.VERSION, Server.getVersion());
        }
    },
    INTRODUCE(1, 2, PlayerState.WAITING) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            if (params.length == 2 && server.getPassword().isEmpty() || params.length == 3 && server.getPassword().equals(params[2])) {
                sender.setClientType(params[1]);
                sender.setState(PlayerState.INTRODUCED);
                sender.sendMessage(ServerMessage.INTRODUCTION_SUCCESSFUL);
            } else {
                sender.sendMessage(ServerMessage.SERVER_ACCESS_DENIED);
            }
        }
    },
    CLOSE_CONNECTION(0, PlayerState.WAITING, PlayerState.INTRODUCED, PlayerState.REGISTERED, PlayerState.PLAYING) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            if (sender.getGame() != null) {
                sender.getGame().leave(sender);
            }
            sender.sendMessage(CONNECTION_CLOSED, "As requested by client.");
            sender.setState(NOT_CONNECTED);
            sender.setRunning(false);
            server.unregisterPlayer(sender);
        }
    },
    REGISTER(1, 1, PlayerState.INTRODUCED) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            if (params.length == 2 && server.getNamePassword(params[1]).isEmpty() || params[2].equals(server.getNamePassword(params[1]))) {
                if (server.registerPlayer(params[1], sender)) {
                    sender.setName(params[1]);
                    sender.setState(REGISTERED);
                    sender.sendMessage(REGISTRATION_SUCCESSFUL);
                    server.broadcastMessage(NEW_PLAYER, params[1]);
                } else {
                    sender.sendMessage(NAME_ALREADY_IN_USE, params[1]);
                }
            } else {
                sender.sendMessage(REGISTRATION_DENIED);
            }
        }
    },
    LIST_GAMES(0, PlayerState.REGISTERED, PlayerState.PLAYING) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            sender.sendMessage(GAMES, formatArray(server.getGames()));
        }
    },
    LIST_PLAYERS(0, PlayerState.INTRODUCED, PlayerState.REGISTERED, PlayerState.PLAYING) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            sender.sendMessage(PLAYERS, formatArray(server.getPlayers()));
        }
    },
    LIST_REPLAYS(0, PlayerState.REGISTERED, PlayerState.PLAYING) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            sender.sendMessage(REPLAYS, formatArray(server.getReplays()));
        }
    },
    /*LIST_SAVES(0, PlayerState.REGISTERED, PlayerState.PLAYING) {

    @Override
    protected void handleMessage(Server server, Player sender, String... params) {
    sender.sendMessage(SAVES, formatArray(server.getSaves()));
    }
    },*/
    LIST_SCENARIOS(0, PlayerState.REGISTERED, PlayerState.PLAYING) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            sender.sendMessage(SCENARIOS, formatArray(server.getScenarios()));
        }
    },
    LIST_GAME_PLAYERS(1, PlayerState.REGISTERED, PlayerState.PLAYING) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            Game game = server.getGame(params[1]);
            if (game == null) {
                sender.sendMessage(GAME_NOT_FOUND, params[1]);
            } else {
                sender.sendMessage(GAME_PLAYERS, formatArray(game.getPlayers()));
            }
        }
    },
    GET_GAME(1, PlayerState.REGISTERED, PlayerState.PLAYING) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            Game game = server.getGame(params[1]);
            if (game == null) {
                sender.sendMessage(GAME_NOT_FOUND, params[1]);
            } else {
                sender.sendMessage(GAME, game.getScenario().getName(), game.getPlayers().length, game.getScenario().getMaximumNumberOfPlayers(), game.isRunning(), game.getName());
            }
        }
    },
    GET_REPLAY(1, PlayerState.REGISTERED, PlayerState.PLAYING) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            Replay replay = server.getReplay(params[1]);
            if (replay == null) {
                sender.sendMessage(REPLAY_NOT_FOUND, params[1]);
            } else {
                sender.sendMessage(REPLAY, replay.getDetailedMessage());
            }
        }
    },
    GET_SCENARIO(1, PlayerState.REGISTERED, PlayerState.PLAYING) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            Scenario scenario = server.getScenario(params[1]);
            if (scenario == null) {
                sender.sendMessage(SCENARIO_NOT_FOUND, params[1]);
            } else {
                sender.sendMessage(SCENARIO, scenario.getDetailedMessage());
            }
        }
    },
    SEND_PRIVATE_MESSAGE(2, PlayerState.REGISTERED, PlayerState.PLAYING) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            Player to = server.getRegisteredPlayer(params[1]);
            if (to != null) {
                to.sendMessage(PRIVATE_CHAT_MESSAGE, sender.getName(), to.getName(), params[2]);
                if (to != sender) {
                    sender.sendMessage(PRIVATE_CHAT_MESSAGE, sender.getName(), to.getName(), params[2]);
                }
            } else {
                sender.sendMessage(PLAYER_NOT_FOUND, params[1]);
            }
        }
    },
    ECHO(1, PlayerState.REGISTERED, PlayerState.PLAYING) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            sender.sendMessage(PRIVATE_CHAT_MESSAGE, sender.getName(), sender.getName(), params[1]);
        }
    },
    SEND_SERVER_MESSAGE(1, PlayerState.REGISTERED, PlayerState.PLAYING) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            server.broadcastMessage(SERVER_CHAT_MESSAGE, sender.getName(), params[1]);
        }
    },/*
    SET_PASSWORD(1, PlayerState.REGISTERED, PlayerState.PLAYING) {

    @Override
    protected void handleMessage(Server server, Player sender, String... params) {
    server.setNamePassword(sender.getName(), params[1]);
    }
    },*/
    SET_UP_GAME(2, 4, PlayerState.ADMIN) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            Scenario scenario = server.getScenario(params[1]);
            String name = "";
            String password = "";
            String seed = "";
            if (scenario == null) {
                sender.sendMessage(SCENARIO_NOT_FOUND, params[1]);
                return;
            }
            switch (params.length) {
                case 5:
                    password = params[4].trim();
                case 4:
                    seed = params[3].trim();
                    try {
                        Long.parseLong(seed);
                    } catch (Exception e) {
                        seed = "";
                    }
                case 3:
                    name = params[2].trim();
                    break;
                default:
                    throw new AssertionError("This should not happen: " + Arrays.deepToString(params));
            }
            Game game = server.createGame(scenario, name, password, seed);
            if (game == null && server.getGame(params[2]) != null) {
                sender.sendMessage(NAME_ALREADY_IN_USE, params[2]);
                return;
            }
            //game.join(sender);
            //sender.setState(PLAYING);
            //sender.addState(GAME_MASTER);
            //sender.sendMessage(GAME_MASTER_STATUS_GRANTED, sender.getName());
            server.broadcastMessage(GAME_CREATED, params[2]);
        }
    },
    CREATE_GAME(2, 4, PlayerState.REGISTERED) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            Scenario scenario = server.getScenario(params[1]);
            String name = "";
            String password = "";
            String seed = "";
            if (scenario == null) {
                sender.sendMessage(SCENARIO_NOT_FOUND, params[1]);
                return;
            }
            switch (params.length) {
                case 5:
                    password = params[4].trim();
                case 4:
                    seed = params[3].trim();
                    try {
                        Long.parseLong(seed);
                    } catch (Exception e) {
                        seed = "";
                    }
                case 3:
                    name = params[2].trim();
                    break;
                default:
                    throw new AssertionError("This should not happen: " + Arrays.deepToString(params));
            }
            Game game = server.createGame(scenario, name, password, seed);
            if (game == null && server.getGame(params[2]) != null) {
                sender.sendMessage(NAME_ALREADY_IN_USE, params[2]);
                return;
            }
            game.join(sender);
            sender.setState(PLAYING);
            sender.addState(GAME_MASTER);
            sender.sendMessage(GAME_MASTER_STATUS_GRANTED, sender.getName());
            server.broadcastMessage(GAME_CREATED, params[2]);
        }
    },
    GET_TIMEOUTS(0, PlayerState.PLAYING) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            long[] timeouts = new long[Type.values().length];
            for (int index = 0; index < Type.values().length; index++) {
                timeouts[index] = sender.getGame().getRuleset().getTimeout(Type.values()[index]);
            }
            sender.getGame().broadcastMessage(TIMEOUTS, timeouts[0], timeouts[1], timeouts[2], timeouts[3], timeouts[4]);
        }
    },
    SET_TIMEOUTS(Type.values().length, PlayerState.GAME_MASTER) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            if (!sender.getGame().isRunning()) {
                long[] timeouts = new long[Type.values().length];
                try {
                    for (int index = 0; index < Type.values().length; index++) {
                        timeouts[index] = Long.parseLong(params[index + 1]);
                        if (timeouts[index] < 0) {
                            sender.sendMessage(TIMEOUTS_MUST_NOT_BE_NEGATIVE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage(TIMEOUTS_COULD_NOT_BE_PARSED);
                    return;
                }
                for (int index = 0; index < Type.values().length; index++) {
                    sender.getGame().getRuleset().setTimeout(Type.values()[index], timeouts[index]);
                }
                //System.out.println(Arrays.toString(timeouts));
                sender.getGame().broadcastMessage(TIMEOUTS, timeouts[0], timeouts[1], timeouts[2], timeouts[3], timeouts[4]);
            } else {
                sender.sendMessage(GAME_IS_ALREADY_RUNNING);
            }
        }
    },
    LAUNCH_GAME(1, PlayerState.ADMIN) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            Game game = server.getGame(params[1]);
            if (game == null) {
                sender.sendMessage(GAME_NOT_FOUND, params[1]);
            }
            if (!game.isRunning()) {
                game.start();
            } else {
                sender.sendMessage(GAME_IS_ALREADY_RUNNING);
            }
        }
    },
    START_GAME(0, PlayerState.GAME_MASTER) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            if (!sender.getGame().isRunning()) {
                sender.getGame().start();
            } else {
                sender.sendMessage(GAME_IS_ALREADY_RUNNING);
            }
        }
    },
    JOIN_GAME(1, 1, PlayerState.REGISTERED) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            Game game = server.getGame(params[1]);
            if (game == null) {
                sender.sendMessage(GAME_NOT_FOUND, params[1]);
                return;
            }
            if (game.join(sender)) {
            } else {
                sender.sendMessage(JOINING_FAILED, params[1]);
            }
        }
    },
    GAME_CHOICE(2, PlayerState.PLAYING) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            Game game = sender.getGame();
            game.sendChoice(sender, params[1], params[2]);
        }
    },
    KICK_PLAYER(1, 2, PlayerState.ADMIN) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            Player player = server.getRegisteredPlayer(params[1]);

            if (player == null) {
                sender.sendMessage(PLAYER_NOT_FOUND, params[1]);
            } else {
                if (params.length == 2) {
                    player.sendMessage(CONNECTION_CLOSED, "Kicked by admin.");
                } else {
                    player.sendMessage(CONNECTION_CLOSED, params[2]);
                }
                server.unregisterNickname(params[1]);
                player.setRunning(false);
                if (params.length == 2) {
                    server.broadcastMessage(PLAYER_KICKED, params[1], "Kicked by admin.");
                } else {
                    server.broadcastMessage(PLAYER_KICKED, params[1], params[2]);
                }
            }
        }
    },
    REQUEST_ADMIN_PRIVILEGES(1, 1, PlayerState.REGISTERED) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            if (params.length == 2 && !server.getAdminPassword().isEmpty() && params[1].equals(server.getAdminPassword())) {
                sender.addState(ADMIN);
                sender.sendMessage(ADMIN_PRIVILEGES_GRANTED);
            } else {
                sender.sendMessage(ADMIN_PRIVILEGES_DENIED);
            }
        }
    },
    SEND_GAME_MESSAGE(1, PlayerState.PLAYING) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            if (sender.getGame() != null) {
                sender.getGame().broadcastMessage(sender, params[1]);
            }
        }
    },
    TERMINATE_SERVER(0, 1, PlayerState.ADMIN) {

        @Override
        protected void handleMessage(Server server, Player sender, String... params) {
            if (params.length == 1) {
                server.broadcastMessage(CONNECTION_CLOSED, "Server terminated.");
            } else {
                server.broadcastMessage(CONNECTION_CLOSED, params[1]);
            }
            server.terminate();
        }
    };
    private final int maxNumberOfParameters;
    private final int minNumberOfParameters;
    private final EnumSet<PlayerState> allowedStates;

    private ClientMessage(int numberOfParameters, PlayerState allowedState, PlayerState... allowedStates) {
        this(numberOfParameters, numberOfParameters, allowedState, allowedStates);
    }

    private ClientMessage(int minNumberOfParameters, int maxNumberOfParameters, PlayerState allowedState, PlayerState... allowedStates) {
        this.minNumberOfParameters = minNumberOfParameters;
        this.maxNumberOfParameters = maxNumberOfParameters;
        this.allowedStates = EnumSet.of(allowedState, allowedStates);
    }

    public EnumSet<PlayerState> getAllowedStates() {
        return allowedStates;
    }

    public int getMaxNumberOfParameters() {
        return maxNumberOfParameters;
    }

    public int getMinNumberOfParameters() {
        return minNumberOfParameters;
    }

    public static void handle(Player sender, String input) {
        String[] params = input.split("\\|");
        for (int i = 0; i < params.length; i++) {
            params[i] = params[i].trim();
        }

        if (params.length == 0) {
            sender.sendMessage(NO_MESSAGE);
            return;
        }

        params[0] = params[0].toUpperCase();
        ClientMessage message = null;
        for (ClientMessage m : ClientMessage.values()) {
            if (params[0].equals(m.name())) {
                message = m;
                break;
            }
        }
        if (message == null) {
            sender.sendMessage(UNKNOWN_MESSAGE, params[0]);
            return;
        }

        boolean allowed = false;
        for (PlayerState state : sender.getState()) {
            allowed |= message.getAllowedStates().contains(state);
        }

        if (!allowed) {
            sender.sendMessage(MESSAGE_NOT_ALLOWED_IN_CURRENT_STATE);
            return;
        }

        if (message.getMinNumberOfParameters() > params.length - 1 || params.length - 1 > message.getMaxNumberOfParameters()) {
            sender.sendMessage(INCORRECT_NUMBER_OF_PARAMETERS, message.getMinNumberOfParameters(), message.getMaxNumberOfParameters(), params.length - 1);
            return;
        }

        message.handleMessage(sender.getServer(), sender, params);
    }

    protected void handleMessage(Server server, Player sender, String... params) {
    }

    protected String formatArray(Object[] objects) {
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
