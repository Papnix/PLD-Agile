package controller.command;

import model.Checkpoint;
import model.DeliveryTime;
import model.Round;

public class TimeChange extends Command {

    public TimeChange(DeliveryTime deliveryTime) {
        super(deliveryTime);
    }

    public TimeChange doCommand(Round round) {
        return this;
    }

    public TimeChange undoCommand(Round round) {
        return this;
    }
}
