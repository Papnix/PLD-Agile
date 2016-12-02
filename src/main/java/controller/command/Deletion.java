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
        this.modifiedRound = new Round(this.previousRound);
        List<DeliveryTime> arrivalTimes = this.modifiedRound.getArrivalTimes();
        for (int i = 0; i < arrivalTimes.size(); i++) {
            if (arrivalTimes.get(i).getCheckpoint().getId() == checkpoint.getId()) {
                this.modifiedRound.getArrivalTimes().remove(i);
                break;
            }
        }
        return this.modifiedRound;
    }
}
