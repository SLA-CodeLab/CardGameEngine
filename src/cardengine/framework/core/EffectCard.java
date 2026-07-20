package cardengine.framework.core;

import cardengine.framework.strategy.Effect;

public class EffectCard extends SimpleCard {
    private Effect action;

    public EffectCard(int id, Suit suit, Rank rank, Effect action) {
        super(id,suit,rank);
        this.action = action;
    }

    public Effect getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "EffectCard (ID: " + getId() + ")";
    }
}
