package cardengine.showcase.minigame;

import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.core.Card;
import cardengine.framework.factory.Deck;
import cardengine.framework.core.GameSetup;
import cardengine.framework.state.Phase;

/**
 * GENERIERT ZUM TESTEN DES FRAMEWORKS
 */
public class MiniSetup implements GameSetup {

    @Override
    public void distributeInitialHands(Game game) {
        System.out.println("[MiniSetup] Verteile Startkarten...");
        Deck deck = game.getDeck();
        if (deck == null) return;
        
        // Jeder Spieler bekommt genau 1 Karte
        for (Player p : game.getPlayers()) {
            Card c = deck.drawCard();
            if (c != null) {
                p.getHand().addCard(c);
                System.out.println("-> " + p.getName() + " zieht " + c.toString());
            }
        }
    }

    @Override
    public Phase getStartPhase() {
        return new MiniDrawPhase();
    }
}
