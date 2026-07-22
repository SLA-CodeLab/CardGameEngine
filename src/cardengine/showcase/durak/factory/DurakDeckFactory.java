package cardengine.showcase.durak.factory;


import cardengine.framework.factory.Deck;
import cardengine.framework.factory.DeckFactory;

/**
 * @author Stanislav
 */
public class DurakDeckFactory extends DeckFactory {

    private static final int NUMBER_OF_CARDS = 36;

    @Override
    public Deck createDeck() {
        return new DurakDeck();
    }

    @Override
    public int getDeckSize() {
        return NUMBER_OF_CARDS;
    }
}
