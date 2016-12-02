package controller.command;

import model.Checkpoint;
import model.DeliveryTime;
import model.Round;

public class Deletion extends Command {

    public Deletion(DeliveryTime deliveryTime) {
        super(deliveryTime);
    }

    public Deletion doCommand(Round round) {
        DeliveryTime previous = null;
        DeliveryTime next = null;


        return this;
    }

    public Deletion undoCommand(Round round) {
        return this;
    }

}
