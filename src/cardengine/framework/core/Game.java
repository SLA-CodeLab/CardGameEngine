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

    public void initGame(DeckFactory deckFactory) {
        if (deckFactory != null) {
            this.deck = deckFactory.createDeck();
        }
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
