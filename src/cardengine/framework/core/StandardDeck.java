package cardengine.framework.core;

import cardengine.framework.factory.Deck;
import java.util.Collections;

public abstract class StandardDeck extends CardCollection implements Deck {
    
    @Override
    public void shuffle() {
        Collections.shuffle(cards);
    }

    @Override
    public void resetDeck() {
        cards.clear();
    }

    /**
     *
     * @author Lukas
     * @return oberste Karte des Decks
     */
    @Override
    public Card drawCard() {
        if (cards.isEmpty()) return null;
        return cards.remove(cards.size() - 1);
    }

    @Override
    public int getDeckSize() {
        return cards.size();
    }
}
