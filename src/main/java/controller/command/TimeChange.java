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
        // Si la plage horaire a été retirée, aucun changement n'est nécessaire
        if (this.start == null && this.end == null) {
            return this.modifiedRound;
        }
        int count = 0;
        for (List<DeliveryTime> deliveryTimes : this.modifiedRound.getRoundTimeOrders()) {
            count++;
            // Indices des DeliveryTiems les plus proches du début et de la fin de la nouvelle plage (mais non compris)
            // Permet de définir un intervalle sur lequel placer la livraison si la nouvelle plage demande un changement
            int startIndex = -1, endIndex = -1, index = -1;

            DeliveryTime deliveryTime = null;

            for (int i = 0; i < deliveryTimes.size(); i++) {
                deliveryTime = deliveryTimes.get(i);
                if (deliveryTime.getDepartureTime() != null && deliveryTime.getDepartureTime().before(this.start)) {
                    startIndex = i;
                } else if (deliveryTime.getArrivalTime() != null && deliveryTime.getArrivalTime().after(this.end)) {
                    endIndex = endIndex == -1 ? i : endIndex;
                }
                if (deliveryTime.getCheckpoint().getId() == this.deliveryTime.getCheckpoint().getId()) {
                    // Si la livraison est toujours à l'heure après le changement, aucune autre modif à faire
                    if (deliveryTime.getArrivalTime().after(this.start) && deliveryTime.getArrivalTime().before(this.end)) {
                        // Si on arrive à temps mais qu'on attend trop avant de livrer, on réduit l'attente
                        if (this.start.after(new Date(deliveryTime.getArrivalTime().getTime() + deliveryTime.getWaitingTime()))) {
                            long waitingTime = this.deliveryTime.getWaitingTime();
                            this.deliveryTime.setWaitingTime(0);
                            this.deliveryTime.setDepartureTime(new Date(this.deliveryTime.getDepartureTime().getTime() - waitingTime));
                            deliveryTimes.get(i + 1).setWaitingTime(deliveryTimes.get(i + 1).getWaitingTime() + waitingTime);
                            deliveryTimes.get(i + 1).setArrivalTime(new Date(deliveryTimes.get(i + 1).getArrivalTime().getTime() - waitingTime));
                        }
                        this.deliveryTime.getCheckpoint().setTimeRangeStart(this.start);
                        this.deliveryTime.getCheckpoint().setTimeRangeEnd(this.end);
                        return this.modifiedRound;
                    }
                    index = i;
                }
            }
            // On cherche s'il est possible d'insérer la livraison quelque part
            for (int j = startIndex; j < endIndex; j++) {

                DeliveryTime previousDelivery = deliveryTimes.get(j);
                DeliveryTime nextDelivery = deliveryTimes.get(j + 1);

                Dijkstra dj = new Dijkstra(this.map);
                dj.execute(previousDelivery.getCheckpoint().getId());
                long timeTo = dj.getTargetPathCost(this.deliveryTime.getCheckpoint().getId());
                dj.execute(this.deliveryTime.getCheckpoint().getId());
                long timeFrom = dj.getTargetPathCost(nextDelivery.getCheckpoint().getId());

                Date arrivalTime = new Date(previousDelivery.getDepartureTime().getTime() + timeTo);
                if (arrivalTime.after(this.end)) {
                    continue;
                }
                long waitingTime = arrivalTime.before(this.start) ? (this.start.getTime() - arrivalTime.getTime()) : 0;
                Date departureTime = new Date(arrivalTime.getTime() + waitingTime + this.deliveryTime.getCheckpoint().getDuration());
                // Calcul timeFrom
                Date nextPointArrivalTime = new Date(departureTime.getTime() + timeFrom);
                if (nextPointArrivalTime.after(new Date(nextDelivery.getArrivalTime().getTime() + nextDelivery.getWaitingTime()))) {
                    continue;
                }
                // Attribution des nouvelles valeurs
                // Temps d'attente = ancien temps - différence entre la nouvelle heure d'arrivée et l'ancienne
                this.deliveryTime.getCheckpoint().setTimeRangeStart(this.start);
                this.deliveryTime.getCheckpoint().setTimeRangeEnd(this.end);
                nextDelivery.setWaitingTime(nextDelivery.getWaitingTime() - (nextPointArrivalTime.getTime() - nextDelivery.getArrivalTime().getTime()));
                nextDelivery.setArrivalTime(nextPointArrivalTime);
                this.deliveryTime.setArrivalTime(arrivalTime);
                this.deliveryTime.setWaitingTime(waitingTime);
                this.deliveryTime.setDepartureTime(departureTime);
                deliveryTimes.remove(index);
                deliveryTimes.add(j + 1, this.deliveryTime);

                break;
            }
        }
        return this.modifiedRound;
    }
}
