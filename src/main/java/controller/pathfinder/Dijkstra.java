package controller.pathfinder;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;

import model.Map;
import model.Section;

public class Dijkstra
{	
	public static int[] getSuccessors(int idOrigin)
	{
		Map map = Map.getInstance();
		
		Object[] sections = map.getSections().get(idOrigin).values().toArray();
		
		int[] successors = new int[sections.length];
		
		for (int i = 0; i < successors.length; i++)
		{
			successors[i] = ((Section)sections[i]).getDestination().getId();
		}
		
		return successors;
	}
	
}
