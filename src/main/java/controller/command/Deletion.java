package controller.command;

import model.Checkpoint;
import model.DeliveryTime;
import model.Round;

import java.util.List;

public class Deletion extends Command {

    private Checkpoint checkpoint;

    public Deletion(Round round, Checkpoint checkpoint) {
        super(round);
        this.checkpoint = checkpoint;
    }

    public Round doCommand() {
        for(List<DeliveryTime> arrivalTimes : this.modifiedRound.getRoundTimeOrders()) {
            for (int i = 0; i < arrivalTimes.size(); i++) {
                if (arrivalTimes.get(i).getCheckpoint().getId() == this.checkpoint.getId()) {
                    arrivalTimes.remove(i);
                    break;
                }
            }
        }
        return this.modifiedRound;
    }
}
