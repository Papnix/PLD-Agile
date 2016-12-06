package controller.command;

import model.Checkpoint;
import model.DeliveryTime;
import model.Round;

import java.util.ArrayList;
import java.util.List;

public class Deletion extends Command {

    public Deletion(Round round) {
        super(round);
    }

    public Round doCommand(Checkpoint checkpoint) {
        for(List<DeliveryTime> arrivalTimes : this.modifiedRound.getRoundTimeOrders()) {
            for (int i = 0; i < arrivalTimes.size(); i++) {
                if (arrivalTimes.get(i).getCheckpoint().getId() == checkpoint.getId()) {
                    arrivalTimes.remove(i);
                    break;
                }
            }
        }
        return this.modifiedRound;
    }
}
