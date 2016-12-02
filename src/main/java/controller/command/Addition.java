package controller.command;

import model.Checkpoint;
import model.DeliveryTime;
import model.Round;

public class Addition extends Command {

    public Addition(Round round) {
        super(round);
    }

    public Round doCommand(Checkpoint checkpoint) {
        return this.modifiedRound;
    }
}
