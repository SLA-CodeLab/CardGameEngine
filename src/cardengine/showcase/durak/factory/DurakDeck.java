package cardengine.showcase.durak.factory;

import cardengine.framework.core.*;

public class DurakDeck extends StandardDeck {
    /**
     * @author Stanislav
     */
    private final static int DURAK_RANK_COUNT = 9;

    private Suit trumpSuit;

    public DurakDeck() {
        Rank[] ranks = Rank.values();// {TWO, THREE .. usw}
        for (Suit suit : Suit.values()) { //{HEARTS,DIAMONDS,CLUBS,SPADES}
            for (int i = getStartIndex(ranks); i < ranks.length; i++) {
                addCard(new Card(suit, ranks[i]));
            }
        }
    }
    /**
     * @author Stanislav
     */
    //um eine Deck zu erzuegen, die mit 6 Startet, eigentlich koennte man auch bei Framework schreiben
    // die loesung von Claude war bei MiniGame --   private static final int MIN_RANK_ORDINAL = Rank.SIX.ordinal();

    private int getStartIndex(Rank[] ranks) {
        int skip = Math.max(0, ranks.length - DURAK_RANK_COUNT);
        return skip;
    }

    public Suit getTrumpSuit() {
        return trumpSuit;
    }
}
