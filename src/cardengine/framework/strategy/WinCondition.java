package cardengine.framework.strategy;

import cardengine.framework.core.Game;
import cardengine.framework.core.Player;

public interface WinCondition {
    boolean isGameOver(Game game);
    Player getWinner(Game game);
}
