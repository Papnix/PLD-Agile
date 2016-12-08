package controller.command;

import model.Round;

import java.util.Stack;

public class CommandManager {

    private Stack<Command> done;
    private Stack<Command> undone;

    public CommandManager() {

    }

    public Round doCommand(Command command) {
        Round returnValue = command.doCommand();
        done.push(command);
        undone.clear();
        return returnValue;
    }

    public Round undoCommand(Round round) {
        if(!done.empty()) {
            Command command = done.pop();
            Round returnValue = command.undoCommand();
            undone.push(command);
            return returnValue;
        }
        return round;
    }

    public Round redoCommand(Round round) {
        if(!undone.empty()) {
            Command command = undone.pop();
            Round returnValue = command.redoCommand();
            done.push(command);
            return returnValue;
        }
        return round;
    }

    public void clear() {
        done.clear();
        undone.clear();
    }

}
