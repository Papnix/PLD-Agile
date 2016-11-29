package controller.command;

import model.Checkpoint;
import model.Round;

public abstract class Command {

    private Checkpoint checkpoint;

    public Command(Checkpoint checkpoint) {
        this.checkpoint = checkpoint;
    }

    public abstract Command doCommand(Round round);

    public abstract Command undoCommand(Round round);

}
