package cardengine.framework.strategy;

import cardengine.framework.core.GameLoop;
import cardengine.framework.core.Player;

public interface WinCondition {
    boolean isGameOver(GameLoop game);
    Player getWinner(GameLoop game);
}
