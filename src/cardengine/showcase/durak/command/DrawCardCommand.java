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
        if (deck.isEmpty()) {
            moved = false; return;
        }
        card = deck.drawCard();
        getPlayer().getHand().addCard(card);
        moved = true;
    }

    @Override
    public void undo() {
        // ist nicht möglich da nach dem Nachziehen kein undo möglich ist
        // erstmal ergibt das kein Sinn und zweitens müsste dafür das Deck-Interface um returnCard oder sowas erweitert werden

    }
}
