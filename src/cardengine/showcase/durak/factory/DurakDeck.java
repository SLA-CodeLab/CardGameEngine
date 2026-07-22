package cardengine.showcase.durak.factory;

import cardengine.framework.core.StandardDeck;
import cardengine.framework.core.Suit;

public class DurakDeck extends StandardDeck {
    /**
     * @author Stanislav
     */
    private Suit trumpSuit;

    public DurakDeck() {
        //ka
    }


    public Suit getTrumpSuit() {
        return trumpSuit;
    }
}
