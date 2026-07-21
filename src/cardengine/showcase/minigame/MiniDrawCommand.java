package cardengine.showcase.minigame;

import cardengine.framework.command.AbstractCommand;
import cardengine.framework.core.Card;
import cardengine.framework.core.Player;
import cardengine.framework.factory.Deck;

/**
 * GENERIERT ZUM TESTEN DES FRAMEWORKS
 */
public class MiniDrawCommand extends AbstractCommand {

    private Deck deck;
    private Card drawnCard;

    public MiniDrawCommand(Player player, Deck deck) {
        super(player);
        this.deck = deck;
    }

    @Override
    public void execute() {
        if (deck != null) {
            this.drawnCard = deck.drawCard();
            if (this.drawnCard != null) {
                getPlayer().getHand().addCard(this.drawnCard);
                System.out.println("  -> Command EXECUTE: " + getPlayer().getName() + " hat " + this.drawnCard + " gezogen.");
            }
        }
    }

    @Override
    public void undo() {
        if (this.drawnCard != null) {
            getPlayer().getHand().removeCard(this.drawnCard);
            // Zurück aufs Deck legen (Trick: addCard existiert in CardCollection)
            ((cardengine.framework.core.CardCollection)deck).addCard(this.drawnCard);
            System.out.println("  -> Command UNDO: " + getPlayer().getName() + " legt " + this.drawnCard + " zurück aufs Deck.");
            this.drawnCard = null;
        }
    }
}
