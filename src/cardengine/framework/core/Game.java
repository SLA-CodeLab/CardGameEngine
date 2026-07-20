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
    private CommandHistory commandHistory = new CommandHistory();
    private WinCondition winCondition;
    private Deck deck;
    private Table table = new Table();
    private List<Player> players = new ArrayList<>();

    /**
     * Wenn ein Spiel gestartet wird soll diese Methode das gesamte Spiel vorbereiten also von Karten vorbereiten Deck shuffeln und Hände verteilen.
     * Also alles im Aktivitätsdiagramm was oberhalb GameLoop steht
     *
     * @author Lukas
     * @param deckFactory
     * @param winCondition
     */
    public void initGame(DeckFactory deckFactory, WinCondition winCondition) {
        //Deck anlegen
        if (deckFactory != null) {
            this.deck = deckFactory.createDeck();
        }

        //Deck mischen
        if (deck != null) {
            deck.shuffle();
        }

        //Hand verteilen
        distributeHands();

        //GameLoop
        gameLoop();
    }

    /**
     * Diese Methode durchläuft den tatsächlichen GameLoop der sobal die WinCondition erfüllt ist beendet
     *
     * @author Lukas
     */
    private void gameLoop() {
        while (true) {
            //updateTable(); stattdessen muss man mit einer Phase arbeiten nach Command Pattern und dann im Showcase
            //TurnLoop
            for (int i = 0; i < players.size(); i++) {
                Player currentPlayer = players.get(i);

                // PhaseLoop
                while (currentPhase != null) {
                    //Innerhalb Phase ActionLoop
                    currentPhase.aktionDurchfuehren(this);

                    //Am Ende der Phase bevor in die nächste geht soll WinCondition geprüft werden
                    if (checkWinCondition()) {
                        break;
                    }
                    //Nächste Phase oder Nächster Turn oder
                    if (shouldAdvancePhase()) {
                        advanceToNextPhase();
                    } else if (isTurnOver()) {
                        break; //ende für currentPlayer
                    }
                }
            }
        }
    }

    private boolean isTurnOver() {
        //todo
        return false;
    }

    private void advanceToNextPhase() {
        //todo
    }

    private boolean shouldAdvancePhase() {
        //todo
        return false;
    }

    private void distributeHands() {
        //todo
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
