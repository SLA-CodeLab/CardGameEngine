package cardengine.framework.command;

import java.util.Stack;

public class CommandHistory {
    private Stack<Command> history = new Stack<>();

    public void executeCommand(Command command) {
        if (command != null) {
            command.execute();
            history.push(command);
        }
    }

    public boolean canUndo() {
        return !history.isEmpty();
    }

    public void undo() {
        if (canUndo()) {
            Command command = history.pop();
            command.undo();
        }
    }

    public void clearHistory() {
        history.clear();
    }
}
