package controller.command;

import model.Round;

import java.util.Stack;

public class CommandManager {

    private Stack<Command> done;
    private Stack<Command> undone;

    public CommandManager() {

    }

    public void doCommand(Command command, Round round) {
        done.push(command.doCommand(round));
        undone.clear();
    }

    public void undoCommand(Round round) {
        if(!done.empty()) {
            undone.push(done.pop().undoCommand(round));
        }
    }

    public void redoCommand(Round round) {
        if(!undone.empty()) {
            done.push(undone.pop().doCommand(round));
        }
    }

    public void clear() {
        done.clear();
        undone.clear();
    }

}
