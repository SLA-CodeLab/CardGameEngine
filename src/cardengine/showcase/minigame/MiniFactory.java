package cardengine.showcase.minigame;

import cardengine.framework.core.Rank;
import cardengine.framework.core.SimpleCard;
import cardengine.framework.core.StandardDeck;
import cardengine.framework.core.Suit;
import cardengine.framework.factory.Deck;
import cardengine.framework.factory.DeckFactory;

/**
 * Factory des Minigames: erzeugt ein Durak-artiges 36-Karten-Deck.
 *
 * <p>Statt namenloser Platzhalterkarten werden jetzt echte {@link SimpleCard}s mit
 * {@link Suit} und {@link Rank} erzeugt (6 bis Ass in allen vier Farben). Dadurch
 * lassen sich die Karten in der GUI als richtige Kartenblaetter (z.&nbsp;B. ♠4)
 * darstellen – es wird keine Karten-ID mehr benoetigt.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class MiniFactory extends DeckFactory {

    /** Kleinster Rang im Durak-Deck (6). Alles ab hier kommt ins Deck. */
    private static final int MIN_RANK_ORDINAL = Rank.SIX.ordinal();

    @Override
    public Deck createDeck() {
        // StandardDeck ist abstrakt (ohne abstrakte Methoden), daher anonyme Subklasse.
        StandardDeck deck = new StandardDeck() {
        };

        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                if (rank.ordinal() >= MIN_RANK_ORDINAL) {
                    deck.addCard(new SimpleCard(suit, rank));
                }
            }
        }
        return deck;
    }

    @Override
    public int getDeckSize() {
        return (Rank.values().length - MIN_RANK_ORDINAL) * Suit.values().length;
    }
}
