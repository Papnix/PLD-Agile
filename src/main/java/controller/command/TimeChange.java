package controller.command;

import model.Round;
import controller.pathfinder.Dijkstra;
import model.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimeChange extends Command {

    private Date start;
    private Date end;
    private Map map;
    private Checkpoint checkpoint;

    public TimeChange(Checkpoint checkpoint, Round round, Date start, Date end, Map map) {
        super(round);
        this.start = start;
        this.end = end;
        this.map = map;
        this.checkpoint = checkpoint;
    }


    public Round doCommand() {

        roundTimeorders:
        for (List<DeliveryTime> deliveryTimes : this.modifiedRound.getRoundTimeOrders()) {
            // Indices des DeliveryTiems les plus proches du début et de la fin de la nouvelle plage (mais non compris)
            // Permet de définir un intervalle sur lequel placer la livraison si la nouvelle plage demande un changement
            int startIndex = -1, endIndex = -1, index = -1;

            if(deliveryTimes.size() == 0) {
                break;
            }

            for (int i = 0; i < deliveryTimes.size(); i++) {
                DeliveryTime currentDeliveryTime = deliveryTimes.get(i);

                if (currentDeliveryTime.getCheckpoint().getId() == this.checkpoint.getId()) {
                    index = i;
                    // If a simple solution exists, apply it and go to next roundTimeOrder
                    if (checkForSimpleSolution(currentDeliveryTime, deliveryTimes.get(i + 1))) {
                        continue roundTimeorders;
                    }
                } else if (currentDeliveryTime.getDepartureTime() != null && currentDeliveryTime.getDepartureTime().before(this.start)) {
                    startIndex = i;
                } else if (currentDeliveryTime.getArrivalTime() != null && currentDeliveryTime.getArrivalTime().after(this.end)) {
                    endIndex = endIndex == -1 ? i : endIndex;
                }
            }

            // At this point, the deliveryTime needs to be moved
            DeliveryTime deliveryTime = deliveryTimes.get(index);
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
                Date departureTime = new Date(arrivalTime.getTime() + waitingTime + this.checkpoint.getDuration());

                Date nextPointArrivalTime = new Date(departureTime.getTime() + timeFrom);
                // If not possible, try another position
                if (nextPointArrivalTime.after(new Date(nextDelivery.getArrivalTime().getTime() + nextDelivery.getWaitingTime()))) {
                    continue;
                }

                // At this point, we have found a suitable position for the delivery

                deliveryTime.getCheckpoint().setTimeRangeStart(this.start);
                deliveryTime.getCheckpoint().setTimeRangeEnd(this.end);

                nextDelivery.setWaitingTime(nextDelivery.getWaitingTime() - (nextPointArrivalTime.getTime() - nextDelivery.getArrivalTime().getTime()));
                nextDelivery.setArrivalTime(nextPointArrivalTime);

                deliveryTime.setArrivalTime(arrivalTime);
                deliveryTime.setWaitingTime(waitingTime);
                deliveryTime.setDepartureTime(departureTime);

                deliveryTimes.remove(index);
                deliveryTimes.add(j + 1, deliveryTime);

                break;
            }
        }
        return this.modifiedRound;
    }

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
