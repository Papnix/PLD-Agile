package controller.command;

import model.Round;

/**
 * @author Nicolas Sorin
 */
public abstract class Command {

    /**
     * Round before modifications
     */
    protected Round previousRound;
    /**
     * Round after modifications
     */
    protected Round modifiedRound;

    /**
     * Build a Command by making a copy of the initial round that will be modified.
     *
     * @param round Initial round
     */
    public Command(Round round) {
        this.previousRound = round;
        this.modifiedRound = new Round(round);
    }

    /**
     * Execute the command
     *
     * @return The round after the command execution
     */
    public abstract Round doCommand();

    /**
     * Undo the command and restore previous state
     *
     * @return The initial round
     */
    public Round undoCommand() {
        return this.previousRound;
    }

    /**
     * Redo a command that had been undone
     *
     * @return The round after the command execution
     */
    public Round redoCommand() {
        return this.modifiedRound;
    }

}
