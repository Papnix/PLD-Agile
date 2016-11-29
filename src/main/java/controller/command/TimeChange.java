package controller.command;

import model.Checkpoint;
import model.Round;

public class TimeChange extends Command {

    public TimeChange(Checkpoint checkpoint) {
        super(checkpoint);
    }

    public TimeChange doCommand(Round round) {
        return this;
    }

    public TimeChange undoCommand(Round round) {
        return this;
    }
}
