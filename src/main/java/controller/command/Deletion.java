package controller.command;

import model.Checkpoint;
import model.DeliveryTime;
import model.Round;

import java.util.List;

public class Deletion extends Command {

    public Deletion(Round round) {
        super(round);
    }

    public Round doCommand(Checkpoint checkpoint) {
        /*List<DeliveryTime> arrivalTimes = round.getArrivalTimes();
        for(int i = 0; i<arrivalTimes.size(); i++) {
            if(arrivalTimes.get(i).getCheckpoint().getId() == this.deliveryTime.getCheckpoint().getId()) {
                index = i;
                round.getArrivalTimes().remove(i);
                break;
            }
        }*/
        return this.modifiedRound;
    }
}
