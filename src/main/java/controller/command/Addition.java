package controller.command;

import model.*;
import view.ErrorDisplayer;

import java.util.Date;
import java.util.List;

import controller.pathfinder.Dijkstra;

/**
 * @author Hugo Humbert
 */
public class Addition extends Command {

    /**
     * Map used to recalculate paths
     */
    private Map map;

    /**
     * Checkpoint to add
     */
    private Checkpoint checkpoint;

    /**
     * Build an Addition
     *
     * @param round      Round to modify
     * @param map        Map used to recalculate paths
     * @param checkpoint Checkpoint to add
     */
    public Addition(Round round, Map map, Checkpoint checkpoint) {
        super(round);
        this.map = map;
        this.checkpoint = checkpoint;
    }

    /**
     * Add the Checkpoint in the round if possible.
     *
     * @return The round after the addition
     */
    public Round doCommand() {
        int k = 0;
        int size = this.modifiedRound.getRoundTimeOrders().size();

        
        while (k < size) {

            List<DeliveryTime> deliveryTimes = this.modifiedRound.getRoundTimeOrders().get(k);

            // Indices des DeliveryTiems les plus proches du début et de la fin de la nouvelle plage (mais non compris)
            // Permet de définir un intervalle sur lequel placer la livraison si la nouvelle plage demande un changement
            int startIndex = 0, endIndex = deliveryTimes.size() - 1;

            for (int i = 0; i < deliveryTimes.size(); i++) {
                DeliveryTime currentDeliveryTime = deliveryTimes.get(i);

                if (currentDeliveryTime.getDepartureTime() != null && this.checkpoint.getTimeRangeStart() != null && currentDeliveryTime.getDepartureTime().before(this.checkpoint.getTimeRangeStart())) {
                    startIndex = i;
                } else if (currentDeliveryTime.getArrivalTime() != null  && this.checkpoint.getTimeRangeEnd() != null && currentDeliveryTime.getArrivalTime().after(this.checkpoint.getTimeRangeEnd())) {
                    endIndex = endIndex > i ? i : endIndex;
                }
            }

            boolean success = false;

            for (int j = startIndex; j < endIndex; j++) {

                DeliveryTime previousDelivery = deliveryTimes.get(j);
                DeliveryTime nextDelivery = deliveryTimes.get(j + 1);

                Dijkstra dj = new Dijkstra(this.map);
                dj.execute(previousDelivery.getCheckpoint().getId());
                long timeTo;
                try { 
                	timeTo = dj.getTargetPathCost(this.checkpoint.getId());
                }
                catch (Exception e )
                {
                	ErrorDisplayer.displayWarningMessageBox("Checkpoint non valide");
                	return null;
                }
                 dj.execute(this.checkpoint.getId());
                long timeFrom = dj.getTargetPathCost(nextDelivery.getCheckpoint().getId());

                Date arrivalTime = new Date(previousDelivery.getDepartureTime().getTime() + timeTo);
                // If not possible, try another position
                if (this.checkpoint.getTimeRangeEnd() != null && arrivalTime.after(this.checkpoint.getTimeRangeEnd())) {
                    continue;
                }

                long waitingTime = this.checkpoint.getTimeRangeStart() != null && arrivalTime.before(this.checkpoint.getTimeRangeStart()) ?
                        (this.checkpoint.getTimeRangeStart().getTime() - arrivalTime.getTime()) : 0;
                Date departureTime = new Date(arrivalTime.getTime() + waitingTime + this.checkpoint.getDuration() * 1000);
                Date nextPointArrivalTime = new Date(departureTime.getTime() + timeFrom);
                // If not possible, try another position
                if (nextPointArrivalTime.after(new Date(nextDelivery.getArrivalTime().getTime() + nextDelivery.getWaitingTime()))) {
                    continue;
                }

                success = true;
                // At this point, we have found a suitable position for the delivery

                nextDelivery.setWaitingTime(nextDelivery.getWaitingTime() - (nextPointArrivalTime.getTime() - nextDelivery.getArrivalTime().getTime()));
                nextDelivery.setArrivalTime(nextPointArrivalTime);

                DeliveryTime deliveryTime = new DeliveryTime(this.checkpoint, arrivalTime, departureTime, waitingTime);

                this.modifiedRound.getRequest().addCheckpoint(this.checkpoint);
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

        this.modifiedRound.buildIndex();
        this.modifiedRound.computePaths(map);
        this.modifiedRound.rebuildRoute(map);
        return this.modifiedRound;

    }
}
