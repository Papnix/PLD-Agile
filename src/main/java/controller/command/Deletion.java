package controller.command;

import model.Checkpoint;
import model.Round;

public class Deletion extends Command {

    public Deletion(Checkpoint checkpoint) {
        super(checkpoint);
    }

    public Deletion doCommand(Round round) {
        return this;
    }

    public Deletion undoCommand(Round round) {
        return this;
    }

}
