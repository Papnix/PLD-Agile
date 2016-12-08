package controller.command;

import model.Checkpoint;
import model.DeliveryTime;
import model.Round;

import java.util.Date;
import java.util.List;

public class Deletion extends Command {

    private Checkpoint checkpoint;

    public Deletion(Round round, Checkpoint checkpoint) {
        super(round);
        this.checkpoint = checkpoint;
    }

    public Round doCommand() {
        for (List<DeliveryTime> deliveryTimes : this.modifiedRound.getRoundTimeOrders()) {
            for (int i = 0; i < deliveryTimes.size(); i++) {
                DeliveryTime deliveryTime = deliveryTimes.get(i);
                if (deliveryTime.getCheckpoint().getId() == this.checkpoint.getId()) {
                    // Add waiting time to the next deliveryTime to compensate for the removal of a delivery
                    DeliveryTime nextDeliveryTime = deliveryTimes.get(i + 1);
                    long waitingTime = deliveryTime.getWaitingTime() + this.checkpoint.getDuration();
                    nextDeliveryTime.setArrivalTime(new Date(nextDeliveryTime.getArrivalTime().getTime() - waitingTime));
                    nextDeliveryTime.setWaitingTime(nextDeliveryTime.getWaitingTime() + waitingTime);
                    // Remove the targeted delivery from the list
                    deliveryTimes.remove(i);

                }
            }
        }
        return this.modifiedRound;
    }
}