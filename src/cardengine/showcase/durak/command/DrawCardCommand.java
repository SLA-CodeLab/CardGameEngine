package cardengine.showcase.durak.command;

import cardengine.framework.command.AbstractCommand;
import cardengine.framework.core.Card;
import cardengine.framework.core.Player;
import cardengine.framework.factory.Deck;

/**
 * Lässt den Spieler eine Karte vom Deck ziehen
 *
 * @author Lukas
 */
public class DrawCardCommand extends AbstractCommand {
    private Deck deck;
    private Card card;
    private boolean moved;

    public DrawCardCommand(Player player, Deck deck) {
        super(player);
        this.deck = deck;
    }

    @Override
    public void execute() {
        if (deck == null || deck.isEmpty() || getPlayer() == null) {
            moved = false;
            return;
        }

        card = deck.drawCard();
        if (card != null) {
            getPlayer().getHand().addCard(card);
            moved = true;
        }
    }

    @Override
    public void undo() {
        if (card != null && getPlayer() != null) {
            getPlayer().getHand().removeCard(card);
            //deck.undoDrawCard(); deck.popTrumpf(); deck.shuffle(); //todo hier muss undo für Karte ziehen umgesetzt werden
            moved = false;
        }
    }
}
