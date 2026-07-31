package cardengine.framework.command;

import cardengine.framework.core.Player;
import cardengine.framework.state.Phase;
/**
 * @author Stanislav
 */
public final class HistoryEntry {
    private final Command command;
    private final Phase phaseBefore;
    private final Player activePlayerBefore;

    HistoryEntry(Command command, Phase phaseBefore, Player activePlayerBefore) {
        this.command = command;
        this.phaseBefore = phaseBefore;
        this.activePlayerBefore = activePlayerBefore;
    }
    public Command getCommand() {
        return command;
    }
    public Phase getPhaseBefore() {
        return phaseBefore;
    }
    public Player getActivePlayerBefore() {
        return activePlayerBefore;
    }
}