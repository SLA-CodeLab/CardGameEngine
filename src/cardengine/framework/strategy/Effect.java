package cardengine.framework.strategy;

import cardengine.framework.core.GameLoop;

public interface Effect {
    void apply(GameLoop game);
}
