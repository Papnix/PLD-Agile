package model;

import java.util.ArrayList;
import java.util.List;

public class Map {

    private static Map instance = null;

    private ArrayList<Waypoint> waypoints;
    private ArrayList<ArrayList<Section>> sections;

    private Map() {
        waypoints = new ArrayList<Waypoint>();
        sections = new ArrayList<ArrayList<Section>>();
    }

    public static Map getInstance() {
        if (instance == null) instance = new Map();
        return instance;
    }

    public Map addWaypoint(Waypoint waypoint) {
        waypoints.set(waypoint.getId(), waypoint);

        return this;
    }

    public Map addSection(Section section) {
        sections.get(section.getOrigin().getId()).set(section.getDestination().getId(), section);

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

    public ArrayList<Waypoint> getWaypoints() {
        return waypoints;
    }

    public Waypoint getWaypoint(int id) {

        return waypoints.get(id);
    }

    public Section getSection(int idOrigin, int idDestination) {
        return sections.get(idOrigin).get(idDestination);
    }
}
