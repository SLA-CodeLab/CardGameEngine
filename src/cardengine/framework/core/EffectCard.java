package cardengine.framework.core;

import cardengine.framework.strategy.Effect;

public class EffectCard extends Card {
    private Effect action;

    public EffectCard(int id, Effect action) {
        super(id);
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
