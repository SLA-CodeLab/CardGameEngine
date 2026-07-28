package cardengine.framework.command;

import cardengine.framework.core.Player;
import cardengine.framework.state.Phase;

import java.util.Stack;

public class CommandHistory {
    private Stack<HistoryEntry> history = new Stack<>();

    public void executeCommand(Command command, Phase phasebefore, Player aktivePlayerBefore) {
        if (command != null) {
            command.execute();
            history.push(new HistoryEntry(command, phasebefore, aktivePlayerBefore));
        }
    }

    public boolean canUndo() {
        return !history.isEmpty();
    }

    /**
     * @author Stanislav
     */
    public HistoryEntry undo() {
        if (canUndo()) {
            HistoryEntry entry = history.pop();
            entry.getCommand().undo();
            return entry;
        }
        return null;
    }

    public void clearHistory() {
        history.clear();
    }
}
