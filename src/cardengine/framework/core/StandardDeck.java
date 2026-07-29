package cardengine.framework.core;

import cardengine.framework.factory.Deck;
import java.util.Collections;

public abstract class StandardDeck extends CardCollection implements Deck {
    
    @Override
    public void shuffle() {
        Collections.shuffle(cards);
    }
    /**
     * Um oberste Karte vom Deck zu nehmen. Habe mit size - 1 damit migration zu Eclipse kein Problem wird
     * @return oberste Karte des Decks
     * @author Lukas
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
