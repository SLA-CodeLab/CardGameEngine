package cardengine.showcase.maumau;

import cardengine.framework.core.Card;
import cardengine.framework.core.Game;
import cardengine.framework.core.GameSetup;
import cardengine.framework.core.Player;
import cardengine.framework.factory.Deck;
import cardengine.framework.state.Phase;
import cardengine.showcase.maumau.state.MauMauPlayPhase;

/**
 * GENERIERT von Claude (Opus 4.8).
 *
 * <p>Spielaufbau fuer Mau-Mau: verteilt an jeden Spieler {@value #HAND_SIZE}
 * Handkarten und deckt danach eine Startkarte auf den Ablagestapel (den Tisch)
 * auf. Die Startphase ist die {@link MauMauPlayPhase}.</p>
 *
 * <p>Bewusst spielerzahl-agnostisch: es wird ueber {@code game.getPlayers()}
 * iteriert, damit dasselbe Setup mit 2, 3 oder 4 Spielern funktioniert.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class MauMauSetup implements GameSetup {

    /** Startkarten pro Spieler. */
    private static final int HAND_SIZE = 5;

    @Override
    public void distributeInitialHands(Game game) {
        Deck deck = game.getDeck();
        if (deck == null) {
            return;
        }

        // Jeder Spieler bekommt HAND_SIZE Karten.
        for (Player player : game.getPlayers()) {
            for (int i = 0; i < HAND_SIZE; i++) {
                Card card = deck.drawCard();
                if (card != null) {
                    player.getHand().addCard(card);
                }
            }
        }

        // Eine Startkarte aufgedeckt auf den Ablagestapel legen.
        Card start = deck.drawCard();
        if (start != null) {
            // flip() deckt die (verdeckte) Karte auf, bevor sie offen liegt.
            game.getTable().addCard(start.flip());
        }
    }

    @Override
    public Phase getStartPhase() {
        return new MauMauPlayPhase();
    }
}
