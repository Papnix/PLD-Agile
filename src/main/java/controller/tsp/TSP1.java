package controller.tsp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.Checkpoint;
import model.DeliveryTime;

public class TSP1 extends TemplateTSP {

	@Override
	protected int bound(Checkpoint currentVertex, List<DeliveryTime> unseen, int[][] cost, int[] duration) {
		
		int bound = 0;
		
		List<Integer> indicesToConsider = new ArrayList<Integer>();
		
		for (DeliveryTime dt1 : unseen) {
			indicesToConsider.add(indexValues.get(dt1.getCheckpoint().getId()));
		}
		
		// From current vertex to unseen ones
		
		int cur_index = indexValues.get(currentVertex.getId());
		
		bound += getMinimumCost(cur_index, cost[cur_index], indicesToConsider) + duration[cur_index];
		
		// In between unseen vertices
		
		
		for (int i = 0; i < indicesToConsider.size() - 1; i++) {
			
			int index = indicesToConsider.get(i);
			bound += getMinCostToVertex(index, cost, indicesToConsider) + duration[index];
		}
		
		// To return to warehouse
		
		bound += getMinCostToWarehouse(cost, indicesToConsider);
		
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
	private int getMinimumCost(int cur_index, int[] array, List<Integer> indicesToConsider){
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
	
	/**
	 * This method returns the cost of the minimum path from the vertices contained in indicesToConsider, to the warehouse.
	 * @param cost the general cost matrix
	 * @param indicesToConsider the indices of the vertices that should be considered in the computation of the minimum.
	 * @return the cost of the minimum path to the warehouse.
	 */
	private int getMinCostToWarehouse(int[][] cost, List<Integer> indicesToConsider) {
		int min = Integer.MAX_VALUE;
		
		for (Integer index : indicesToConsider) {
			if (cost[index][0] < min) {
				min = cost[index][0];
			}
		}
		
		return min;
	}
	
	/**
	 * This method gives the minimum cost to a particular vertex, from any vertex contained in indicesToConsider.
	 * @param vertex the destination vertex
	 * @param cost the general cost matrix
	 * @param indicesToConsider the indices of the vertices that should be considered in the computation of the minimum.
	 * @return
	 */
	private int getMinCostToVertex(int vertex, int[][] cost, List<Integer> indicesToConsider) {
		int min = Integer.MAX_VALUE;
		
		for (Integer i : indicesToConsider) {
			if (cost[i][vertex] < min && vertex != i) {
				min = cost[i][vertex];
			}
		}
		
		return min;
	}

}
