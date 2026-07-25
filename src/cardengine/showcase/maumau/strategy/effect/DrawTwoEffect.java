package cardengine.showcase.maumau.strategy.effect;

import cardengine.framework.core.Card;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.factory.Deck;
import cardengine.framework.strategy.Effect;

/**
 * Mau-Mau-Effekt 7: der naechste Spieler muss 2 Karten aus Deck ziehen.
 *
 * @author Stanislav
 */

public class DrawTwoEffect implements Effect {

    private final static int CARDS_TO_DRAW = 2;

    @Override
    public void apply(Game game) {
        // wir ziehen den naechster Spieler
        Player opfer = game.getNextPlayer(game.getActivePlayer());
        Deck deck = game.getDeck();
        // es werden 2 Karten von Deck gezogen
        for (int i = 0; i < CARDS_TO_DRAW; i++) {
            Card card = deck.drawCard();
            // Falls es keine Karten im Deck gibt, dann nichst machen
            // da musste == sein, bloede Fehler, der ich lange Zeit gesucht habe
            if (card == null) {
                return;
            }
            // die gezogene Karten in die Hand des Opfers legen
            opfer.getHand().addCard(card);
        }
    }
}
