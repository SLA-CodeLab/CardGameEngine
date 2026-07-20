package cardengine.showcase.minigame;

import cardengine.framework.core.Card;
import cardengine.framework.core.StandardDeck;
import cardengine.framework.factory.Deck;
import cardengine.framework.factory.DeckFactory;
import cardengine.framework.core.CardVisibility;

/**
 * GENERIERT ZUM TESTEN DES FRAMEWORKS
 */
public class MiniFactory extends DeckFactory {

    @Override
    public Deck createDeck() {
        StandardDeck deck = new StandardDeck() {
            // Anonyme Klasse für unser Mini-Deck
        };
        
        // Füge 10 einfache Karten hinzu
        for (int i = 1; i <= 10; i++) {
            final int id = i;
            deck.addCard(new Card() {
                @Override
                public String toString() {
                    return "Karte " + id;
                }
            });
        }
        return deck;
    }

    @Override
    public int getDeckSize() {
        return 10;
    }
}
