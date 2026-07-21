package cardengine.framework.core;


public class SimpleCard extends Card {
    private Suit suit;
    private Rank rank;

    public SimpleCard(Suit suit, Rank rank) {
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
