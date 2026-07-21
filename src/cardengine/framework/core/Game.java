package cardengine.framework.core;

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

    /**
     * Wenn ein Spiel gestartet wird soll diese Methode das gesamte Spiel vorbereiten also von Karten vorbereiten Deck shuffeln und Hände verteilen.
     * Also alles im Aktivitätsdiagramm was oberhalb GameLoop steht
     *
     * @author Lukas
     * @param deckFactory erzeugt das showcasespezifische Deck
     * @param winCondition showcasespezifische Siegbedingung
     * @param setup um Haende und Startphase vom Showcase zu bekommen
     */
    public void initGame(DeckFactory deckFactory, WinCondition winCondition, GameSetup setup) {
        //Deck anlegen
        if (deckFactory != null) {
            this.deck = deckFactory.createDeck();
        }

        this.winCondition = winCondition;
        this.currentPhase = startPhase;

        //Deck mischen
        if (deck != null) {
            deck.shuffle();
        }

        //Hand verteilen und startphase vom showcase
        if (setup != null) {
            setup.distributeInitialHands(this);
            this.startPhase = setup.getStartPhase();
        }

        //GameLoop
        gameLoop();
    }

    /**
     * Methode durchläuft den Kern des Frameworks und beendet sobal winCondition erfüllt
     *
     * @author Lukas
     */
    private void gameLoop() {
        boolean weiter = true;
        while (weiter) {
            for (Player player : players) {
                activePlayer = player;
                currentPhase = startPhase;

                // PhaseLoop
                while (currentPhase != null) {
                    currentPhase.aktionDurchfuehren(this);
                    if (checkWinCondition()) {
                        weiter = false;
                        break;
                    }
                }
                if (!weiter) break;
            }
        }
    }

    public Player getActivePlayer() {
        return activePlayer;
    }


    public void executeCommand(Command command) {
        if (commandHistory != null) {
            commandHistory.executeCommand(command);
        }
    }

    /**
     * @author Lukas
     */
    public void undoLastAction() {
        if (commandHistory != null) {
            commandHistory.undo();
        }
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

    public CommandHistory getCommandHistory() {
        return commandHistory;
    }

    public WinCondition getWinCondition() {
        return winCondition;
    }

    public void setWinCondition(WinCondition winCondition) {
        this.winCondition = winCondition;
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
