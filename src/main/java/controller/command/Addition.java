package controller.command;

import model.Checkpoint;
import model.DeliveryTime;
import model.Round;

public class Addition extends Command {

    public Addition(DeliveryTime deliveryTime) {
        super(deliveryTime);
    }

    public Addition doCommand(Round round) {
        return this;
    }

    public Addition undoCommand(Round round) {
        return this;
    }
}
