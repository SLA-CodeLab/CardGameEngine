package cardengine.framework.factory;

import cardengine.framework.core.Card;
import cardengine.framework.core.Rank;

public interface Deck {
    void shuffle();
    Card drawCard();
    boolean isEmpty();
    int getDeckSize();
    int getStartIndex(Rank[] ranks);
}
