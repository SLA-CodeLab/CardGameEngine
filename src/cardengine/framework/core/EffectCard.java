package cardengine.framework.core;

import cardengine.framework.strategy.Effect;

public class EffectCard extends SimpleCard {
    private Effect action;

    public EffectCard(int id, Suit suit, Rank rank, Effect action) {
        super(suit,rank);
        this.action = action;
    }

    public Effect getAction() {
        return action;
    }
}
