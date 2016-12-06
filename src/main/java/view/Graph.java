package view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import model.DeliveryTime;
import model.Map;
import model.Round;
import model.Section;
import model.Waypoint;
import view.GraphNode.State;

public class Graph extends Pane {

	private Map map;
	private HashMap<Integer, GraphNode> nodes;
	private HashMap<String, Line> sections;
	private LinkedList<String> lightedUpRoads;
	private LinkedList<Integer> lightedUpWaypoints;
	private final static double offsetNode = GraphNode.SIZE + 2;

	public Graph() {
		super();
		this.setBackground(
				new Background(new BackgroundFill(new Color(0, 0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));
		nodes = new HashMap<>();
		sections = new HashMap<>();
		lightedUpRoads = new LinkedList<>();
		lightedUpWaypoints = new LinkedList<>();
	}

	/**
	 * Set map and build the graph with new waypoints and sections references.
	 * 
	 * @param map
	 *            The source map to use.
	 */
	public void setMap(Map map) {

		this.map = map;
		if (map != null) {
			clearAllContainers();
			extractSectionFromMap();
			extractPointFromMap();
		}
	}

	/**
	 * Color path and waypoint of a round on the graph.
	 * 
	 * @param round
	 *            The path to display
	 */
	public void displayRound(Round round) {
		
		lightDownWaypoint();
		lightDownRoads();
		
		lightUpWaypoint(round);
		lightUpRoads(round);
	}

	// PRIVATE
	// ------------------------------------------------------------------------------------
	/**
	 * Create representations and keep the track of waypoints of the map.
	 */
	private void extractPointFromMap() {
		for (Entry<Integer, Waypoint> entry : map.getWaypoints().entrySet()) {
			Waypoint waypoint = entry.getValue();
			addWaypointDisplay(waypoint);
		}
	}

	/**
	 * Create and keep track of a graphic node which represent a waypoint.
	 * 
	 * @param waypoint
	 *            The source waypoint to represent
	 */
	private void addWaypointDisplay(Waypoint waypoint) {

		GraphNode node = new GraphNode(waypoint);
		node.relocate(waypoint.getXCoord(), waypoint.getYCoord());
		this.getChildren().add(node);
		nodes.put(waypoint.getId(), node);
	}

	/**
	 * Create representations keep track of sections of the map.
	 */
	private void extractSectionFromMap() {
		for (Entry<Integer, TreeMap<Integer, Section>> source : map.getSections().entrySet()) {

			// On r�cup�re le point source
			TreeMap<Integer, Section> waypoint = source.getValue();

			// On va maintenant chercher les destinations
			for (Entry<Integer, Section> sectionFromWaypoint : waypoint.entrySet()) {

				Section section = sectionFromWaypoint.getValue();
				addSectionDisplay(section);
			}
		}
	}

	/**
	 * Create and keep track of a line which represent a section.
	 * 
	 * @param section
	 *            The section to display
	 */
	private void addSectionDisplay(Section section) {
		Line line = new Line();
		line.setStrokeWidth(2);
		// We add GraphNode.SIZE + 2 (thickness of the border) because the icon
		// polygon don't display on its center.
		line.setStartX(section.getOrigin().getXCoord() + offsetNode);
		line.setStartY(section.getOrigin().getYCoord() + offsetNode);
		line.setEndX(section.getDestination().getXCoord() + offsetNode);
		line.setEndY(section.getDestination().getYCoord() + offsetNode);

		this.getChildren().add(line);
		sections.put(getSectionKey(section.getOrigin(), section.getDestination()), line);
	}

	/**
	 * Create a specific key to identify a pair origin:destination
	 * 
	 * @param origin
	 *            Origin waypoint
	 * @param destination
	 *            Destination waypoint
	 * @return specific key for the association origin:destination
	 */
	private String getSectionKey(Waypoint origin, Waypoint destination) {

		int value_a = origin.getId();
		int value_b = destination.getId();

		if (value_a < value_b) {
			return value_a + ":" + value_b;
		} else {
			return value_b + ":" + value_a;
		}
	}

	/**
	 * Remove track of all nodes and sections from the differents containers.
	 */
	private void clearAllContainers() {
		nodes.clear();
		sections.clear();
		lightedUpRoads.clear();
		lightedUpWaypoints.clear();
		this.getChildren().clear();
	}

	/**
	 * Color paypoint of a round
	 * 
	 * @param round
	 */
	private void lightUpWaypoint(Round round) {
		
		ArrayList<Integer> allPoints = new ArrayList<>();
		List<Section> points = round.getRoute(0);
		for (Section section : points) {
			allPoints.add(section.getOrigin().getId());
		}
		lightedUpWaypoints.addAll(allPoints);
		
		ArrayList<Integer> deliveryPoints = new ArrayList<>();
		List<DeliveryTime> deliveries = round.getRoundTimeOrder(0);
		for (DeliveryTime delivery : deliveries) {
			deliveryPoints.add(delivery.getCheckpoint().getId());
		}
		
		allPoints.removeAll(deliveries);
		
		// Paint the points !
		for(Integer id : allPoints) {
			nodes.get(id).setState(GraphNode.State.WAYPOINT);
		}
		
		for(Integer id : deliveryPoints) {
			nodes.get(id).setState(GraphNode.State.DELIVERYPOINT);
		}

		// Paint the warehouse differently
		int id = points.get(0).getOrigin().getId();
		nodes.get(id).setState(GraphNode.State.WAREHOUSE);
	}

	/**
	 * Color section of a round
	 * 
	 * @param round
	 *            the round to display
	 */
	private void lightUpRoads(Round round) {

		List<Section> roads = round.getRoute(0);

		for (Section line : roads) {
			String key = getSectionKey(line.getOrigin(), line.getDestination());
			sections.get(key).setStroke(Color.RED);
			lightedUpRoads.add(key);
		}
	}

	/**
	 * Light down the points of the previous round
	 */
	private void lightDownWaypoint() {
		for (Integer id : lightedUpWaypoints) {
			nodes.get(id).setState(State.NORMAL);
		}
		lightedUpWaypoints.clear();

	}

	/**
	 * Light down the sections of the previous roads
	 */
	private void lightDownRoads() {
		for (String key : lightedUpRoads) {
			sections.get(key).setStroke(Color.BLACK);
		}
		lightedUpRoads.clear();
	}

}
