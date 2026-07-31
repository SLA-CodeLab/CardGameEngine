package cardengine.showcase.minigame;

import cardengine.framework.core.Card;
import cardengine.framework.core.Rank;
import cardengine.framework.core.StandardDeck;
import cardengine.framework.core.Suit;
import cardengine.framework.factory.Deck;
import cardengine.framework.factory.DeckFactory;

/**
 * Factory des Minigames: erzeugt ein Durak-artiges 36-Karten-Deck.
 *
 * <p>Statt namenloser Platzhalterkarten werden jetzt echte {@link Card}s mit
 * {@link Suit} und {@link Rank} erzeugt. Dadurch lassen sich die Karten in der
 * GUI als richtige Kartenblaetter (z.&nbsp;B. ♠4) darstellen.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class MiniFactory extends DeckFactory {

    private static final int NUMBER_OF_CARDS = 36;

    @Override
    public Deck createDeck() {
        int rankCount = NUMBER_OF_CARDS / Suit.values().length;

        // StandardDeck ist abstrakt, daher anonyme Subklasse.
        StandardDeck deck = new StandardDeck() {
            @Override
            public int getStartIndex(Rank[] ranks) {
                return Math.max(0, ranks.length - rankCount);
            }
        };

        Rank[] ranks = Rank.values();
        int startIndex = deck.getStartIndex(ranks);

        for (Suit suit : Suit.values()) {
            for (int i = startIndex; i < ranks.length; i++) {
                deck.addCard(new Card(suit, ranks[i]));
            }
        }

        return deck;
    }

    @Override
    public int getDeckSize() {
        return NUMBER_OF_CARDS;
    }
}