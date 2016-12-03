package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.collections4.MapUtils;
import controller.pathfinder.Dijkstra;
import tsp.TSP1;

public class Round
{
	//-------------------------------------------------- Attributes ----------------------------------------------------
	
	private int duration;
	
	private List<Section> route;
	private DeliveryRequest request;
	private List<DeliveryTime> arrivalTime;
	
	/**
	 * This array contains the cost of the shortest path from cp1 to cp2 (costTab[cp1][cp2]).
	 */
	private int[][] costTab;
	/**
	 * The Dijkstra instance that will compute the paths.
	 */
	private Dijkstra dj;
	/**
	 * This hashmap contains all the computed paths (represented by lists of waypoint ids). To get the path from cp1 to
	 * cp2, use paths.get(cp1).get(cp2) (cp1 and cp2 have to be waypoint ids).
	 */
	private HashMap<Integer, HashMap<Integer, List<Integer>>> paths;

	/**
	 * This hashmap is used to bind a waypoint id to a unique array index.
	 */
	private HashMap<Integer, Integer> indexValues;
		
	//--------------------------------------------------- Methods ------------------------------------------------------
	
			//----------------------------------------- Constructors ---------------------------------------------------

	public Round(DeliveryRequest request) {

		this.request = request;
		arrivalTime = new ArrayList<DeliveryTime>();
		route = new ArrayList<Section>();
		buildIndex();
	}

			//------------------------------------------- Getters ------------------------------------------------------
	
	public int getDuration() {
		return duration;
	}
	
	public List<Section> getRoute() {
		return route;
	}
	
	public DeliveryRequest getRequest() {
		return request;
	}

	public List<DeliveryTime> getArrivalTimes() {
		return arrivalTime;
	}
	
			//------------------------------------------- Setters ------------------------------------------------------

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public void setRoute(List<Section> route) {
		this.route = route;
	}
	
			//---------------------------------------- Other methods ---------------------------------------------------

	/**
	 * @param delReq the delivery request that has to be analyzed
	 */
	public void buildIndex() {

		// initializing the cost array
		int size = request.getDeliveryPointList().size();
		costTab = new int[size][size];
		
		// assigning indices to every waypoint
		indexValues = new LinkedHashMap<Integer, Integer>();

		for (int index = 0; index < size; ++index) {
			indexValues.put(request.getDeliveryPoint(index).getId(), index);
		}

		paths = new LinkedHashMap<Integer, HashMap<Integer, List<Integer>>>();
	}

	/**
	 * This method compute the paths from any waypoint (in the delivery request) to any other.
	 */
	public void computePaths(Map map) {
		dj = new Dijkstra(map);
		paths.clear();
		
		// selecting an origin waypoint
		for (Checkpoint checkpoint1 : request.getDeliveryPointList()) {
			
			// compute all paths from the origin
			dj.execute(checkpoint1.getId());
			
			// selecting a destination
			for (Checkpoint checkpoint2 : request.getDeliveryPointList()) {
				
				// if the origin and the destination are equal, the cost of the path is 0.
				// else, the path is replaced with the result from Dijkstra.
				if (checkpoint1.getId() == checkpoint2.getId()) {
					costTab[indexValues.get(checkpoint1.getId())][indexValues.get(checkpoint2.getId())] = 0;
				} else {
					costTab[indexValues.get(checkpoint1.getId())][indexValues.get(checkpoint2.getId())] = dj.getTargetPathCost(checkpoint2.getId());
				}

				HashMap<Integer, List<Integer>> path = paths.get(checkpoint1.getId());
				
				// if no path has been determined for cp1, the structure containing all its paths is created.
				if (path == null) {
					path = new LinkedHashMap<Integer, List<Integer>>();
					paths.put(checkpoint1.getId(), path);

				}
				
				// the path is inserted in the structure
				path.put(checkpoint2.getId(), dj.getPath(checkpoint2.getId()));
			}
		}
	}

	/**
	 * This method computes the best possible round. Method computePaths must be called before.
	 * @return The ids of the waypoints in the best round, in the right order (the warehouse is both at the beginning
	 * and the end of the round.
	 */
	public void computeRound(Map map) {
		
		computePaths(map);
		
		int[] durations = initializeWaypointTime();
		
		int numberOfDelivery = request.getDeliveryPointList().size();
				
		TSP1 tspAlgorithm = new TSP1();

		// The TSP algorithm is used to compute the best round
		tspAlgorithm.chercheSolution(Integer.MAX_VALUE, numberOfDelivery, costTab, durations);
		
		
		HashMap<Integer, Integer> inversedMap = (HashMap<Integer, Integer>) MapUtils.invertMap(indexValues);
		
		for (int i = 0; i < numberOfDelivery; i++){
			
			int checkpointIndex = tspAlgorithm.getMeilleureSolution(i);
			arrivalTime.add(new DeliveryTime(request.getDeliveryPoint(checkpointIndex), null));

			
			List<Integer> path;

			Checkpoint checkpoint = request.getDeliveryPoint(checkpointIndex);
			
			if (i < numberOfDelivery - 1) {
				
				int nextCheckpointIndex = tspAlgorithm.getMeilleureSolution(i + 1);
				path = paths.get(checkpoint.getId()).get(inversedMap.get(nextCheckpointIndex));
				
			} else {
				path = paths.get(checkpoint.getId()).get(inversedMap.get(0));
			}

			for (int j = 0; j < path.size(); j++) {
				Section section;

				if (j < path.size() - 1) {
					section = map.getSection(path.get(j), path.get(j + 1));
					route.add(section);
				}
			}
		}
		
		arrivalTime.add(new DeliveryTime(request.getDeliveryPoint(0), null));

	}

	/**
	 * @param idOrigin the id of the origin
	 * @param idDestination the id of the destination
	 * @return The cost of the shortests path from the origin to the destination.
	 */
	public int getCost(int idOrigin, int idDestination) {
		return costTab[indexValues.get(idOrigin)][indexValues.get(idDestination)];
	}
	
	public void addStep(Checkpoint step)
	{
		
	}
	
	//---- Private methods -----------------------------------------------------------------------------------	
	
	private int[] initializeWaypointTime() {
		int size = request.getDeliveryPointList().size();

		// the visiting time of every waypoint is initialized
		int[] durations = new int[size];

		for (Checkpoint d : request.getDeliveryPointList()) {
			durations[indexValues.get(d.getAssociatedWaypoint().getId())] = d.getDuration();
		}

		return durations;
	}
	
	public List<Integer> getPath(Integer idOrigin, Integer idDestination) {
		return paths.get(idOrigin).get(idDestination);
	}
}
