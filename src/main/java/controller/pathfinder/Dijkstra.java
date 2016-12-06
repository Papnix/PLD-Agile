package controller.pathfinder;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;

import model.Map;
import model.Section;

/**
 * @author Alexandre BENTO
 *
 */
public class Dijkstra {

	
	private final int MILLIS_TO_SEC = 1000;
	
	/**
	 * The current map instance.
	 */
	private Map map;
	/**
	 * Set of points that have been completely analyzed.
	 */
	private Set<Integer> processedWaypoints;
	/**
	 * Set of waypoints that have been visited.
	 */
	private Set<Integer> visitedWaypoints;
	
	/**
	 * Treemap of predecessor Waypoint for each Waypoint of the map
	 */
	private TreeMap<Integer, Integer> predecessors;
	
	/**
	 * Treemap of the cost of section between the waypoint.
	 */
	private TreeMap<Integer, Integer> cost;

	/**
	 * Dijkstra Class constructor
	 */
	public Dijkstra(Map map) {
		this.map = map;
	}

	/**
	 * @param target the desired destination
	 * @return The ids if all the waypoints forming the path from the current source to the target. Returns null if no path exists.
	 */
	public LinkedList<Integer> getPath(Integer target) {
		LinkedList<Integer> path = new LinkedList<Integer>();
		Integer step = target;

		if (predecessors.get(step) == null) {
			return null;
		}

		path.add(step);

		while (predecessors.get(step) != null) {
			step = predecessors.get(step);
			path.add(step);
		}

		// The path has to be reversed to be in the correct order
		Collections.reverse(path);

		return path;
	}

	/**
	 * @param target the desired destination
	 * @return The cost of the shortest path from the current source to the target. Returns null if no path exists.
	 */
	public int getTargetPathCost(int target) {
		return cost.get(target);
	}

	/**
	 * This method computes all the paths from the specified source to any reachable waypoint.
	 * @param source the origin from which the paths should be determined
	 */
	public void execute(int source) {
		processedWaypoints = new HashSet<Integer>();
		visitedWaypoints = new HashSet<Integer>();
		cost = new TreeMap<Integer, Integer>();
		predecessors = new TreeMap<Integer, Integer>();

		cost.put(source, 0);

		visitedWaypoints.add(source);

		while (visitedWaypoints.size() > 0) {
			Integer wp = getMinimum(visitedWaypoints);
			processedWaypoints.add(wp);
			visitedWaypoints.remove(wp);
			findMinimalCost(wp);
		}
	}

	/**
	 * @param idOrigin the id of the specified origin
	 * @return All the direct successors of the specified origin
	 */
	public int[] getSuccessors(int idOrigin) {
		
		if(map == null) System.out.println("flute");
		
		Object[] sections = map.getSections().get(idOrigin).values().toArray();
		int[] successors = new int[sections.length];

		for (int i = 0; i < successors.length; i++) {
			successors[i] = ((Section) sections[i]).getDestination().getId();
		}

		return successors;
	}

	/**
	 * @param idOrigin the id of the specified origin
	 * @param idDestination the id of the desired destination
	 * @return The cost of the shortest path from the origin to the destination
	 */
	public double computeCost(int idOrigin, int idDestination) {
		Section section = map.getSection(idOrigin, idDestination);

		return ((double) section.getLength()) / ((double) section.getSpeed())*MILLIS_TO_SEC;
	}

	/**
	 * @param idOrigin
	 */
	private void findMinimalCost(int idOrigin) {
		int[] successors = getSuccessors(idOrigin);

		for (int target : successors) {
			if (getShortestDistance(target) > getShortestDistance(idOrigin) + 
					computeCost(idOrigin, target)) {
				cost.put(target, (int) (getShortestDistance(idOrigin) + 
						computeCost(idOrigin, target)));
				predecessors.put(target, idOrigin);
				visitedWaypoints.add(target);
			}
		}
	}

	/**
	 * @param waypoints
	 * @return the 
	 */
	private int getMinimum(Set<Integer> waypoints) {
		Integer minimum = null;

		for (Integer w : waypoints) {
			if (minimum == null) {
				minimum = w;
			} else {
				if (getShortestDistance(w) < getShortestDistance(minimum)) {
					minimum = w;
				}
			}
		}
		return minimum;
	}
	
	/**
	 * @param idDestination the id of the desired destination.
	 * @return The cost of the shortest path from the source to the destination
	 */
	private Integer getShortestDistance(int idDestination) {
		Integer d = cost.get(idDestination);

		if (d == null) {
			return Integer.MAX_VALUE;
		} else {
			return d;
		}
	}

}
