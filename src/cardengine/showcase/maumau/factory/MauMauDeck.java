package cardengine.showcase.maumau.factory;

import cardengine.framework.core.Card;
import cardengine.framework.core.Rank;
import cardengine.framework.core.StandardDeck;
import cardengine.framework.core.Suit;

/**
 * GENERIERT von Claude (Opus 4.8).
 *
 * <p>Deck fuer den Mau-Mau-Showcase: das klassische 32er-Blatt (Sieben bis Ass in
 * allen vier Farben). Analog zu {@code DurakDeck} wird das Deck bereits im
 * Konstruktor mit Karten gefuellt; das Mischen uebernimmt spaeter
 * {@link StandardDeck#shuffle()} ueber {@code Game.initGame(...)}.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class MauMauDeck extends StandardDeck {

    /** Kleinster Rang im Mau-Mau-Blatt (7). Alles ab hier kommt ins Deck. */
    private static final int MIN_RANK_ORDINAL = Rank.SEVEN.ordinal();

    public MauMauDeck() {
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                if (rank.ordinal() >= MIN_RANK_ORDINAL) {
                    addCard(new Card(suit, rank));
                }
            }
        }
    }
}
