package controller.command;

import model.Checkpoint;
import model.DeliveryTime;
import model.Round;

public abstract class Command {

    private DeliveryTime deliveryTime;

    public Command(DeliveryTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public abstract Command doCommand(Round round);

    public abstract Command undoCommand(Round round);

}
