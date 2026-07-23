package cardengine.showcase.durak.state;

import cardengine.framework.command.Command;
import cardengine.framework.core.Game;
import cardengine.framework.state.Phase;

public class AttackPhase implements Phase {
    @Override
    public boolean isValid(Game game, Command cmd) {
        //todo
        return false;
    }

    @Override
    public Phase next(Game game) {
        //todo
        return null;
    }
}
