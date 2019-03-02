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

After receiving the `AWAITING_REGISTRATION` message, the client can introduce himself and register a name, as described in section MESSAGES TO THE SERVER.

If the server does not recognize the type of a message (i.e., the first part of the message), he will reply by a message of the form

`UNKNOWN_MESSAGE | <First part of the client's message>`  

If the client's message is recognized, but not fitting the current state (e.g. the client tries to make a move in a game without having joined a game in the first place), the server will send a message of the form:

`MESSAGE_NOT_ALLOWED_IN_CURRENT_STATE | <First part of the client's message>`  

If the message has too few or too many parameters for its type, the server will reply by

`INCORRECT_NUMBER_OF_PARAMETERS | <Min> | <Max> | <Send>`  

where `<Min>` and `<Max>` are the minimal and maximal number of parameters for the type of message, and `<Send>` is the number of parameters that the client used.

## Messages to the server
The server accepts the following message types from the client:
  - `INTRODUCE`
  - `REGISTER`
  - `SEND_SERVER_MESSAGE`
  - `SEND_GAME_MESSAGE`
  - `SEND_PRIVATE_MESSAGE`
  - `ECHO`
  - `LIST_GAME_PLAYERS`
  - `LIST_GAMES`
  - `LIST_PLAYERS`
  - `LIST_REPLAYS`
  - `LIST_SCENARIOS`
  - `GET_GAME`
  - `GET_REPLAY`
  - `GET_SCENARIO`
  - `CREATE_GAME`
  - `JOIN_GAME`
  - `START_GAME`
  - `CLOSE_CONNECTION`
  
In the following, all of these message types will be explained in detail. If we are talking about strings, we assume that the character `|` is forbidden.

#### INTRODUCE

`INTRODUCE | <Client name> | <Passwort>`

*Number of arguments:* 1-2. *Valid in the following states:* If an introduction has not happened yet.
 
