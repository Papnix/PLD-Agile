package controller.command;

import model.Checkpoint;
import model.DeliveryTime;
import model.Round;

import java.util.Date;
import java.util.List;

/**
 * @author Nicolas Sorin
 */
public class Deletion extends Command {

    private Checkpoint checkpoint;

    /**
     * Build a Deletion
     *
     * @param round      Round to modify
     * @param checkpoint Checkpoint to delete
     */
    public Deletion(Round round, Checkpoint checkpoint) {
        super(round);
        this.checkpoint = checkpoint;
    }

    /**
     * Delete the DeliveryTime associated to the targeted Checkpoint and adjust waiting and arrival times accordingly.
     *
     * @return The round after the deletion
     */
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