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
	private Set<Integer> processedWaypoints;
    private Set<Integer> visitedWaypoints;
    private TreeMap<Integer, Integer> predecessors;
    private TreeMap<Integer, Integer> cost;
	
    public Dijkstra()
    {
    	map = Map.getInstance();
    }
    
    /*
     * This method returns the path from the source to the selected target and
     * NULL if no path exists
     */
    public LinkedList<Integer> getPath(Integer target)
    {
    		LinkedList<Integer> path = new LinkedList<Integer>();
            Integer step = target;
            
            // check if a path exists
            if (predecessors.get(step) == null)
            {
            	return null;
            }
            
            path.add(step);
            
            while (predecessors.get(step) != null)
            {
                step = predecessors.get(step);
                path.add(step);
            }
            
            // Put it into the correct order
            Collections.reverse(path);
            
            return path;
    }
    
    
    public int getTargetPathCost(int target)
    {
		return cost.get(target);
	}

	public void execute(int source)
	{
        processedWaypoints = new HashSet<Integer>();
        visitedWaypoints = new HashSet<Integer>();
        cost = new TreeMap<Integer, Integer>();
        predecessors = new TreeMap<Integer, Integer>();
        
        cost.put(source, 0);
        
        visitedWaypoints.add(source);
        
        while (visitedWaypoints.size() > 0)
        {
    		Integer wp = getMinimum(visitedWaypoints);
            processedWaypoints.add(wp);
            visitedWaypoints.remove(wp);
            findMinimalCost(wp);
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
	
	public static double computeCost(int idOrigin, int idDestination)
	{
		Section section = map.getSection(idOrigin, idDestination);
		
		return ((double) section.getLength())/((double) section.getSpeed());
	}
	
	private void findMinimalCost(int idOrigin)
	{
		int[] successors = getSuccessors(idOrigin);
		
	    for (int target : successors)
	    {
	        if (getShortestDistance(target) > getShortestDistance(idOrigin) + computeCost(idOrigin, target))
	        {
        		cost.put(target, (int) (getShortestDistance(idOrigin) + computeCost(idOrigin, target)));
                predecessors.put(target, idOrigin);
                visitedWaypoints.add(target);
	        }
	    }
	}
	
	// 
	private int getMinimum(Set<Integer> waypoints)
	{
		Integer minimum = null;
		
        for (Integer w : waypoints)
        {
            if (minimum == null)
            {
                    minimum = w;
            }
            else
            {
                if (getShortestDistance(w) < getShortestDistance(minimum))
                {
                        minimum = w;
                }
            }
        }
        return minimum;
}
	
	// trouve le plus petit cout allant au noeud idDestination dans le tableau de cout.
	private Integer getShortestDistance(int idDestination)
	{
        Integer d = cost.get(idDestination);
        
        if (d == null)
        {
            return Integer.MAX_VALUE;
        }
        else
        {
            return d;
        }
	}
	
}