Introduces a client to server, with `<Client name>` being an arbitrary string that tells the server about the type of the AI (e.g. Jane's AI). It is 
 
Es können mehrere Clienten des selben Typs gleichzeitig mit dem Server verbunden sein. Der Parameter \texttt{<Passwort>} ist optional und muss nur angegeben werden, wenn der Zugang zum Server durch ein Passwort geschützt ist. Das Passwort muss die korrekte Groß- \& Kleinschreibung aufweisen, Leerzeichen am Anfang und Ende des Passwortes werden ignoriert. Der Server sendet

`INTRODUCTION\_SUCCESSFUL`

if the introduction was successful, otherwise he sends:

`SERVER_ACCESS_DENIED`

This can only happen if the server is password protected and a wrong password was given.

Examples:

`INTRODUCE | Client of group 001`
`INTRODUCE | Client X | abcde`

#### REGISTER

 \clientmessage
 {REGISTER | <Spielername>}
 {1}
 {Nach der Vorstellung, solange noch kein Name registriert wurde}
 { Registriert beim Server einen Namen für euren Clienten, der vom Server benutzt wird, euch zu identifizieren. Andere Clienten können euch mit Hilfe dieses Namens Nachrichten schicken, wenn sie mit demselben Server verbunden sind. Dieser Name muss innerhalb des Servers eindeutig sein; benutzt ein anderer Client bereits den von euch gewünschten Namen, antwortet der Server mit
 \[
  \text{\texttt{NAME\_ALREADY\_IN\_USE}}
 \]
 Wird der Name noch nicht benutzt, antwortet der Server mit
 \[
  \text{\texttt{REGISTRATION\_SUCCESSFUL}}
 \] 
 Eine Liste aktuell schon vergegeben Namen könnt ihr mit \texttt{LIST\_PLAYERS} erhalten.
 }
 {REGISTER | Marvin}

#### CLOSE_CONNECTION

  \clientmessage
  {CLOSE\_CONNECTION}
  {Keine}
  {Immer}
  {Trennt die Verbindung mit dem Server. Nimmt der Client gerade an einem Spiel teil, verlässt der Client das Spiel und die Spieler erhalten eine Nachricht der Form:
    \[
     \text{\texttt{PLAYER\_LEFT | <Spielername>}}
    \]
    Der Client selbst erhält die Nachricht
    \[
     \text{\texttt{CONNECTION\_CLOSED | As requested by client.}}
    \]
    Der vom Client benutzte Name wird vom Server wieder zur Benutzung freigegeben.
  }
  {CLOSE\_CONNECTION}

#### SEND_SERVER_MESSAGE

 \clientmessage
 {SEND\_SERVER\_MESSAGE | <Nachricht>}
 {1}
 {Sobald ein Name registriert wurde}
 { Schickt die spezifizierte Nachricht an alle auf dem Server registrierten Spieler. Der Server schickt sie in der Form 
 \[
  \text{\texttt{SERVER\_CHAT\_MESSAGE | <Sender> | <Nachricht>}}
 \]
 an alle Spieler weiter.
 }
 {SEND\_SERVER\_MESSAGE | Hallo Welt!}

#### SEND_GAME_MESSAGE

 \clientmessage
 {SEND\_GAME\_MESSAGE | <Nachricht>}
 {1}
 {Wenn man ein Spiel spielt}
 { Schickt die spezifizierte Nachricht an alle Spieler, die gerade mit dem Client spielen. Der Server schickt sie in der Form 
 \[
  \text{\texttt{GAME\_CHAT\_MESSAGE | <Sender> | <Nachricht>}}
 \]
 an diese Spieler weiter.
 }
 {SEND\_GAME\_MESSAGE | Hallo Welt!}

#### SEND_PRIVATE_MESSAGE

 \clientmessage
 {SEND\_PRIVATE\_MESSAGE | <Spielername> | <Nachricht>}
 {2}
 {Wenn man auf dem Server registriert ist}
 { Schickt die spezifizierte Nachricht an den Spieler mit dem angegebenen Namen. Ist auf dem Server kein Spieler mit diesem Namen registriert, antwortet der Server mit
 \[
  \text{\texttt{PLAYER\_NOT\_FOUND}}
 \]
 Anderenfalls leitet der Server die Nachricht in der Form
 \[
  \text{\texttt{PRIVATE\_CHAT\_MESSAGE | <Sender> | <Empfänger> | <Nachricht>}}
 \]
 an Sender und Emfänger weiter.
 }
 {SEND\_PRIVATE\_MESSAGE | Marvin | Don't panic!}

#### ECHO

 \clientmessage
 {ECHO | <Nachricht>}
 {1}
 {Wenn man auf dem Server registriert ist}
 { Schickt die spezifizierte Nachricht an sich selbst. Dieser Befehl ist eine Abkürzung für 
 \[
  \text{\texttt{SEND\_PRIVATE\_MESSAGE | <Sender> | <Nachricht>}}
 \]
 entsprechend schickt der Server
 \[
  \text{\texttt{PRIVATE\_CHAT\_MESSAGE | <Sender> | <Sender> | <Nachricht>}}
 \] 
 zurück.
 }
 {ECHO | Echooooooo}

#### LIST_PLAYERS

 \clientmessage
 {LIST\_PLAYERS}
 {0}
 {Wenn man auf dem Server vorgestellt ist}
 { Fragt eine Liste der aktuell auf dem Server registrierten Spieler ab. Der Server schickt sie in der Form
 \[
  \text{\texttt{PLAYERS | <Spieler1> | <Spieler2> | ...}}
 \]
 an den Client. \texttt{SpielerX} ist dabei der Name eines Spielers.
 }
 {LIST\_PLAYERS}

#### LIST_GAMES

 \clientmessage
 {LIST\_GAMES}
 {0}
 {Wenn man auf dem Server registriert ist}
 { Fragt eine Liste der aktuell auf dem Server existierenden Spiele ab. Der Server schickt sie in der Form
 \[
  \text{\texttt{GAMES | <Spiel1> | <Spiel2> | ...}}
 \]
 an den Client. \texttt{SpielX} ist dabei der Name eines Spiels.
 }
 {LIST\_GAMES}

#### LIST_SCENARIOS

 \clientmessage
 {LIST\_SCENARIOS}
 {0}
 {Wenn man auf dem Server registriert ist}
 { Fragt eine Liste der aktuell auf dem Server verfügbaren Szenarien ab, aus denen Spiele erstellt werden können. Der Server schickt sie in der Form
 \[
  \text{\texttt{SCENARIO | <Szenario1> | <Szenario2> | ...}}
 \]
 an den Client. \texttt{SzenarioX} ist dabei der Name eines Szenarios.
 }
 {LIST\_SCENARIOS}
 
#### LIST_REPLAYS 
 
 \clientmessage
 {LIST\_REPLAYS}
 {0}
 {Wenn man auf dem Server registriert ist}
 { Fragt eine Liste der aktuell auf dem Server gespeicherten Replays ab. Ein Replay stellt dabei ein Protokoll eines auf dem Server geführten Spiels dar. Replays werden vom Server automatisch nach Spielende erzeugt, sofern ein Spiel nicht vorzeitig beendet wurde.
 \[
  \text{\texttt{REPLAYS | <Replay1> | <Replay2> | ...}}
 \]
 an den Client. \texttt{ReplayX} ist dabei der Name eines Replays, welcher sich aus dem Name des Spiels und Datum \& Zeit, zu der das Spiel beendet wurde, zusammensetzt. 
 }
 {LIST\_REPLAYS} 

#### LIST_GAME_PLAYERS

 \clientmessage
 {LIST\_GAME\_PLAYERS | <Spielname>}
 {1}
 {Wenn man auf dem Server registriert ist}
 { Fragt eine Liste der Spieler ab, die dem angegebenen Spiel beigetreten sind. Existiert kein Spiel mit dem angegebenen Namen, antwortet der Server mit
 \[
  \text{\texttt{GAME\_NOT\_FOUND | <Spielname>}}
 \]
 Anderenfalls wird die Liste in der Form
 \[
  \text{\texttt{GAME\_PLAYERS | <Spieler1> | <Spieler2> | ...}}
 \]
 an den Client geschickt. \texttt{SpielerX} ist dabei der Name eines Spielers.
 }
 {LIST\_GAME\_PLAYERS | CoMa-Testspiel}

#### GET_GAME

 \clientmessage
 {GET\_GAME | <Spielname>}
 {1}
 {Wenn man auf dem Server registriert ist}
 { Fragt ein auf dem Server existierendes Spiel ab. Existiert kein Spiel mit dem angegebenen Namen, antwortet der Server mit
 \[
  \text{\texttt{GAME\_NOT\_FOUND | <Spielname>}}
 \]
 Anderenfalls werden Informationen über das Spiel in der Form
 \[
  \text{\texttt{GAME | <Szenario> | <Akt. \# Spieler> | <Max. \# Spieler> | <Laufend?>}}
 \]
 geschickt. \texttt{<Szenario>} ist dabei der Name des Szenarios, auf dem das Spiel basiert, \texttt{<Akt. \# Spieler>} und \texttt{<Max. \# Spieler>} sind die aktuelle und die maximale Anzahl Spieler für das Spiel und \texttt{<Laufend?>} gibt an, ob man dem Spiel noch beitreten kann oder nicht.
 }
 {GET\_GAME | CoMa-Testspiel} 

#### GET_REPLAY

 \clientmessage
 {GET\_REPLAY | <Replayname>}
 {1}
 {Wenn man auf dem Server registriert ist}
 { Fragt ein auf dem Server gespeichertes Replay ab. Existiert das gewünschte Replay nicht, antwortet der Server mit
 \[
  \text{\texttt{REPLAY\_NOT\_FOUND | <Replayname>}}
 \]
 Anderenfalls wird das Replay in der Form
 \[
  \text{\texttt{REPLAYS | <Replay-Daten>}}
 \]
 an den Client geschickt, wo bei die \texttt{Replay-Daten} alle vom Server geschickten Spiel-Nachrichten sind, jeweils durch \texttt{||} getrennt. 
 }
 {GET\_REPLAY | Blubb (28.04.10 12:30:18)} 

#### CREATE_GAME

 \clientmessage
 {CREATE\_GAME | <Szenarioname> | <Spielname> }
 {2}
 {Wenn man auf dem Server registriert ist und gerade nicht spielt}
 { Erzeugt ein neues Spiel mit dem angegebenen Namen basierend auf dem angegebenen Szenario. Der Spielname muss serverweit eindeutig sein, wird der Name schon benutzt, antwortet der Server mit
 \[
  \text{\texttt{NAME\_ALREADY\_IN\_USE | <Spielname>}}
 \]
 Existiert das angegebene Szenario nicht, wird
 \[
  \text{\texttt{SCENARIO\_NOT\_FOUND | <Szenarioname>}}
 \] 
 zurückgegeben. Anderenfalls tritt der Client dem erzeugten Spiel bei und es wird an alle registrierten Spieler
 \[
  \text{\texttt{GAME\_CREATED | <Spielname>}}
 \] 
 geschickt.
 }
 {CREATE\_GAME | Risky Exchange | CoMa-Testspiel} 

#### JOIN_GAME

 \clientmessage
 {JOIN\_GAME | <Spielname> }
 {1}
 {Wenn man auf dem Server registriert ist und gerade nicht spielt}
 { Tritt dem Spiel bei, sofern noch Plätze frei sind und das Spiel nicht bereits läuft. Findet der Server kein Spiel mit dem angegebenen Namen, wird
 \[
  \text{\texttt{GAME\_NOT\_FOUND | <Spielname>}}
 \]
 zurückgegeben. Läuft das Spiel bereits, oder sind alle Plätze besetzt, antwortet der Server mit
 \[
  \text{\texttt{JOINING\_FAILED | <Spielname>}}
 \] 
 Anderenfalls erhalten der Client und alle weiteren Spieler, die dem Spiel bisher beigetreten sind, die Nachricht
 \[
  \text{\texttt{PLAYER\_JOINED | <Spielername>}}
 \]
 Sollte ein Spieler zu einem späteren Zeitpunkt ein Spiel verlassen, wird
 \[
  \text{\texttt{PLAYER\_LEFT | <Spielername>}}
 \] 
 geschickt.
 }
 {JOIN\_GAME | CoMa-Testspiel} 

#### START_GAME

 \clientmessage
 {START\_GAME }
 {0}
 {Wenn man auf dem Server registriert ist und ein Spiel erstellt hat}
 { Startet ein Spiel, was man zuvor erstellt hat. Weitere Spieler können dem Spiel dann nicht mehr beitreten. Läuft das Spiel bereits, anwortet der Server mit
 \[
  \text{\texttt{GAME\_IS\_ALREADY\_RUNNING}}
 \] 
 Der Server fängt dann mit dem Spiel an und schickt an alle mitspielenden Clienten folgende Nachrichten:
 \[
  \text{\texttt{GAME\_STARTED | <Spielname>}}
 \]
 Anschließend wird die Reihenfolge der Spieler zufällig ausgelost, die resultierende Reihenfolge wird den Spielern mittels Nachrichten der Form
 \[
  \text{\texttt{GAME\_PLAYERS | <Spieler1> | <Spieler2> | ...}}
 \] 
 bekannt gegeben. Danach erhalten alle Clienten die Szenario-Daten mittels einer 
 \[
  \text{\texttt{GAME\_STATUS | ...}} 
 \]
 Nachricht. Die Scenario-Daten haben dieselbe Form wie bei \texttt{GET\_SCENARIO}. 
 }
 {START\_GAME} 

#### GAME_CHOICE

 \clientmessage
 {GAME\_CHOICE | <Art der Auswahl> | <Auswahl>}
 {2}
 {Wenn man ein Spiel spielt}
 { Trifft eine vom Server mit
 \[
  \text{\texttt{CHOOSE | <Art der Auswahl> | <Anzahl> | <Option1> | <Option2> | ...}}
 \] 
 angeforderte Auswahl. \texttt{<Art der Auswahl>} muss in dieser Nachricht identisch zu der in der \texttt{CHOOSE}-Nachricht sein. \texttt{<Auswahl>} besteht aus einer durch Kommata getrennten Liste von Zahlen. Die Anzahl der Zahlen in dieser Liste muss der \texttt{<Anzahl>} aus der \texttt{CHOOSE}-Nachricht entsprechen. Die Zahlen werden als Indizes der in der Server-Nachricht gelisteten Optionen interpretiert, wobei 0 der Index der ersten Option ist. Dementsprechend dürfen die Zahlen in der Auswahlliste nur ganze Zahlen zwischen 0 und \# Optionen$-1$ sein; außerdem darf keine Zahl doppelt vorkommen. Der Server gibt die Wahl des Spielers mittels
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
 
 \clientmessage
 {GET\_SCENARIO | <Szenarioname>}
 {1}
 {Wenn man auf dem Server registriert ist}
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
   
 \subsection{Kommunikation während des Spiels}
 
 Nachdem die Clienten die \texttt{GAME\_STATUS}-Nachrichten erhalten haben, werden sie der Reihe nach mit \texttt{CHOOSE | SPAWN\_DIRECTION} aufgefordert, die Startrichtung für ihren Roboter zu wählen. 
 
 Zu Beginn einer neuen Runde erhalten die Spieler eine \texttt{NEW\_TURN | <Rundennummer>}-Nachricht; abgeschaltete Roboter werden mittels \texttt{CHOOSE | REMAIN\_POWERED\_DOWN} gefragt, ob sie abgeschaltet bleiben wollen. Anschließend erhalten die Roboter ihre Karten und werden per \texttt{CHOOSE | PROGRAMMING} aufgefordert, ihre Programmierung vorzunehmen, was alle Roboter parallel machen. Nachdem die Programmierung abgeschlossen ist, werden die Roboter der Reihe nach per \texttt{CHOOSE | ANNOUNCE\_POWER\_DOWN} gefragt, ob sie sich in der nächsten Runde abschalten wollen.
 
 Danach sendet der Server \texttt{EXECUTING\_PROGRAM\_CARDS} und fängt an, die Programme auszuführen. Vor Beginn jeder Phase sendet er dabei jeweils \texttt{NEW\_PHASE | <Phasennummer>} an die Spieler. Unmittelbar vor dem Ausführen einer Programm-Karte kündigt der Server mittels \texttt{EXECUTE\_PROGRAM\_CARD | <Programm-Karte>} an, was gerade ausgeführt wird. Am Ende jeder Phase und am Ende jeder Runde erhalten alle Spieler den aktuellen Zustand jedes Roboters mittels einer Nachricht der Form:
 \ama
  &\text{\texttt{ROBOT\_STATUS | <Name> | <Lebenspunkte> | <Max. Lebenspunkte>}}\\
  &\text{\texttt{| <Zerstört?> | <Fortschritt> | <Archiv-Feld> | <Aktuelles Feld>}}\\
  &\text{\texttt{| <Richtung> | <Abschalten angekündigt?> | <Abgeschaltet?>}} 
 \ema
 Dabei ist \texttt{<Name>} der Name des Spieler, dem der Roboter gehört; \texttt{<Lebenspunkte>} und \texttt{<Max. Lebenspunkte>} sind die aktuellen und maximalen Lebenspunkte des Roboters. \texttt{<Zerstört?>} gibt an, ob der Roboter zerstört ist, \texttt{<Fortschritt>} gibt die Anzahl erreichter Checkpoints an. \texttt{<Archiv-Feld>} und \texttt{<Aktuelles Feld>} geben die Position der Sicherungskopie und des Roboters an, die Koordinaten haben die Form $(x,y)$, wobei der Ursprung das oberste linke Feld des Spielplans ist. \texttt{<Richtung>} gibt die Richtung des Roboters an (NORTH, EAST, SOUTH, WEST), \texttt{<Abschalten angekündigt?>} und \texttt{<Abgeschaltet?>} genau das, was sie vermuten lassen.
                 
Wird ein Spieler zerstört (d.h. sein Roboter ist zerstört und er hat keine Leben mehr), wird \texttt{PLAYER\_DESTROYED | <Spielername>} gesendet. Erreicht ein Spieler den letzten Checkpoint, wird \texttt{PLAYER\_ARRIVED | <Spielername>} geschickt. Sobald alle Spieler zerstört oder angekommen sind ist das Spiel vorbei und es wird \texttt{GAME\_OVER | <Spieler1> | <Spieler2> | ...} gesendet, wobei die Spieler in ihrer Ankunfsreihenfolge geordnet sind.
  
## AIs  
  
%     GAME("%1$s | %2$s | %3$s | %4$s"),
%     JOINING\_FAILED("%1$s"),
%     PLAYER\_JOINED("%1$s"),
%     PLAYER\_ARRIVED("%1$s"),
%     PLAYER\_DESTROYED("%1$s"),
%     NOT\_WAITING\_FOR\_THIS\_CHOICE("%1$s"),
%     UNKNOWN\_CHOICE("%1$s"),
%     CHOOSE {
%     TIMEOUT("%1$s | %2$s"),ILLEGAL_CHOICE,NEW_PHASE("%1$s"),EXECUTING_PROGRAMS,EXECUTING_PROGRAM_CARD("%1$s"),
% 
%     GAME\_OVER("%1$s") {
%     CHOSEN {
% 
% EFFECT\_OCCURRED("%1$s"),ROBOT_STATUS("%1$s"),
%     ADMIN\_PRIVILEGES\_DENIED,
%     ADMIN\_PRIVILEGES\_GRANTED,
%     SAVES("%1$s"),
%     GAMES("%1$s"),
%     SCENARIOS("%1$s"),
%     REPLAYS("%1$s"),
%     REPLAY("%1$s"),
%     REPLAY\_NOT\_FOUND,
%     SCENARIO("%1$s"),
%     GAME\_CREATED("%1$s"),
%     GAME\_PLAYERS("%1$s"),
%     GAME\_STATUS("%1$s"),
%     PLAYER\_LEFT("%1$s"),
%     NEW\_TURN("%1$s"),
%     AWAITING\_REGISTRATION,
%     CONNECTION\_CLOSED("%1$s"),
%     GAME\_CHAT\_MESSAGE("%1$s | %2$s"),
%     INCORRECT\_NUMBER\_OF\_PARAMETERS("%1$s | %2$s | $3$s"),
%     INTRODUCTION\_SUCCESSFUL,
%     MESSAGE\_NOT\_ALLOWED\_IN\_CURRENT\_STATE,
%     NAME\_ALREADY\_IN\_USE,
%     NO\_MESSAGE,
%     PLAYER\_LEFT,
%     PLAYER\_KICKED("%1$s | %2$s"),
%     PLAYER\_NOT\_FOUND("%1$s"),
%     GAME\_NOT\_FOUND("%1$s"),
%     GAME\_STARTED("%1$s"),
%     GAME\_IS\_ALREADY\_RUNNING,
%     PRIVATE\_CHAT\_MESSAGE("%1$s | %2$s | %3$s"),
%     PLAYERS("%1$s"),
%     REGISTRATION\_DENIED,
%     REGISTRATION\_SUCCESSFUL,
%     SCENARIO\_NOT\_FOUND("%1$s"),
%     SERVER\_ACCESS\_DENIED,
%     SERVER\_CHAT\_MESSAGE("%1$s | %2$s"),
%     UNKNOWN\_MESSAGE("%1$s"),
%     WELCOME("%1$s");
%        AUTHOR("Author:"),
%         COURSE("Course:"),
%         DESCRIPTION("Description:"),
%         DIFFICULTY("Difficulty:"),
%         HEIGHT("Height:"),
%         LENGTH("Length:"),
%         MAX\_PLAYERS("Max. Players:"),
%         MIN\_PLAYERS("Min. Players:"),
%         NAME("Name:"),
%         WIDTH("Width:");
%     UNKOWN\_CHOICE,
%   	TURN\_AROUND
%      ROTATE\_LEFT,
%      ROTATE\_RIGHT,
%      BACKMOVE\_1,
%      MOVE\_2, 
%      MOVE\_3, 
% EXECUTING\_PROGRAM\_CARD,
%          GAME\_STARTED, name,
%          GAME\_PLAYERS, players,
%          GAME\_STATUS, 
%          EXECUTING\_PROGRAMS,
%          NEW\_PHASE,
%          PLAYER\_DESTROYED,
%          PLAYER\_ARRIVED,
%          EFFECT\_OCCURRED,
%          ROBOT\_STATUS,
%          BOARD\_ELEMENT\_MOVE,
% 				 LASER\_FIRE,
% 				 TOUCH\_CHECKPOINT,
% 		\item Client Messages
% 		INTRODUCE,
% 		CLOSE\_CONNECTION,
% 		REGISTER,
% 		LIST\_GAMES,
% 		LIST\_PLAYERS,
% 		LIST\_REPLAYS,
% 		LIST\_SCENARIOS,
% 		LIST\_GAME\_PLAYERS,
% 		GET\_GAME,
% 		GET\_REPLAY,
% 		GET\_SCENARIO,
% 		SEND\_PRIVATE\_MESSAGE,
% 		ECHO,
% 		SEND\_SERVER\_MESSAGE,
% 		CREATE\_GAME,
% 		START\_GAME,
% 		JOIN\_GAME,
% 		GAME\_CHOICE,
% 		SEND\_GAME\_MESSAGE,
% 		CHOOSE,
% 		    ANNOUNCE\_POWER\_DOWN,
%         PROGRAMMING(Visibility.RESULT\_ONLY),
%         REMAIN\_POWERED\_DOWN,
%         SPAWN\_TILE,
%         SPAWN\_DIRECTION;
% 	\item m\"ogliche Antworten darauf
% 	INTRODUCTION\_SUCCESSFUL,
% 	SERVER\_ACCESS\_DENIED,
% 	CONNECTION\_CLOSED, "As requested by client.",
% 	REGISTRATION\_SUCCESSFUL,
% 	NAME\_ALREADY\_IN\_USE,
% 	REGISTRATION\_DENIED,
% 	GAMES,
% 	PLAYERS,
% 	REPLAYS,
% 	SCENARIOS,
% 	GAME\_NOT\_FOUND,
% 	GAME\_PLAYERS,
% 	GAME,
% 	REPLAY\_NOT\_FOUND,
% 	REPLAY,
% 	SCENARIO\_NOT\_FOUND,
% 	SCENARIO,
% 	PLAYER\_NOT\_FOUND,
% 	GAME\_CREATED,
% 	GAME\_IS\_ALREADY\_RUNNING,
% 	JOINING\_FAILED,
% 	NO\_MESSAGE,
% 	UNKNOWN\_MESSAGE,
% 	MESSAGE\_NOT\_ALLOWED\_IN\_CURRENT\_STATE,
% 	INCORRECT\_NUMBER\_OF\_PARAMETERS,
% 	NOT\_WAITING\_FOR\_THIS\_CHOICE,
% 	CHOSEN,
