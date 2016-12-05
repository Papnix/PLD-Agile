package controller.tsp;


import java.util.Iterator;
import java.util.List;

import model.Checkpoint;
import model.DeliveryTime;

public class TSP1 extends TemplateTSP{

	@Override
	protected int bound(Checkpoint sommetcrt, List<DeliveryTime> nonVus, int[][] cout, int[] duree) {

		return 0;
	}

	@Override
	protected Iterator<DeliveryTime> iterator(DeliveryTime sommetcrt, List<DeliveryTime> nonVus, int[][] cout,
			int[] duree) {
		return new IteratorSeq(nonVus, sommetcrt);
	}

}
