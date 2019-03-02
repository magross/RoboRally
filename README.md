# RoboRally
This project provides a server that can host RoboRally games, enforce the game rules and communicate with AI clients using a protocol build upon TCP/IP. The project was used to run AI tournaments in Java classes, letting the students create AIs for RoboRally in groups, and then using this server to put the students' AIs against each other.

## Server
The jar-file of the server can be found under [dist/RoboRally.jar](dist/RoboRally.jar). To run this server, it is sufficient to type `java -jar RoboRally.jar`. The server accepts various optional command line parameters:
 
- `-c` or `--connections`: Sets the maximum number of clients that the server accepts. The default is 50.
- `-x` or `--password`: Sets a password that clients have to provide at registration. If the password is set to "", the clients do not have to provide a password. Notice that whitespace in the beginning and end of the password will be ignored; furthermore, the password is case-sensitive and may not contain the character `|`. The default is "".
- `-m` or `--admin`: Sets an admin-password, with which registered players can get admin rights. Without this password, no player can become admin. Notice that whitespace in the beginning and end of the password will be ignored; furthermore, the password is case-sensitive and may not contain the character `|`. The default is "".
- `-p` or `--port`: Sets the port that the server uses to listen for clients. The default is 8888.
- `-r` or `--replay`: Specifies the folder in which the server looks for replays and saves replays. The default is "replay".
- `-s` or `--scenario`: Specifies the folder in which the server looks for scenarios. The default is "scenario".
- `-a` or `--ai`: Specifies the folder in which the server looks for AIs. An AI needs to be packed in a JAR file containing a class with static methods `createAIClient(String, String, int, String, String, String, String)` and `getAiProfiles()`. See also section [AI](https://github.com/magross/RoboRally/blob/master/README.md#ais). The default is "ai".
- `-h` or `--help`: Shows a list of these optional command line parameters. 
- `--statistics`: If this is passed to the server, the server will add statistics to replays (e.g. when which player arrived with his robot).
- Arguments without flags will be used as the welcome message. The default is "Welcome!".
  
## Communication with clients  
The communication between server and clients is based on a custom protocol of text messages send via TCP. A message consists of a string with a line break at the end, and the message itself consists of one or multiple parts separated by the character `|`. The first part determines the type of the message, and all subsequent parts are parameters that depend on the type of the message.

After a connection has been established, the server sends a welcome message to the client that has the following form:

`WELCOME | <Welcome message>`

where `<Welcome message>` is any string. This message is followed by the message

`AWAITING_REGISTRATION`

After receiving the `AWAITING_REGISTRATION` message, the client can introduce himself and register a name, as described in section [Messages to the server](https://github.com/magross/RoboRally/blob/master/README.md#messages-to-the-server).

If the server does not recognize the type of a message (i.e., the first part of the message), he will reply by a message of the form

`UNKNOWN_MESSAGE | <First part of the client's message>`  

If the client's message is recognized, but not fitting the current state (e.g. the client tries to make a move in a game without having joined a game in the first place), the server will send a message of the form:

`MESSAGE_NOT_ALLOWED_IN_CURRENT_STATE | <First part of the client's message>`  

If the message has too few or too many parameters for its type, the server will reply by

`INCORRECT_NUMBER_OF_PARAMETERS | <Min> | <Max> | <Send>`  

where `<Min>` and `<Max>` are the minimal and maximal number of parameters for the type of message, and `<Send>` is the number of parameters that the client used.

## Messages to the server
The server accepts the following message types from the client:
  - [`INTRODUCE`](https://github.com/magross/RoboRally/blob/master/README.md#introduce)
  - [`REGISTER`](https://github.com/magross/RoboRally/blob/master/README.md#register)
  - [`SEND_SERVER_MESSAGE`](https://github.com/magross/RoboRally/blob/master/README.md#send_server_message)
  - [`SEND_GAME_MESSAGE`](https://github.com/magross/RoboRally/blob/master/README.md#send_game_message)
  - [`SEND_PRIVATE_MESSAGE`](https://github.com/magross/RoboRally/blob/master/README.md#send_private_message)
  - [`ECHO`](https://github.com/magross/RoboRally/blob/master/README.md#echo)
  - [`LIST_GAME_PLAYERS`](https://github.com/magross/RoboRally/blob/master/README.md#list_game_players)
  - [`LIST_GAMES`](https://github.com/magross/RoboRally/blob/master/README.md#list_games)
  - [`LIST_PLAYERS`](https://github.com/magross/RoboRally/blob/master/README.md#list_players)
  - [`LIST_REPLAYS`](https://github.com/magross/RoboRally/blob/master/README.md#list_replays)
  - [`LIST_SCENARIOS`](https://github.com/magross/RoboRally/blob/master/README.md#list_scenarios)
  - [`GET_GAME`](https://github.com/magross/RoboRally/blob/master/README.md#get_game)
  - [`GET_REPLAY`](https://github.com/magross/RoboRally/blob/master/README.md#get_replay)
  - [`GET_SCENARIO`](https://github.com/magross/RoboRally/blob/master/README.md#get_scenario)
  - [`CREATE_GAME`](https://github.com/magross/RoboRally/blob/master/README.md#create_game)
  - [`JOIN_GAME`](https://github.com/magross/RoboRally/blob/master/README.md#join_game)
  - [`START_GAME`](https://github.com/magross/RoboRally/blob/master/README.md#start_game)
  - [`CLOSE_CONNECTION`](https://github.com/magross/RoboRally/blob/master/README.md#close_connection)
  
In the following, all of these message types will be explained in detail. If we are talking about strings, we assume that the character `|` is forbidden, and whitespace in the beginning and end is ignored.

#### INTRODUCE

`INTRODUCE | <Client name> | <Passwort>`

*Number of arguments:* 1-2. *Valid in the following states:* If an introduction has not happened yet.
 
Introduces a client to server, with `<Client name>` being an arbitrary string that tells the server about the type of the AI (e.g. Jane's AI). It is possible for two or more clients to use the same string. The parameter `<Passwort>` is optional and needs only to be given if access to the server is password protected. The password is case-sensitive, whitespace in the beginning and end is ignored. The server sends

`INTRODUCTION_SUCCESSFUL`

if the introduction was successful, otherwise he sends:

`SERVER_ACCESS_DENIED`

This can only happen if the server is password protected and a wrong password was given.

Examples:

`INTRODUCE | Client of group 001`, 
`INTRODUCE | Client X | abcde`

#### REGISTER

`REGISTER | <Name>`

*Number of arguments:* 1. *Valid in the following states:* After introduction, if no name has been registered yet.
 
Registers a name for your client with the server that the server uses to identify you. Other clients that are connected to this server can use this name to send you messages. The name you are registering needs to be server-unique; if `Name` is already in use, the server will reply with

`NAME_ALREADY_IN_USE`
 
 If your name is still available, the server answers with
  
 `REGISTRATION_SUCCESSFUL`
 
 A list of currently used names can be obtained by [`LIST_PLAYERS`](https://github.com/magross/RoboRally/blob/master/README.md#list_players).
 
 Example:
 
 'REGISTER | Marvin'

#### CLOSE_CONNECTION

`CLOSE_CONNECTION`

*Number of arguments:* 0. *Valid in the following states:* Always.

Closes the connection to the server. If the client is participating in a game, the client will leave the game and the players receive a message of the form:

`PLAYER_LEFT | <Name of the leaving player>`

The client receives the message

`CONNECTION_CLOSED | As requested by client.`

The name used by the client will be made available again.

Example:

`CLOSE_CONNECTION`

#### SEND_SERVER_MESSAGE

`SEND_SERVER_MESSAGE | <Message>`

*Number of arguments:* 1. *Valid in the following states:* Once the client has registered a name for itself.

Sends the string `<Message>` to all other players on the server. The server sends these in the form

`SERVER_CHAT_MESSAGE | <Sender> | <Message>`

to all players.

Example:
`SEND_SERVER_MESSAGE | Hello World!`

#### SEND_GAME_MESSAGE

`SEND_GAME_MESSAGE | <Message>`

*Number of arguments:* 1. *Valid in the following states:* If a client is playing a game.

Sends the string `<Message>` to all other players in the same game. The server sends these in the form 

`GAME_CHAT_MESSAGE | <Sender> | <Message>`
 
to all players.

Example:
`SEND_GAME_MESSAGE | Hello World!`

#### SEND_PRIVATE_MESSAGE

`SEND_PRIVATE_MESSAGE | <Player name> | <Message>`

*Number of arguments:* 2. *Valid in the following states:* Once the client has registered a name for itself.

Sends the string `<Message>` to the player with the name `<Player name>`. If this name is not used on the server, the server answers with

`PLAYER_NOT_FOUND`

Otherwise the server sends the message in the form 

`PRIVATE_CHAT_MESSAGE | <Sender> | <Receiver> | <Message>`

to both sender and receiver.

Example:

`SEND_PRIVATE_MESSAGE | Marvin | Don't panic!` 

#### ECHO

`ECHO | <Message>` 

*Number of arguments:* 1. *Valid in the following states:* Once the client has registered a name for itself.

Allows a client to send a private message to itself. This command is a shortcut for
 
 `SEND_PRIVATE_MESSAGE | <Sender> | <Message>`
 
 and correspondingly, the server sends the message in the form:
 
 `PRIVATE_CHAT_MESSAGE | <Sender> | <Sender> | <Message>`

Example:

`ECHO | Echooooooo`

#### LIST_PLAYERS

`LIST_PLAYERS`

*Number of arguments:* 0. *Valid in the following states:* Once the client has introduced itself.

Requests a list of all players registered on the server. The server sends the list in the form

`PLAYERS | <Player1> | <Player2> | ...`
 
 to the client. `<PlayerX>` is the registered name of a player.
 
 Example:
 
 `LIST_PLAYERS`

#### LIST_GAMES

`LIST_GAMES`

*Number of arguments:* 0. *Valid in the following states:* Once the client has registered a name for itself.

Requests a list of all games that are active on the server. The server sends the list in the form

`GAMES | <Game1> | <Game2> | ...`
 
 to the client. `<GameX>` is the name of a game.
 
 Example:
 
 `LIST_GAMES`

#### LIST_SCENARIOS

`LIST_SCENARIOS`

*Number of arguments:* 0. *Valid in the following states:* Once the client has registered a name for itself.

Requests a list of all scenarios that can be used for game creation. The server sends the list in the form

`SCENARIO | <Scenario1> | <Scenario2> | ...`
 
 to the client, with `<ScenarioX>` being the name of a scenario.
 
 Example:
 
 `LIST_SCENARIOS`
 
#### LIST_REPLAYS 

`LIST_REPLAYS`
 
 *Number of arguments:* 0. *Valid in the following states:* Once the client has registered a name for itself.
 
Requests a list of all replays stored by the server. A replay contains a log of a game hosted by a server. Replays are created automatically at the end of game, unless a game was stopped prematurely. The server sends the list in the form

`REPLAYS | <Replay1> | <Replay2> | ...`
 
 to the client, with `<ReplayX>` being the name of a replay, which consists of the name of the game and the date and time on which the game ended.
 
 Example:
 `LIST_REPLAYS`

#### LIST_GAME_PLAYERS

`LIST_GAME_PLAYERS | <Name of the game>`

 *Number of arguments:* 1. *Valid in the following states:* Once the client has registered a name for itself.
 
 Requests a list of all players that have joined the specified game. If no game of the specified name exists, the server answers with 
 
 `GAME_NOT_FOUND | <Name of the game>`
 
 Otherwise the server sends the list in the form

`GAME_PLAYERS | <Player1> | <Player2> | ...`

 to the client. `<PlayerX>` is the registered name of a player.
 
 Example:
 
 `LIST_GAME_PLAYERS | Testgame`

#### GET_GAME

`GET_GAME | <Name of the game>`

 *Number of arguments:* 1. *Valid in the following states:* Once the client has registered a name for itself.

Requests a game on the server. If no game with the requested name exists, the server answer with

`GAME_NOT_FOUND | <Name of the game>`
 
 Otherwise, information will be given in the form
 
 `GAME | <Scenario> | <Current \# of players> | <Maximal \# of players> | <Running?>`
 
 `<Scenario>` is the name of the scenario the game is based on, `<Current \# of players>` and `<Maximal \# of players>` are the current and maximal numbers of players for the game, and `<Laufend?>` specifies if it the game is already running; in this case, it is no longer possible to join the game.
 
`GET_GAME | Testgame`

#### GET_REPLAY

`GET_REPLAY | <Name of the replay>`

 *Number of arguments:* 1. *Valid in the following states:* Once the client has registered a name for itself.

Requests a replay on the server. If no replay with the requested name exists, the server answer with
 
 `REPLAY_NOT_FOUND | <Name of the replay>`
 
 Otherwise, the replay is send in the form
 
 `REPLAYS | <Replay-Data>`
 
 to the client, with `Replay-Data` being all messages from the server related to the game, separated by `||`
 
 `GET_REPLAY | Testgame (28.04.10 12:30:18)`

#### CREATE_GAME

`CREATE_GAME | <Name of the scenario> | <Name of the game>`

 *Number of arguments:* 2. *Valid in the following states:* Once the client has registered a name for itself and is not playing a game.
 
 Creates a new game with specified name based on the given scenario. The game must be unique in the server; if a game of the specified name already exists, the server answers with 
 
 `NAME_ALREADY_IN_USE | <Name of the game>`
 
 If the specified scenario does not exist, the server answers with:
 
 `SCENARIO_NOT_FOUND | <Name of the scenario>`
 
 Otherwise, the game is created and the creating client joins the game; all registered players receive the message

`GAME_CREATED | <Name of the game>`
 
 from the server.
 
 Example:
 
 `CREATE_GAME | Scenario | Test`

#### JOIN_GAME

`JOIN_GAME | <Name of the game>`

 *Number of arguments:* 1. *Valid in the following states:* Once the client has registered a name for itself and is not playing a game.

Joins the game with the specified game, if there is still room for another player and the game is not already running. If the server cannot find a game with the specified name, he answers with

`GAME_NOT_FOUND | <Name of the game>`
 
 If the game is already running, or the game has already the maximal number of players, the server answers with 
 
 `JOINING_FAILED | <Name of the game>`
 
 Otherwise, the client and all players that already joined the game receive the message 
 
  `PLAYER_JOINED | <Name of the player>`
 
 Should a player at some point leave the game, a message of the form
 
 `PLAYER_LEFT | <Name of player>`
 
 will be sent.
 
 Example:
 
 `JOIN_GAME | Testgame`

#### START_GAME

`START_GAME`

 *Number of arguments:* 0. *Valid in the following states:* Once the client has registered a name for itself and has created a game.
 
 Starts a game that has been created by the client. After the game has been started, it will not be possible for other players to join the game. If the game has already been started, the server responds with
 
 `GAME_IS_ALREADY_RUNNING`
 
 Otherwise, the server starts the game and sends all clients that are participating in the game the following messages:
 
 `GAME_STARTED | <Name of the game>`
 
 Next, the order of the players will randomized, and the resulting order will be broadcasted to the players by 
 
 `GAME_PLAYERS | <Player1> | <Player2> | ...`
 
 After that, all clients will recieve the scenario information in form of a message of the type
 
 `GAME_STATUS | ...`
 
 The scenario information has the same form as for the `GET_SCENARIO` command.
 
 Example:
 
 `START_GAME`

#### GAME_CHOICE

`GAME_CHOICE | <Type of choice> | <Choice> `

 *Number of arguments:* 2. *Valid in the following states:* When playing a game.

Used to make a choice that was requested by the server in form of a message of the type:

`CHOOSE | <Type of choice> | <Number of picks> | <Option1> | <Option2> | ...`
 
 `<Type of choice>` has to be identical in both messages. <Auswahl>} besteht aus einer durch Kommata getrennten Liste von Zahlen. Die Anzahl der Zahlen in dieser Liste muss der \texttt{<Anzahl>} aus der \texttt{CHOOSE}-Nachricht entsprechen. Die Zahlen werden als Indizes der in der Server-Nachricht gelisteten Optionen interpretiert, wobei 0 der Index der ersten Option ist. Dementsprechend dürfen die Zahlen in der Auswahlliste nur ganze Zahlen zwischen 0 und \# Optionen$-1$ sein; außerdem darf keine Zahl doppelt vorkommen. Der Server gibt die Wahl des Spielers mittels
 \[
  \text{\texttt{CHOSEN | <Spielername> | <Art der Auswahl> | <Gewählte Objekte>}}
 \]
 bekannt. Trifft ein Client eine ungültige Wahl, ignoriert der Server diese und wählt stattdessen zufällig. Antwortet der Client nicht innerhalb einer serverspezifischen Frist auf eine \texttt{CHOOSE}-Nachricht, trifft der Server eine zufällige Auswahl und sendet
 \[
  \text{\texttt{TIMEOUT | <Spielername> | <Art der Auswahl> }}
 \]
 an alle Spieler. Erkennt der Server die vom Client angegebene \texttt{<Art der Auswahl>} nicht, antwortet er mit 
 \[
  \text{\texttt{UNKNOWN\_CHOICE}}
 \]
 Erkennt der Server die Art der Auswahl und erwartet sie zu dem aktuellen Zeitpunkt aber nicht, schickt er
 \[
  \text{\texttt{NOT\_WAITING\_FOR\_THIS\_CHOICE}}
 \]
 zurück. Enthält \texttt{<Auswahl>} etwas ungültiges (z.B. Zeichenketten), wird 
 \[
  \text{\texttt{ILLEGAL\_CHOICE}}
 \]
 zurückgegeben.
 }
 {GAME\_CHOICE | PROGRAMMING | 0,8,3,4,5} 
 
#### GET_SCENARIO

`GET_SCENARIO | <Name of the scenario>`
 
  *Number of arguments:* 1. *Valid in the following states:* Once the client has registered a name for itself.
 
 { Fragt ein auf dem Server existierendes Szenario ab. Existiert kein Szenario mit dem angegebenen Namen, antwortet der Server mit
 \[
  \text{\texttt{SCENARIO\_NOT\_FOUND | <Szenarioname>}}
 \]
 Anderenfalls werden Informationen über das Szenario in der Form
 \ama
  &\text{\texttt{SCENARIO | <Name> | <Breite> | <Höhe> | <Schwierigkeit> | <Dauer>}}\\
  &\text{\texttt{| <Min. Spieler> | <Max. Spieler> | <Autor> | <Beschreibung>}}\\
  &\text{\texttt{| <Spielfeld>}} 
 \ema
 geschickt. \texttt{<Name>} ist dabei der Name des Szenarios, \texttt{<Breite>} und \texttt{<Höhe>} sind Breite und Höhe des (immer rechteckigen) Spielfelds. \texttt{<Schwierigkeit>} ist ein vom Author festgelegter Hinweis auf die Schwierigkeit der Strecke (\texttt{EASY}, \texttt{MEDIUM} oder \texttt{EXPERT}); \texttt{<Dauer>} ein Hinweis auf die zum Spielen des Szenarios benötigte Zeit (\texttt{SHORT}, \texttt{MEDIUM} oder \texttt{LONG}). \texttt{Min. Spieler} und \texttt{Max. Spieler} geben die empfohlende Mindestanzahl von Spielern und die maximale Anzahl von Spielern für das Szenario an, es wird nur letztere vom Server erzwungen. \texttt{<Autor>} ist der Ersteller der Karte, \texttt{<Beschreibung>} ist eine vom Autor verfasste Beschreibung der Karte. \texttt{<Spielfeld>} beschreibt schließlich das Spielfeld, siehe dazu den nächsten Abschnitt. 
 }
 {GET\_SCENARIO | Risky Exchange}  
   
 ## Encoding of scenarios

The area is always rectangular, and every line of the area is terminated by the character `|`, and consists of game fields.

A game field consists of one or more factory elements, which can either be the center of the field, or on one the four borders (north, east, south, west) of the field. A factory element is denoted by a sequence of upper-case characters, which specify the type of the factory element. Appended to the upper case characters can be lower case characters and numbers, that act as parameters for the factory element.

Factory elements that are only active in specific phases can have numbers from 1-5 appended to them to specify in which phases these elements become active. For example, `PU24` refers to a pusher factory element that is active in phase 2 and 4. 

 An Phasen-aktive Elemente können die Ziffern 1-5 angehängt werden um zu beschreiben, in welchen Phasen diese Elemente aktiv sind. \text{\texttt{PU24}} steht z.B. für einen Pusher, der in der 2. und 4. Phase aktiv wird. Band-Elemente wie Förderbänder haben eine Richtung, in der sie Transporieren, welche durch einen Kleinbuchstaben (n,e,s,w für Norden, Osten, Süden, Westen) direkt nach den Großbuchstaben angegeben wird. Werden keine weiteren Kleinbuchstaben angegeben, wird davon ausgegangen, dass das Band aus der gegenüberliegenden Richtung kommt. Ansonsten können die Herkunftsrichtungen durch weitere Kleinbuchstaben beschrieben werden. \texttt{Cwns} ist z.B. ein Förderband, was von Norden und Süden nach Westen transportiert (ein T-Stück also). Die Ziffern der Checkpoints definieren die Reihenfolge, in der die Checkpoints angefahren werden müssen, der erste Checkpoint hat dabei die Nummer 1. Eine Übersicht der Elemente findet sich in der Tabelle.
 
 Besitzt ein Feld mehrere Elemente an derselben Position, z.B. eine Mauer und ein Laser, werden die Elemente durch Leerzeichen getrennt und mit runden Klammern eingeschlossen, in dem Beispiel so: \texttt{(W L)}. 
 
 Die unterschiedlichen Positionen eines Feldes (Mitte, Norden, Osten, Süden, Westen) werden wie folgt dargestellt: zuerst die Mitte, dann die vier Ränder in der Reihenfolge Norden, Osten, Süden, Westen eingeschlossen in eckige Klammern. Ein Feld mit Mauer und Laser in Norden und Westen sowie einer in der ersten Phase aktiven Presse wäre: \texttt{CR1[(W L)\_\_(W L)]}. Ein leeres Feld wäre entsprechend \texttt{\_[\_\_\_\_]}. Für Felder mit leerem Rand gibt es eine Abkürzung -- fehlen die eckigen Klammern nach einem Feld, wird automatisch ein leerer Rand angenommen.
 
 Felder in einer Zeile werden durch Leerzeichen getrennt.   
 
 \begin{table}
 \centering
 \begin{tabular}{|l|l|l|l|l|l|}
  \hline
  Abk. & Zugehöriges Element & Band & Phasen & Rand  & Weitere Parameter\\
  \hline
  \_ & Leer                  & Nein & Nein & Überall & --\\
  \hline
  C  & Förderband            & Ja   & Nein & Mitte   & --\\
  \hline
  CR & Presse                & Nein & Ja   & Mitte   & --\\
  \hline
  CP & Checkpoint            & Nein & Nein & Mitte   & Ziffer von 1-\# Checkpoints\\
  \hline
  E  & Expressförderband     & Ja   & Nein & Mitte   & --\\
  \hline
  G  & Zahnrad               & Nein & Nein & Mitte   & l oder r für die Richtung\\
  \hline
  L  & Laser                 & Nein & Nein & Rand    & --\\
  \hline
  P  & Grube                 & Nein & Nein & Mitte   & --\\
  \hline
  PU & Schieber              & Nein & Ja   & Rand    & --\\
  \hline
  R  & Reparaturfeld         & Nein & Nein & Mitte   & --\\
  \hline
  SP & Startpunkt            & Nein & Nein & Mitte   & Ziffer von 1-\# Spieler\\
  \hline
  U  & Großes Reparaturfeld  & Nein & Nein & Mitte   & --\\
  \hline
  W  & Mauer                 & Nein & Nein & Rand    & --\\
  \hline
 \end{tabular}   
 \end{table}
   
 ## Communication during the game
 
 After the clients received their `GAME_STATUS`-messages, they will be prompted by the server with 
 
 `CHOOSE | SPAWN_DIRECTION`
 
 messages to choose the spawn direction of their robots.
 
 At the start of the new turn, the clients receive a 
 
 `NEW_TURN | <Number of turn>`
 
 message. Powered down robots will be prompted with  a 
 
 `CHOOSE | REMAIN_POWERED_DOWN` 
 
 message whether they want to remain powered down. After that, the clients receive the cards for programming their robots and have to make a choice. This happens by a 
 
 `CHOOSE | PROGRAMMING` 
 
 message, which all clients do in parallel. After all clients replied to the prompt, all clients are prompted one after the other whether they want to power down their robots for the next round by a 
 
 `CHOOSE | ANNOUNCE_POWER_DOWN`
 
 message.
 
 After this, the server sends
 
 `EXECUTING_PROGRAM_CARDS`
 
 and begins to execute the programs the clients set for their robots. At the beginning of each phase, the server sends 
 
 `NEW_PHASE | <Phase number>`
 
 to the clients. Immediately before executing a program card, the server announces via 
 
 `EXECUTE_PROGRAM_CARD | <Program card>`
 
 what is being executed. After the end of each phase and at the end of each turn the clients receive an update about the state of their robots by a message of the type:
 \ama
  &\text{\texttt{ROBOT\_STATUS | <Name> | <Lebenspunkte> | <Max. Lebenspunkte>}}\\
  &\text{\texttt{| <Zerstört?> | <Fortschritt> | <Archiv-Feld> | <Aktuelles Feld>}}\\
  &\text{\texttt{| <Richtung> | <Abschalten angekündigt?> | <Abgeschaltet?>}} 
 \ema
 Dabei ist \texttt{<Name>} der Name des Spieler, dem der Roboter gehört; \texttt{<Lebenspunkte>} und \texttt{<Max. Lebenspunkte>} sind die aktuellen und maximalen Lebenspunkte des Roboters. \texttt{<Zerstört?>} gibt an, ob der Roboter zerstört ist, \texttt{<Fortschritt>} gibt die Anzahl erreichter Checkpoints an. \texttt{<Archiv-Feld>} und \texttt{<Aktuelles Feld>} geben die Position der Sicherungskopie und des Roboters an, die Koordinaten haben die Form $(x,y)$, wobei der Ursprung das oberste linke Feld des Spielplans ist. \texttt{<Richtung>} gibt die Richtung des Roboters an (NORTH, EAST, SOUTH, WEST), \texttt{<Abschalten angekündigt?>} und \texttt{<Abgeschaltet?>} genau das, was sie vermuten lassen.
                 
If a player is destroyed, i.e. its robot is destroyed and there are no more lives left, a 

`PLAYER_DESTROYED | <Name of player>` 

message is sent. If a player reaches the final checkpoint, a 

`PLAYER_ARRIVED | <Name of player>`

is sent. Once all players are destroyed or have arrived at the last checkpoint, the game is over and a 

`GAME_OVER | <Player1> | <Player2> | ...`

is sent, with the players being ordered in their order of arrival. After the game is over, a replay with the game name and the end date and time as its name will be saved.
  
## AIs  
  

