package cardengine.showcase.durak.command;

import cardengine.framework.command.AbstractCommand;
import cardengine.framework.core.Card;
import cardengine.framework.core.Player;
import cardengine.framework.core.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Nimmt alle Karten vom Tisch und gibt dem Spieler in die Hand. Speichert aber alle Karten falls ein Undo durchgeführt wird
 *
 * @author Lukas
 */
public class TakeCardCommand extends AbstractCommand {
    private Table table;
    private List<Card> cards;
    private boolean moved;

    public TakeCardCommand(Player player, Table table) {
        super(player);
        this.table = table;
    }

    @Override
    public void execute() {
        cards = new ArrayList<>(table.getCards());
        for (Card card : cards) {
            getPlayer().getHand().addCard(card);
            table.removeCard(card);
        }
        moved = true;
    }

    @Override
    public void undo() {
        for  (Card card : cards) {
            getPlayer().getHand().removeCard(card);
            table.addCard(card);
        }
        moved = false;
    }
}
