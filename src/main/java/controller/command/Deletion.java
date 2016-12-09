package controller.command;

import controller.pathfinder.Dijkstra;
import model.Checkpoint;
import model.DeliveryTime;
import model.Map;
import model.Round;

import java.util.Date;
import java.util.List;

/**
 * @author Nicolas Sorin
 */
public class Deletion extends Command {

    /**
     * Checkpoint to delete
     */
    private Checkpoint checkpoint;

    /**
     * Map used to calculate the new path
     */
    private Map map;

    /**
     * Build a Deletion
     *
     * @param round      Round to modify
     * @param checkpoint Checkpoint to delete
     */
    public Deletion(Round round, Checkpoint checkpoint, Map map) {
        super(round);
        this.checkpoint = checkpoint;
        this.map = map;
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

                    // Recalculate root not necessarily reaching the deleted checkpoint
                    DeliveryTime previousDeliveryTime = deliveryTimes.get(i-1);
                    DeliveryTime nextDeliveryTime = deliveryTimes.get(i + 1);

                    Dijkstra dj = new Dijkstra(this.map);
                    dj.execute(previousDeliveryTime.getCheckpoint().getId());
                    long timeToNext = dj.getTargetPathCost(nextDeliveryTime.getCheckpoint().getId());
                    Date nextArrivalTime = new Date(previousDeliveryTime.getDepartureTime().getTime() + timeToNext);
                    long diffWaitingTime = nextDeliveryTime.getArrivalTime().getTime() - nextArrivalTime.getTime();

                    nextDeliveryTime.setArrivalTime(nextArrivalTime);
                    nextDeliveryTime.setWaitingTime(nextDeliveryTime.getWaitingTime() + diffWaitingTime);
                    // Remove the targeted delivery from the list
                    deliveryTimes.remove(i);
                }
            }
        }

        this.modifiedRound.rebuildRoute(map);
        return this.modifiedRound;
    }
}