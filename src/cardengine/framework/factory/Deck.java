package cardengine.framework.factory;

import cardengine.framework.core.Card;

public interface Deck {
    void shuffle();
    void resetDeck(); //todo ich verstehe nicht wann man das brauchen könnte (von Lukas)
    Card drawCard();
    boolean isEmpty();
    int getDeckSize();
}
