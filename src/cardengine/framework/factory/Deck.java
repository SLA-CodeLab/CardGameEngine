package cardengine.framework.factory;

import cardengine.framework.core.Card;

public interface Deck {
    void shuffle();
    void resetDeck();
    Card drawCard();
    boolean isEmpty();
    int getDeckSize();
}
