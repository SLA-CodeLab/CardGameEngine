package cardengine.showcase.maumau.command;

import cardengine.framework.command.AbstractCommand;
import cardengine.framework.core.Card;
import cardengine.framework.core.Player;
import cardengine.framework.core.Table;

/**
 * GENERIERT von Claude (Opus 4.8).
 *
 * <p>Command (Command-Pattern), das eine Handkarte auf den Ablagestapel legt.
 * Wie im Framework vorgesehen prueft dieser Command <b>keine</b> Regeln – ob die
 * Karte ueberhaupt passt (Farbe/Zahl), entscheidet {@code MauMauPlayPhase.isValid}.
 * Hier wird nur die Karte bewegt und fuer das Undo gemerkt.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class PlayCardCommand extends AbstractCommand {

    private final Table discardPile;
    private final Card card;
    private boolean moved;

    /**
     * @param player      Spieler, der die Karte legt
     * @param card        die zu legende Handkarte
     * @param discardPile Ablagestapel (der Tisch des Spiels)
     */
    public PlayCardCommand(Player player, Card card, Table discardPile) {
        super(player);
        this.card = card;
        this.discardPile = discardPile;
    }

    @Override
    public void execute() {
        // card.flip() deckt die Karte fuer den Ablagestapel auf und liefert sie zurueck.
        moved = getPlayer().getHand().transferCard(card.flip(), discardPile);
    }

    @Override
    public void undo() {
        if (!moved) {
            return;
        }
        discardPile.transferCard(card.flip(), getPlayer().getHand());
        moved = false;
    }

    /** @return die von diesem Command betroffene Karte (fuer die Regelpruefung in der Phase). */
    public Card getCard() {
        return card;
    }
}
