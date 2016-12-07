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
    private DeliveryTime deliveryTime;

    public TimeChange(DeliveryTime deliveryTime, Round round, Date start, Date end, Map map) {
        super(round);
        this.start = start;
        this.end = end;
        this.map = map;
        this.deliveryTime = deliveryTime;
    }

    public Round doCommand() {
        this.modifiedRound = new Round(this.previousRound);
        // Si la plage horaire a été retirée, aucun changement n'est nécessaire
        if (this.start == null && this.end == null) {
            return this.modifiedRound;
        }
        for (List<DeliveryTime> arrivalTimes : this.modifiedRound.getRoundTimeOrders()) {
            // Indices des DeliveryTiems les plus proches du début et de la fin de la nouvelle plage (mais non compris)
            // Permet de définir un intervalle sur lequel placer la livraison si la nouvelle plage demande un changement
            int startIndex = -1, endIndex = -1, index = -1;

            DeliveryTime deliveryTime = null;

            for (int i = 0; i < arrivalTimes.size(); i++) {
                deliveryTime = arrivalTimes.get(i);
                List<DeliveryTime> intervalTimes = new ArrayList<>();
                if (deliveryTime.getDepartureTime().before(this.start)) {
                    startIndex = i;
                } else if (deliveryTime.getArrivalTime().after(this.end)) {
                    endIndex = endIndex == -1 ? i : endIndex;
                } else {
                    intervalTimes.add(i, deliveryTime);
                }
                if (deliveryTime.getCheckpoint().getId() == this.deliveryTime.getCheckpoint().getId()) {
                    deliveryTime.getCheckpoint().setTimeRangeStart(this.start);
                    deliveryTime.getCheckpoint().setTimeRangeEnd(this.end);
                    // Si la livraison est toujours à l'heure après le changement, aucune autre modif à faire
                    if (deliveryTime.getArrivalTime().after(this.start) && deliveryTime.getArrivalTime().before(this.end)) {
                        return this.modifiedRound;
                    }
                    index = i;
                }
            }
            // On cherche s'il est possible d'insérer la livraison quelque part
            for (int j = startIndex; j < endIndex; j++) {

                DeliveryTime previousDelivery = arrivalTimes.get(j);
                DeliveryTime nextDelivery = arrivalTimes.get(j+1);

                long timeTo = 0, timeFrom = 0;

                Dijkstra dj = new Dijkstra(this.map);
                dj.execute(previousDelivery.getCheckpoint().getId());
                timeTo = dj.getTargetPathCost(this.deliveryTime.getCheckpoint().getId());
                dj.execute(this.deliveryTime.getCheckpoint().getId());
                timeFrom = dj.getTargetPathCost(nextDelivery.getCheckpoint().getId());

                Date arrivalTime = new Date(previousDelivery.getDepartureTime().getTime() + timeTo * 1000);
                if (arrivalTime.after(this.end)) {
                    continue;
                }
                long waitingTime = arrivalTime.before(this.start) ? (this.start.getTime() - arrivalTime.getTime()) : 0;
                Date departureTime = new Date(arrivalTime.getTime() + waitingTime + this.deliveryTime.getCheckpoint().getDuration());
                // Calcul timeFrom
                Date nextPointArrivalTime = new Date(departureTime.getTime() + timeFrom);
                if(nextPointArrivalTime.after(new Date(nextDelivery.getArrivalTime().getTime() + nextDelivery.getWaitingTime()))) {
                    continue;
                }
                // Attribution des nouvelles valeurs
                // Temps d'attente = ancien temps - différence entre la nouvelle heure d'arrivée et l'ancienne
                nextDelivery.setWaitingTime(nextDelivery.getWaitingTime() - (nextPointArrivalTime.getTime() - nextDelivery.getArrivalTime().getTime()));
                nextDelivery.setArrivalTime(nextPointArrivalTime);
                this.deliveryTime.setArrivalTime(arrivalTime);
                this.deliveryTime.setWaitingTime(waitingTime);
                this.deliveryTime.setDepartureTime(departureTime);
                arrivalTimes.remove(index);
                arrivalTimes.add(j+1, this.deliveryTime);
            }
        }
        return this.modifiedRound;
    }
}
