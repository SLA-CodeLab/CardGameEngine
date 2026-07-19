package cardengine.framework.core;

public class CardWithoutAction extends Card {
    private Suit suit;
    private Rank rank;

    public CardWithoutAction(int id, Suit suit, Rank rank) {
        super(id);
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}
