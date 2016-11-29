package controller.command;

import model.Checkpoint;
import model.Round;

public class Addition extends Command {

    public Addition(Checkpoint checkpoint) {
        super(checkpoint);
    }

    public Addition doCommand(Round round) {
        return this;
    }

    public Addition undoCommand(Round round) {
        return this;
    }
}
