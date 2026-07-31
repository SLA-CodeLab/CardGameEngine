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
    private static final int MAX_PLAYERS = 6;

    @Override
    public void distributeInitialHands(Game game) {
        Deck deck = game.getDeck();
        if (deck == null) return;

        // Jeder Spieler bekommt START_HAND_SIZE Karten.
        // Keine Konsolenausgabe: der Startzustand wird nach start() ueber den
        // GameListener (onStateChanged) dargestellt.
        for (Player p : game.getPlayers()) {
            for (int i = 0; i < START_HAND_SIZE; i++) {
                Card c = deck.drawCard();
                if (c != null) {
                    p.getHand().addCard(c);
                }
            }
        }
    }

    @Override
    public void assignFirstPlayer(Game game) {
        // Kein Sonderkriterium: der erste Spieler in der Liste beginnt.
        if (!game.getPlayers().isEmpty()) {
            game.setActivePlayer(game.getPlayers().get(0));
        }
    }

    @Override
    public Phase getStartPhase(Game game) {
        return new MiniDrawPhase();
    }

    @Override
    public void validateNumberOfPlayers(Game game) {
        if (game.getPlayers().size() > MAX_PLAYERS) {
            throw new IllegalArgumentException("Too many players");
        }
        if (game.getPlayers().size() < 2) {
            throw new IllegalArgumentException("Not enough players");
        }
    }
}
