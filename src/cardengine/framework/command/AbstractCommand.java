package cardengine.framework.command;

import cardengine.framework.core.Player;

public abstract class AbstractCommand implements Command {
    protected Player player;

    public AbstractCommand(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
