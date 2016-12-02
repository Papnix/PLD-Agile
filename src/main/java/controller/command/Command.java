package controller.command;

import model.Checkpoint;
import model.DeliveryTime;
import model.Round;

public abstract class Command {

    protected Round previousRound;
    protected Round modifiedRound;


    public Command(Round round) {
        this.previousRound = round;
        this.modifiedRound = null;
    }

    public abstract Round doCommand(Checkpoint checkpoint);

    public Round undoCommand() {
        return this.previousRound;
    }

    public Round redoCommand() {
        return this.modifiedRound;
    }

}
