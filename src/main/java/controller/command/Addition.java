package controller.command;

import model.Checkpoint;
import model.Round;

public class Addition extends Command {

    public Addition(Round round) {
        super(round);
    }

    public Round doCommand() {
        return this.modifiedRound;
    }
}
