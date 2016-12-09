package controller.command;

import model.Round;
import controller.pathfinder.Dijkstra;
import model.*;

import java.util.Date;
import java.util.List;

/**
 * @author Nicolas Sorin
 */
public class TimeChange extends Command {

    /**
     * Starting date for the new time range
     */
    private Date start;
    /**
     * Ending date for the new time range
     */
    private Date end;
    /**
     * Map used to recalculate paths
     */
    private Map map;
    /**
     * Checkpoint to modify
     */
    private Checkpoint checkpoint;

    /**
     * Build a TimeChange
     *
     * @param checkpoint Checkpoint to modify
     * @param round      Round to modify
     * @param start      Starting date for the new time range
     * @param end        Ending date for the new time range
     * @param map        Map used to recalculate paths
     */
    public TimeChange(Checkpoint checkpoint, Round round, Date start, Date end, Map map) {
        super(round);
        this.start = start;
        this.end = end;
        this.map = map;
        this.checkpoint = checkpoint;
    }

    /**
     * Change the time range of the Checkpoint and move it if needed.
     *
     * @return The round after the modification
     */
    public Round doCommand() {

        int k = 0;
        int size = this.modifiedRound.getRoundTimeOrders().size();

        roundTimeorders:
        while (k < size) {

            List<DeliveryTime> deliveryTimes = this.modifiedRound.getRoundTimeOrders().get(k);

            // Indices des DeliveryTiems les plus proches du début et de la fin de la nouvelle plage (mais non compris)
            // Permet de définir un intervalle sur lequel placer la livraison si la nouvelle plage demande un changement
            int startIndex = 0, endIndex = deliveryTimes.size() - 1, index = -1;

            for (int i = 0; i < deliveryTimes.size(); i++) {
                DeliveryTime currentDeliveryTime = deliveryTimes.get(i);

                if (currentDeliveryTime.getCheckpoint().getId() == this.checkpoint.getId()) {
                    index = i;
                    // If a simple solution exists, apply it and go to next roundTimeOrder
                    if (checkForSimpleSolution(currentDeliveryTime, deliveryTimes.get(i + 1))) {
                        k++;
                        continue roundTimeorders;
                    }
                } else if (currentDeliveryTime.getDepartureTime() != null && this.start != null && currentDeliveryTime.getDepartureTime().before(this.start)) {
                    startIndex = i;
                } else if (currentDeliveryTime.getArrivalTime() != null && this.end != null && currentDeliveryTime.getArrivalTime().after(this.end)) {
                    endIndex = endIndex > i ? i : endIndex;
                }
            }

            // At this point, the deliveryTime needs to be moved
            DeliveryTime deliveryTime = deliveryTimes.get(index);
            boolean success = false;

            for (int j = startIndex; j < endIndex; j++) {

                DeliveryTime previousDelivery = deliveryTimes.get(j);
                DeliveryTime nextDelivery = deliveryTimes.get(j + 1);

                Dijkstra dj = new Dijkstra(this.map);
                dj.execute(previousDelivery.getCheckpoint().getId());
                long timeTo = dj.getTargetPathCost(this.checkpoint.getId());
                dj.execute(this.checkpoint.getId());
                long timeFrom = dj.getTargetPathCost(nextDelivery.getCheckpoint().getId());

                Date arrivalTime = new Date(previousDelivery.getDepartureTime().getTime() + timeTo);
                // If not possible, try another position
                if (arrivalTime.after(this.end)) {
                    continue;
                }

                long waitingTime = arrivalTime.before(this.start) ? (this.start.getTime() - arrivalTime.getTime()) : 0;
                Date departureTime = new Date(arrivalTime.getTime() + waitingTime + this.checkpoint.getDuration() * 1000);
                Date nextPointArrivalTime = new Date(departureTime.getTime() + timeFrom);
                // If not possible, try another position
                if (nextPointArrivalTime.after(new Date(nextDelivery.getArrivalTime().getTime() + nextDelivery.getWaitingTime()))) {
                    continue;
                }

                success = true;
                // At this point, we have found a suitable position for the delivery

                deliveryTime.getCheckpoint().setTimeRangeStart(this.start);
                deliveryTime.getCheckpoint().setTimeRangeEnd(this.end);

                nextDelivery.setWaitingTime(nextDelivery.getWaitingTime() - (nextPointArrivalTime.getTime() - nextDelivery.getArrivalTime().getTime()));
                nextDelivery.setArrivalTime(nextPointArrivalTime);

                deliveryTime.setArrivalTime(arrivalTime);
                deliveryTime.setWaitingTime(waitingTime);
                deliveryTime.setDepartureTime(departureTime);

                // Recalculate route for the DeliveryTimes that used to be before and after
                DeliveryTime previousDeliveryTime = deliveryTimes.get(index - 1);
                DeliveryTime nextDeliveryTime = deliveryTimes.get(index + 1);

                dj.execute(previousDeliveryTime.getCheckpoint().getId());
                long timeToNext = dj.getTargetPathCost(nextDeliveryTime.getCheckpoint().getId());
                Date nextArrivalTime = new Date(previousDeliveryTime.getDepartureTime().getTime() + timeToNext);
                long diffWaitingTime = nextDeliveryTime.getArrivalTime().getTime() - nextArrivalTime.getTime();

                nextDeliveryTime.setArrivalTime(nextArrivalTime);
                nextDeliveryTime.setWaitingTime(nextDeliveryTime.getWaitingTime() + diffWaitingTime);

                deliveryTimes.remove(index);
                deliveryTimes.add(j + 1, deliveryTime);

                break;
            }

            if (success) {
                k++;
            } else {
                this.modifiedRound.getRoundTimeOrders().remove(k);
                size--;
            }
        }

        this.modifiedRound.rebuildRoute(map);
        return this.modifiedRound;
    }

    /**
     * Check if the modification can be applied without moving the targeted DeliveryTime, and apply it if it can
     *
     * @param deliveryTime     The DeliveryTime that may be modified
     * @param nextDeliveryTime The DeliveryTime right after the one that may be modified
     * @return True if there is a quick solution that doesn't require complex calculations, false otherwise
     */
    private boolean checkForSimpleSolution(DeliveryTime deliveryTime, DeliveryTime nextDeliveryTime) {
        // If the new time range is null, no need to change anything
        if (this.start == null && this.end == null) {
            deliveryTime.getCheckpoint().setTimeRangeStart(null);
            deliveryTime.getCheckpoint().setTimeRangeEnd(null);
            return true;
        }

        // If the current arrival times is already in the new time range, no need to move the deliveryTime
        if (deliveryTime.getArrivalTime().after(this.start) && deliveryTime.getArrivalTime().before(this.end)) {
            // If there was any waiting time on the checkpoint, it is not needed anymore and is transferred to the next
            if (deliveryTime.getWaitingTime() > 0) {
                long waitingTime = deliveryTime.getWaitingTime();
                deliveryTime.setWaitingTime(0);
                deliveryTime.setDepartureTime(new Date(deliveryTime.getDepartureTime().getTime() - waitingTime));
                nextDeliveryTime.setWaitingTime(nextDeliveryTime.getWaitingTime() + waitingTime);
                nextDeliveryTime.setArrivalTime(new Date(nextDeliveryTime.getArrivalTime().getTime() - waitingTime));
            }
            deliveryTime.getCheckpoint().setTimeRangeStart(this.start);
            deliveryTime.getCheckpoint().setTimeRangeEnd(this.end);
            return true;
        }

        return false;
    }
}
