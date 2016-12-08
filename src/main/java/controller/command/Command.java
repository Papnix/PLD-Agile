package controller.command;

import model.Checkpoint;
import model.Round;

public abstract class Command {

    protected Round previousRound;
    protected Round modifiedRound;


    public Command(Round round) {
        this.previousRound = round;
        this.modifiedRound = new Round(round);
    }

    /**
     * Execute the command
     * @return The round after the command execution
     */
    public abstract Round doCommand();

    /**
     * Undo the command and restore previous state
     * @return The initial round
     */
    public Round undoCommand() {
        return this.previousRound;
    }

    /**
     * Redo a command that had been undone
     * @return The round after the command execution
     */
    public Round redoCommand() {
        return this.modifiedRound;
    }

}
