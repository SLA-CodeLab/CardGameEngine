package cardengine.showcase.durak.factory;

import cardengine.framework.core.*;

public class DurakDeck extends StandardDeck {
    private final static int DURAK_RANK_COUNT = 9;
    private Suit trumpSuit;

    /**
     * Da erst nach dem Mischen klar ist welche Karte ganz unten im Deck ist muss das überschrieben werden und dann kann man das hier im Deck speichern
     *
     * @author Lukas
     */
    @Override
    public void shuffle() {
        super.shuffle();
        revealTrumpCard();
    }

    /**
     * Hilfsmethode zum speichern der Suit in Klassenvariable. Außerdem wird Karte visible gesetzt für GUI
     * Karte wird nicht aus Deck entfernt weil wird ja letztes gezogen
     *
     * @author Lukas
     */
    private void revealTrumpCard() {
        if (cards.isEmpty()) return;
        Card trumpCard = cards.get(0);
        trumpCard.flip();
        trumpSuit = trumpCard.getSuit();
    }

    /**
     * @author Stanislav
     */
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
    //todo ich finde auch man sollte das in Framework machen // von Lukas

    private int getStartIndex(Rank[] ranks) {
        int skip = Math.max(0, ranks.length - DURAK_RANK_COUNT);
        return skip;
    }

    public Suit getTrumpSuit() {
        return trumpSuit;
    }
}
