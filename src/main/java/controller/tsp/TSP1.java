package controller.tsp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.Checkpoint;
import model.DeliveryTime;

public class TSP1 extends TemplateTSP {

	@Override
	protected int bound(Checkpoint currentVertice, List<DeliveryTime> unseen, int[][] cost, int[] duration) {
		
		int bound = 0;
		
		List<Integer> indicesToConsider = new ArrayList<Integer>();
		
		for (DeliveryTime dt1 : unseen) {
			indicesToConsider.add(indexValues.get(dt1.getCheckpoint().getId()));
		}
		
		for (Integer index : indicesToConsider) {
			
			int[] outCost = cost[index];
			
			int min = getMinimum(index, outCost, indicesToConsider);
			
			if (outCost.length > 0) {
				bound += min;
			}
			
			bound += duration[index];
		}
		
		return bound;
	}

	@Override
	protected Iterator<DeliveryTime> iterator(DeliveryTime currentVertice, List<DeliveryTime> unseen, int[][] cost,
			int[] duration) {
		return new IteratorSeq(unseen, currentVertice);
	}
	
	/**
	 * This method returns the minimum edge from the current node, to the other nodes to consider.
	 * It should only be used to compute the lower bound of the current round.
	 * @param cur_index the index of the current node
	 * @param array the cost of every edge leaving the current node
	 * @param indicesToConsider list of the nodes to consider to compute the lower bound
	 * @return the minimum edge leaving the current node
	 */
	private int getMinimum(int cur_index, int[] array, List<Integer> indicesToConsider)
	{
		int min = Integer.MAX_VALUE;
		
		for (int i = 0; i < array.length; i++) {
			if (array[i] < min && cur_index != i) {
				if (indicesToConsider.contains(i)) {
					min = array[i];
				}
			}
		}
		
		return min;
	}

}
