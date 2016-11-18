package model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Map {

    private static Map instance = null;

    private TreeMap<Integer, Waypoint> waypoints;
    private TreeMap<Integer, TreeMap<Integer, Section>> sections;

    private Map() {
        waypoints = new TreeMap<Integer, Waypoint>();
        sections = new TreeMap<Integer, TreeMap<Integer, Section>>();
    }

    public static Map getInstance() {
        if (instance == null) instance = new Map();
        return instance;
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

    public Map addMultipleWaypoint(ArrayList<Waypoint> waypoints) {

        for (Waypoint waypoint : waypoints) {
            addWaypoint(waypoint);
        }

        return this;
    }

    public Map addMultipleSection(ArrayList<Section> sections) {
        for (Section section : sections) {
            addSection(section);
        }

        return this;
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
}
