package cardengine.framework.command;

public interface Command {
    void execute();
    void undo();
}
