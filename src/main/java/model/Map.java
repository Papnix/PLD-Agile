package model;

import java.util.ArrayList;
import java.util.List;

public class Map {
	List<Waypoint> waypoints;
	List<Section> sections;
	
	public Map()
	{
		waypoints = new ArrayList();
		sections = new ArrayList();
	}
	
	public void addWaypoint(Waypoint wp)
	{
		waypoints.add(wp);
	}
	
	public void addSection(Section section)
	{
		sections.add(section);
	}
	
	public void addMultipleWaypoint(List<Waypoint> wp)
	{
		waypoints.addAll(wp);
	}
	
	public void addMultipleSection(List<Section> section)
	{
		sections.addAll(section);
	}

	public List<Waypoint> getWaypoints() {
		return waypoints;
	}

	public List<Section> getSections() {
		return sections;
	}
	
}
