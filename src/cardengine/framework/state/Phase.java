package cardengine.framework.state;

import cardengine.framework.core.GameLoop;

public interface Phase {
    void aktionDurchfuehren(GameLoop game);
}
