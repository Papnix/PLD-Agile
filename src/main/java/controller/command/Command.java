package controller.command;

import model.Round;

public abstract class Command {

    protected Round previousRound;
    protected Round modifiedRound;


    public Command(Round round) {
        this.previousRound = round;
        this.modifiedRound = new Round(round);
    }

    public abstract Round doCommand();

    public Round undoCommand() {
        return this.previousRound;
    }

    public Round redoCommand() {
        return this.modifiedRound;
    }

}
