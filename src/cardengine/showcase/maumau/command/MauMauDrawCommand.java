package cardengine.showcase.maumau.command;

import cardengine.framework.command.AbstractCommand;
import cardengine.framework.core.Card;
import cardengine.framework.core.Player;
import cardengine.framework.factory.Deck;

/**
 * GENERIERT von Claude (Opus 4.8).
 *
 * <p>Command (Command-Pattern), mit dem der aktive Spieler eine Karte vom
 * Nachziehstapel zieht (wenn er nicht legen kann oder will). Danach ist der
 * naechste Spieler dran – das Weiterschalten uebernimmt die Phase.</p>
 *
 * <p>Ein Undo ist bewusst nicht umgesetzt: Das {@code Deck}-Interface bietet kein
 * "Karte zuruecklegen". Dieselbe Entscheidung trifft auch der Durak-DrawCommand.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class MauMauDrawCommand extends AbstractCommand {

    private final Deck deck;
    private Card card;
    private boolean moved;

    /**
     * @param player Spieler, der zieht
     * @param deck   Nachziehstapel
     */
    public MauMauDrawCommand(Player player, Deck deck) {
        super(player);
        this.deck = deck;
    }

    @Override
    public void execute() {
        if (deck.isEmpty()) {
            moved = false;
            return;
        }
        card = deck.drawCard();
        getPlayer().getHand().addCard(card);
        moved = true;
    }

    @Override
    public void undo() {
        // Nicht unterstuetzt: gezogene Karte kann nicht zurueck ins Deck (siehe Klassen-Javadoc).
    }
}
