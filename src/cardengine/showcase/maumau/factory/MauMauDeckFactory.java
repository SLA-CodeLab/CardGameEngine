package cardengine.showcase.maumau.factory;

import cardengine.framework.factory.Deck;
import cardengine.framework.factory.DeckFactory;

/**
 * GENERIERT von Claude (Opus 4.8).
 *
 * <p>Konkrete Factory (Factory-Method-Pattern) fuer das Mau-Mau-Deck. Erzeugt ein
 * {@link MauMauDeck} mit 32 Karten.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class MauMauDeckFactory extends DeckFactory {

    private static final int NUMBER_OF_CARDS = 32;

    @Override
    public Deck createDeck() {
        return new MauMauDeck();
    }

    @Override
    public int getDeckSize() {
        return NUMBER_OF_CARDS;
    }
}
