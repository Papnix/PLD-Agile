package controller.command;

import model.Checkpoint;
import model.Round;

import java.util.Stack;

public class CommandManager {

    private Stack<Command> done;
    private Stack<Command> undone;

    public CommandManager() {

    }

    /**
     * Execute a given command. In case of success, the command is added to the done LIFO to be undone if needed. Clear
     * the undone LIFO to avoid conflicts in case of redo.
     * @param command Command to execute
     * @return The round after the command has been executed, or null if the command failed
     */
    public Round doCommand(Command command) {
        Round returnValue = command.doCommand();
        if(!returnValue.getRoundTimeOrders().isEmpty()) {
            done.push(command);
            undone.clear();
            return returnValue;
        }
        return null;
    }

    /**
     * Undo a previous command and add it to the undone LIFO to be redone if needed.
     * @return The round after the command has been undone, or null if there is no command to undo
     */
    public Round undoCommand() {
        if (!done.empty()) {
            Command command = done.pop();
            Round returnValue = command.undoCommand();
            undone.push(command);
            return returnValue;
        }
        return null;
    }

    /**
     * Redo a previously undone command and add it back to the done LIFO to be undone again if needed.
     * @return The round after the command has been redone, or null if there is no command to redo
     */
    public Round redoCommand() {
        if (!undone.empty()) {
            Command command = undone.pop();
            Round returnValue = command.redoCommand();
            done.push(command);
            return returnValue;
        }
        return null;
    }

    /**
     * Clear the done and undone LIFOs.
     */
    public void clear() {
        done.clear();
        undone.clear();
    }

}
