package controller.command;

import model.Checkpoint;
import model.Round;

public class TimeChange extends Command {

    public TimeChange(Round round) {
        super(round);
    }

    public Round doCommand(Checkpoint checkpoint) {
        return this.modifiedRound;
    }
}
