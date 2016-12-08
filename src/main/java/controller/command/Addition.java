package controller.command;

import model.*;

import java.util.Date;
import java.util.List;

import controller.pathfinder.Dijkstra;

public class Addition extends Command {
    private Map map;
    private Checkpoint checkpoint;

    public Addition(Round round, Map map, Checkpoint checkpoint) {
        super(round);
        this.map = map;
        this.checkpoint = checkpoint;
    }

    public Round doCommand() {
        // Si la plage horaire a été retirée, aucun changement n'est nécessaire

        boolean noTimeRange = this.checkpoint.getTimeRangeStart() == null && this.checkpoint.getTimeRangeEnd() == null;

        int k = 0;
        int size = this.modifiedRound.getRoundTimeOrders().size();

        while (k < size) {

            List<DeliveryTime> deliveryTimes = this.modifiedRound.getRoundTimeOrders().get(k);

            int startIndex = -1, endIndex = -1;

            DeliveryTime deliveryTime = null;
            if (noTimeRange) {
                startIndex = 0;
                endIndex = deliveryTimes.size() - 1;
            } else {
                for (int i = 0; i < deliveryTimes.size(); i++) {

                    deliveryTime = deliveryTimes.get(i);

                    if (deliveryTime.getDepartureTime() != null && deliveryTime.getDepartureTime().before(this.checkpoint.getTimeRangeStart())) {

                        startIndex = i;

                    } else if (deliveryTime.getArrivalTime() != null && deliveryTime.getArrivalTime().after(this.checkpoint.getTimeRangeEnd())) {

                        endIndex = endIndex == -1 ? i : endIndex;

                    }

                }
            }

            boolean success = false;

            // On cherche s'il est possible d'insérer la livraison quelque part
            for (int j = startIndex; j < endIndex; j++) {


                DeliveryTime previousDelivery = deliveryTimes.get(j);

                DeliveryTime nextDelivery = deliveryTimes.get(j + 1);


                Dijkstra dj = new Dijkstra(this.map);

                dj.execute(previousDelivery.getCheckpoint().getId());

                long timeTo = dj.getTargetPathCost(this.checkpoint.getId());

                dj.execute(this.checkpoint.getId());

                long timeFrom = dj.getTargetPathCost(nextDelivery.getCheckpoint().getId());


                Date arrivalTime = new Date(previousDelivery.getDepartureTime().getTime() + timeTo);

                if (arrivalTime.after(this.checkpoint.getTimeRangeEnd())) {

                    continue;
                }

                long waitingTime = arrivalTime.before(this.checkpoint.getTimeRangeStart()) ? (this.checkpoint.getTimeRangeStart().getTime() - arrivalTime.getTime()) : 0;

                Date departureTime = new Date(arrivalTime.getTime() + waitingTime + this.checkpoint.getDuration());

                // Calcul timeFrom

                Date nextPointArrivalTime = new Date(departureTime.getTime() + timeFrom);

                if (nextPointArrivalTime.after(new Date(nextDelivery.getArrivalTime().getTime() + nextDelivery.getWaitingTime()))) {

                    continue;
                }

                success = true;
                // Attribution des nouvelles valeurs

                // Temps d'attente = ancien temps - différence entre la nouvelle heure d'arrivée et l'ancienne

                DeliveryTime newDeliveryTime = new DeliveryTime(this.checkpoint, arrivalTime, departureTime, waitingTime);

                nextDelivery.setWaitingTime(nextDelivery.getWaitingTime() - (nextPointArrivalTime.getTime() - nextDelivery.getArrivalTime().getTime()));

                nextDelivery.setArrivalTime(nextPointArrivalTime);

                deliveryTimes.add(j + 1, newDeliveryTime);

                break;

            }
            if (success) {
                k++;
            } else {
                this.modifiedRound.getRoundTimeOrders().remove(k);
                size--;
            }

        }

        return this.modifiedRound;
    }
}
