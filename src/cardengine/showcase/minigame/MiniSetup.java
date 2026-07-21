package cardengine.showcase.minigame;

import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.core.Card;
import cardengine.framework.factory.Deck;
import cardengine.framework.core.GameSetup;
import cardengine.framework.state.Phase;

/**
 * GENERIERT ZUM TESTEN DES FRAMEWORKS
 *
 * @author Gemini 3.1 Pro High
 */
public class MiniSetup implements GameSetup {

    /** Anzahl der Startkarten pro Spieler (Durak-artige Starthand). */
    private static final int START_HAND_SIZE = 3;

    @Override
    public void distributeInitialHands(Game game) {
        System.out.println("[MiniSetup] Verteile Startkarten...");
        Deck deck = game.getDeck();
        if (deck == null) return;

        // Jeder Spieler bekommt START_HAND_SIZE Karten.
        for (Player p : game.getPlayers()) {
            for (int i = 0; i < START_HAND_SIZE; i++) {
                Card c = deck.drawCard();
                if (c != null) {
                    p.getHand().addCard(c);
                    System.out.println("-> " + p.getName() + " zieht " + c);
                }
            }
        }
    }

    @Override
    public Phase getStartPhase() {
        return new MiniDrawPhase();
    }
}
