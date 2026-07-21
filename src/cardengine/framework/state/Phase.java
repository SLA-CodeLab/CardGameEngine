package cardengine.framework.state;

import cardengine.framework.command.Command;
import cardengine.framework.core.Game;

public interface Phase {
    boolean isValid(Game game, Command cmd);
    void next(Game game);
}
