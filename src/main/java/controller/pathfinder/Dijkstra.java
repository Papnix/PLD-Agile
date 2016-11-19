package controller.pathfinder;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;

import model.Map;
import model.Section;

public class Dijkstra
{	
	
	private static Map map;
	private Set<Integer> settledNodes;
    private Set<Integer> unSettledNodes;
    private TreeMap<Integer, Integer> predecessors;
    private TreeMap<Integer, Double> cost;
	
    
    /*
     * This method returns the path from the source to the selected target and
     * NULL if no path exists
     */
    public LinkedList<Integer> getPath(Integer target) {
            map = Map.getInstance();
    		LinkedList<Integer> path = new LinkedList<Integer>();
            Integer step = target;
            // check if a path exists
            if (predecessors.get(step) == null) {
                    return null;
            }
            path.add(step);
            while (predecessors.get(step) != null) {
                    step = predecessors.get(step);
                    path.add(step);
            }
            // Put it into the correct order
            Collections.reverse(path);
            return path;
    }
    
    
    public void execute(int source) {
        settledNodes = new HashSet<Integer>();
        unSettledNodes = new HashSet<Integer>();
        cost = new TreeMap<Integer, Double>();
        predecessors = new TreeMap<Integer, Integer>();
        cost.put(source, 0.0);
        unSettledNodes.add(source);
        while (unSettledNodes.size() > 0) {
        		Integer node = getMinimum(unSettledNodes);
                settledNodes.add(node);
                unSettledNodes.remove(node);
                findMinimalCost(node);
        }
}
    
	public static int[] getSuccessors(int idOrigin)
	{
		Object[] sections = map.getSections().get(idOrigin).values().toArray();
		int[] successors = new int[sections.length];
		for (int i = 0; i < successors.length; i++)
		{
			successors[i] = ((Section)sections[i]).getDestination().getId();
		}
		
		return successors;
	}
	
	public static double calculateCost(int idOrigin, int idDestination)
	{
		Map map = Map.getInstance();
		Section section = map.getSection(idOrigin, idDestination);
		
		return section.getLength()/section.getSpeed();
	}
	
	private void findMinimalCost(int idOrigin) {
		int[] successors = getSuccessors(idOrigin);
	    for (int target : successors) {
	        if (getShortestDistance(target) > getShortestDistance(idOrigin) + calculateCost(idOrigin, target)) {
	        		cost.put(target, getShortestDistance(idOrigin) + calculateCost(idOrigin, target));
	                predecessors.put(target, idOrigin);
	                unSettledNodes.add(target);
	        }
	    }
	}
	
	// 
	private int getMinimum(Set<Integer> vertexes) {
		Integer minimum = null;
        for (Integer vertex : vertexes) {
                if (minimum == null) {
                        minimum = vertex;
                } else {
                        if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
                                minimum = vertex;
                        }
                }
        }
        return minimum;
}
	
	// trouve le plus petit cout allant au noeud idDestination dans le tableau de cout.
	private double getShortestDistance(int idDestination) {
        Double d = cost.get(idDestination);
        if (d == null) {
                return Double.MAX_VALUE;
        } else {
                return d;
        }
	}
	
}
