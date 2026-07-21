package cardengine.framework.observer;

import cardengine.framework.command.Command;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;

public interface GameListener {
    void onStateChanged(Game game);
    void onGameOver(Player winner);
    void onInvalidMove(Command cmd);
}
