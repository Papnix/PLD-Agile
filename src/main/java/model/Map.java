package model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Map {

    private TreeMap<Integer, Waypoint> waypoints;
    private TreeMap<Integer, TreeMap<Integer, Section>> sections;

    public Map(){
    	waypoints = new TreeMap<Integer, Waypoint>();
        sections = new TreeMap<Integer, TreeMap<Integer, Section>>();
    }

    public Map addWaypoint(Waypoint waypoint) {
        waypoints.put(waypoint.getId(), waypoint);

        return this;
    }

    public Map addSection(Section section) {
        if (sections.get(section.getOrigin().getId()) == null) {
            sections.put(section.getOrigin().getId(), new TreeMap<Integer, Section>());
        }
        sections.get(section.getOrigin().getId()).put(section.getDestination().getId(), section);

        return this;
    }

    public void addMultipleWaypoint(ArrayList<Waypoint> waypoints) {

        for (Waypoint waypoint : waypoints) {
            addWaypoint(waypoint);
        }

    }

    public void addMultipleSection(ArrayList<Section> sections) {
        for (Section section : sections) {
            addSection(section);
        }
    }

    public TreeMap<Integer, Waypoint> getWaypoints() {
        return waypoints;
    }

    public Waypoint getWaypoint(int id) {

        return waypoints.get(id);
    }

    public TreeMap<Integer, TreeMap<Integer, Section>> getSections() {
        return sections;
    }

    public Section getSection(int idOrigin, int idDestination) {
        return sections.get(idOrigin).get(idDestination);
    }
    
    public List<Section> getAllSections() {
    	List<Section> result = new ArrayList<Section>();
    	
    	for (Integer i : sections.keySet()) {
    		TreeMap<Integer, Section> sec = sections.get(i);
    		
    		for (Integer j : sec.keySet()) {
    			result.add(sec.get(j));
    		}
    	}
    	
    	return result;
    }
    
    public List<Section> getActiveSections() {
    	List<Section> result = new ArrayList<Section>();
    	
    	for (Integer i : sections.keySet()) {
    		TreeMap<Integer, Section> sec = sections.get(i);
    		
    		for (Integer j : sec.keySet()) {
    			Section s = sec.get(j);
    			
    			if (s.isActive()) {
    				result.add(s);
    			}
    		}
    	}
    	
    	return result;
    }
    
    public void resetPath() {
    	List<Section> sec = getAllSections();
    	
    	for (Section s : sec) {
    		s.setActive(false);
    	}
    }

    public Map clear() {
        sections.clear();
        waypoints.clear();
        return this;
    }
}
