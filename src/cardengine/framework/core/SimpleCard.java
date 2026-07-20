package cardengine.framework.core;

import cardengine.framework.strategy.Effect;

public class SimpleCard extends Card {
    private Suit suit;
    private Rank rank;
    private Effect effect;

    public SimpleCard(int id, Suit suit, Rank rank) {
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

    public Effect getEffect() {
        return effect;
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}
