package controller.tsp;

import java.util.Iterator;
import java.util.List;

import model.Checkpoint;
import model.DeliveryTime;

public class TSP1 extends TemplateTSP {

	
	@Override
	protected int bound(Checkpoint currentVertice, List<DeliveryTime> unseen, int[][] cost, int[] duration) {
		return 0;
	}

	@Override
	protected Iterator<DeliveryTime> iterator(DeliveryTime currentVertice, List<DeliveryTime> unseen, int[][] cost,
			int[] duration) {
		return new IteratorSeq(unseen, currentVertice);
	}

}
