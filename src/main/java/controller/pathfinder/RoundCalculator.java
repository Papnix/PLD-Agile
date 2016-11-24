package controller.pathfinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.collections4.MapUtils;

import model.Delivery;
import model.DeliveryRequest;
import model.Waypoint;
import model.Map;
import model.Section;
import tsp.TSP1;

public class RoundCalculator {

	/**
	 * The delivery request on which all calculations will be based.
	 */
	private DeliveryRequest delReq;
	/**
	 * This array contains the cost of the shortest path from w1 to w2 (costTab[w1][w2]).
	 */
	private int[][] costTab;
	/**
	 * The Dijkstra instance that will compute the paths.
	 */
	private Dijkstra dj;

	/**
	 * This hashmap contains all the computed paths (represented by lists of waypoint ids). To get the path from w1 to
	 * w2, use paths.get(w1).get(w2) (w1 and w2 have to be waypoint ids).
	 */
	private HashMap<Integer, HashMap<Integer, List<Integer>>> paths;

	/**
	 * The list of all the waypoints that have to be delivered.
	 */
	private List<Waypoint> waypoints;

	/**
	 * This hashmap is used to bind a waypoint id to a unique array index.
	 */
	private HashMap<Integer, Integer> indexValues;

	/**
	 * @param delReq the delivery request that has to be analyzed
	 */
	public RoundCalculator(DeliveryRequest delReq) {
		this.delReq = delReq;
		
		// initializing the waypoints array

		waypoints = new ArrayList<Waypoint>();

		waypoints.add(delReq.getWarehouse().getAssociatedWaypoint());

		for (Delivery d : delReq.getDeliveryPointList()) {
			waypoints.add(d.getAssociatedWaypoint());
		}
		
		// initializing the cost array

		int size = waypoints.size();
		costTab = new int[size][size];
		
		dj = new Dijkstra();
		
		// assigning indices to every waypoint
		
		indexValues = new LinkedHashMap<Integer, Integer>();

		int index = 0;

		for (Waypoint w : waypoints) {
			indexValues.put(w.getId(), index);
			index++;
		}

		paths = new LinkedHashMap<Integer, HashMap<Integer, List<Integer>>>();
	}

	/**
	 * This method compute the paths from any waypoint (in the delivery request) to any other.
	 */
	public void computePaths() {
		paths.clear();
		
		// selecting an origin waypoint
		for (Waypoint w1 : waypoints) {
			
			// compute all paths from the origin
			dj.execute(w1.getId());
			
			// selecting a destination
			for (Waypoint w2 : waypoints) {
				
				// if the origin and the destination are equal, the cost of the path is 0.
				// else, the path is replaced with the result from Dijkstra.
				if (w1.getId() == w2.getId()) {
					costTab[indexValues.get(w1.getId())][indexValues.get(w2.getId())] = 0;
				} else {
					costTab[indexValues.get(w1.getId())][indexValues.get(w2.getId())] = dj
							.getTargetPathCost(w2.getId());
				}

				HashMap<Integer, List<Integer>> path = paths.get(w1.getId());
				
				// if no path has been determined for w1, the structure containing all its paths is created.
				if (path == null) {
					path = new LinkedHashMap<Integer, List<Integer>>();
					paths.put(w1.getId(), path);

				}
				
				// the path is inserted in the structure
				path.put(w2.getId(), dj.getPath(w2.getId()));
			}
		}
	}

	/**
	 * This method computes the best possible round. Method computePaths must be called before.
	 * @return The ids of the waypoints in the best round, in the right order (the warehouse is both at the beginning
	 * and the end of the round.
	 */
	public int[] computeRound() {
		int size = delReq.getDeliveryPointList().size() + 1;

		// the visiting time of every waypoint is initialized
		int[] duration = new int[size];
		
		duration[indexValues.get(delReq.getWarehouse().getAssociatedWaypoint().getId())] = 0;

		for (Delivery d : delReq.getDeliveryPointList()) {
			duration[indexValues.get(d.getAssociatedWaypoint().getId())] = d.getDuration();
		}

		TSP1 t = new TSP1();

		// The TSP algorithm is used to compute the best round
		t.chercheSolution(Integer.MAX_VALUE, size, costTab, duration);

		Map map = Map.getInstance();

		int[] round = new int[size + 1];
		
		// all the sections used on the round are set active
		map.resetPath();
		HashMap<Integer, Integer> mapInversed = (HashMap<Integer, Integer>)MapUtils.invertMap(indexValues);
		for (int i = 0; i < size; i++){
			round[i] = mapInversed.get(t.getMeilleureSolution(i));
			List<Integer> path;

			if (i < size - 1) {
				path = paths.get(round[i]).get(mapInversed.get(t.getMeilleureSolution(i + 1)));
			} else {
				path = paths.get(round[i]).get(round[0]);
			}

			for (int j = 0; j < path.size(); j++) {
				Section s;

				if (j < path.size() - 1) {
					s = map.getSection(path.get(j), path.get(j + 1));

					s.setActive(true);
				}
			}
		}
		round[size] = round[0];
		return round;
	}

	/**
	 * @param idOrigin the id of the origin
	 * @param idDestination the id of the destination
	 * @return The cost of the shortests path from the origin to the destination.
	 */
	public int getCost(int idOrigin, int idDestination) {
		return costTab[indexValues.get(idOrigin)][indexValues.get(idDestination)];
	}

	/**
	 * @return the array containing the cost of the shortest path from any waypoint (in the delivery request) to any
	 * other.
	 */
	public int[][] getCost() {
		return costTab;
	}
}
