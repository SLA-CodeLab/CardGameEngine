package cardengine.framework.core;

import cardengine.framework.observer.GameListener;
import cardengine.framework.state.Phase;
import cardengine.framework.command.Command;
import cardengine.framework.command.CommandHistory;
import cardengine.framework.strategy.WinCondition;
import cardengine.framework.factory.Deck;
import cardengine.framework.factory.DeckFactory;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private Phase currentPhase;
    private Phase startPhase;
    private CommandHistory commandHistory = new CommandHistory();
    private WinCondition winCondition;
    private Deck deck;
    private Table table = new Table();
    private List<Player> players = new ArrayList<>();
    private Player activePlayer;
    private List<GameListener> listeners = new ArrayList<>();

    /**
     * Wenn ein Spiel gestartet wird soll diese Methode das gesamte Spiel vorbereiten also von Karten vorbereiten Deck shuffeln und Hände verteilen.
     * Diese Methode startet das Spiel aber noch nicht das macht start() nur den aufbau
     * Also alles im (alten) Aktivitätsdiagramm was oberhalb GameLoop steht
     *
     * @author Lukas
     * @param deckFactory erzeugt das showcasespezifische Deck
     * @param winCondition showcasespezifische Siegbedingung
     * @param setup um Hände und Startphase vom Showcase zu bekommen
     */
    public void initGame(DeckFactory deckFactory, WinCondition winCondition, GameSetup setup) {
        //Deck anlegen
        if (deckFactory != null) {
            this.deck = deckFactory.createDeck();
        }

        this.winCondition = winCondition;

        //Deck mischen
        if (deck != null) {
            deck.shuffle();
        }

        //Hand verteilen und startphase vom showcase
        if (setup != null) {
            setup.distributeInitialHands(this);
            startPhase = setup.getStartPhase();
        }

        //GameLoop war in der Konsolen version hier nach Aktivitätsdigramm fällt aber jetzt weg
    }

    /*
     * Alter Ansatz mit zwei schleifen. dieser ansatz ist wegen GUI müll daher wird sie durch start() und submitCommand() ersetzt
     * Methode durchläuft den Kern des Frameworks und beendet sobal winCondition erfüllt
     *
     * @author Lukas
     */
//    private void gameLoop() {
//        boolean weiter = true;
//        while (weiter) {
//            for (Player player : players) {
//                activePlayer = player;
//                currentPhase = startPhase;
//
//                // PhaseLoop
//                while (currentPhase != null) {
//                    currentPhase.aktionDurchfuehren(this);
//                    if (checkWinCondition()) {
//                        weiter = false;
//                        break;
//                    }
//                }
//                if (!weiter) break;
//            }
//        }
//    }

    public void start() {
        changePhase(startPhase);
        setActivePlayer(players.get(0));
        notifyStateChanged();
    }

    /**
     *
     * @author Lukas
     */
    public void submitCommand(Command command) {
        if (currentPhase == null || command == null) return;

        if (!currentPhase.isValid(this, command)) {
            notifyInvalidMove(command);
            return;
        }

        commandHistory.executeCommand(command);

        Phase nextPhase = currentPhase.next(this);
        changePhase(nextPhase);

        if (checkWinCondition()) {
            changePhase(null);
            notifyGameOver(getWinner());
        }
        notifyStateChanged();
    }

    public void executeCommand(Command command) {
        if (commandHistory != null) {
            commandHistory.executeCommand(command);
        }
    }

    public void addGameListener(GameListener l) {
        listeners.add(l);
    }

    private void notifyStateChanged() {
        for (GameListener l : new ArrayList<>(listeners)) {
            l.onStateChanged(this);
        }
    }
    private void notifyGameOver(Player winner) {
        for (GameListener l : new ArrayList<>(listeners)) {
            l.onGameOver(winner);
        }
    }
    private void notifyInvalidMove(Command cmd) {
        for (GameListener l : new ArrayList<>(listeners)) {
            l.onInvalidMove(cmd);
        }
    }

    public boolean canUndo() {
        return commandHistory != null && commandHistory.canUndo();
    }

    public void setActivePlayer(Player player) {
        activePlayer = player;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    /**
     * @author Lukas
     */
    public void undoLastAction() {
        if (commandHistory != null) {
            commandHistory.undo();
        }
        notifyStateChanged();
    }

    public void changePhase(Phase newPhase) {
        this.currentPhase = newPhase;
    }

    public boolean checkWinCondition() {
        if (winCondition != null) {
            return winCondition.isGameOver(this);
        }
        return false;
    }

    /**
     * @author Lukas
     */
    public Player getWinner() {
        if (winCondition != null) {
            return winCondition.getWinner(this);
        }
        return null;
    }

    public Phase getCurrentPhase() {
        return currentPhase;
    }

    public Deck getDeck() {
        return deck;
    }

    public Table getTable() {
        return table;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        if (player != null) {
            players.add(player);
        }
    }
}
