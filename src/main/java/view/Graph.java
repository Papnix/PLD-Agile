package view;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
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

	public final static Color BORDERNORMALCOLOR = new Color(0.1, 0.1, 0.1, 1);
	public final static Color BORDERLIGHTEDCOLOR = Color.BLANCHEDALMOND;

	private Map map;
	private Round round;

	private HashMap<Integer, GraphNode> nodes;
	private HashMap<String, Line> sections;
	private LinkedList<String> roundRoads;
	private LinkedList<Integer> roundWaypoints;

	private LinkedList<String> lightedRoads;
	private LinkedList<Integer> lightedWaypoints;

	private final static double offsetNode = GraphNode.SIZE + 2;

	public Graph() {
		super();
		this.setBackground(new Background(new BackgroundFill(new Color(0, 0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));
		nodes = new HashMap<>();
		sections = new HashMap<>();
		roundRoads = new LinkedList<>();
		roundWaypoints = new LinkedList<>();
		lightedRoads = new LinkedList<>();
		lightedWaypoints = new LinkedList<>();

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
	public void setRound(Round round) {
		this.round = round;
		clearDisplay();
		lightDownPath();
		clearDisplayWaypoint();
		clearDisplayRoads();

		displayRoundWaypoint(round);
		displayRoundRoads(round);
	}

	/**
	 * Clear all kind of display of roads and specials points.
	 */
	private void clearDisplay() {
		clearDisplayWaypoint();
		clearDisplayRoads();
	}

	/**
	 * Ligh up the path from the warehouse to a certain point.
	 * 
	 * @param idLastPoint
	 *            The point where we want to end the path.
	 */
	public void lightUpPath(int idLastPoint) {
		lightDownPath();
		lightUpWaypoint(idLastPoint);
		lightUpRoads(idLastPoint);
	}

	/**
	 * Remove the display of the path.
	 */
	public void lightDownPath() {
		lightDownWaypoint();
		lightDownRoads();
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
		node.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent t) {
				if (node.getState() == GraphNode.State.DELIVERYPOINT 
						|| node.getState() == GraphNode.State.WAREHOUSE) {

					lightUpPath(waypoint.getId());
				} else {
					lightDownPath();
				}

			}
		});
		this.getChildren().add(node);
		nodes.put(waypoint.getId(), node);
	}

	/**
	 * Create representations keep track of sections of the map.
	 */
	private void extractSectionFromMap() {
		for (Entry<Integer, TreeMap<Integer, Section>> source : map.getSections().entrySet()) {

			// On récupère le point source
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
		line.setStroke(BORDERNORMALCOLOR);
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
		roundRoads.clear();
		roundWaypoints.clear();
		lightedRoads.clear();
		lightedWaypoints.clear();
		this.getChildren().clear();
	}

	/**
	 * Color paypoint of a round
	 * 
	 * @param round
	 */
	private void displayRoundWaypoint(Round round) {

		// On récupère tous les points de la tournée
		ArrayList<Integer> allPoints = new ArrayList<>();
		List<Section> points = round.getRoute(0);
		for (Section section : points) {
			allPoints.add(section.getOrigin().getId());
		}
		roundWaypoints.addAll(allPoints);

		// On récupère les points de livraisons
		ArrayList<Integer> deliveryPoints = new ArrayList<>();
		List<DeliveryTime> deliveries = round.getRoundTimeOrder(0);
		for (DeliveryTime delivery : deliveries) {
			deliveryPoints.add(delivery.getCheckpoint().getId());
		}

		// Cela nous permet d'avoir deux ensemble (les livraions et les points
		// de passage)
		allPoints.removeAll(deliveries);

		// Paint the points !
		for (Integer id : allPoints) {
			nodes.get(id).setState(GraphNode.State.WAYPOINT);
		}

		for (Integer id : deliveryPoints) {
			nodes.get(id).setState(GraphNode.State.DELIVERYPOINT);
		}

		// On paint l'entrepôt
		int id = points.get(0).getOrigin().getId();
		nodes.get(id).setState(GraphNode.State.WAREHOUSE);
	}

	/**
	 * Color section of a round
	 * 
	 * @param round
	 *            the round to display
	 */
	private void displayRoundRoads(Round round) {

		List<Section> roads = round.getRoute(0);

		for (Section line : roads) {
			String key = getSectionKey(line.getOrigin(), line.getDestination());
			sections.get(key).setStroke(Color.RED);
			roundRoads.add(key);
		}
	}

	/**
	 * Light down the points of the previous round
	 */
	private void clearDisplayWaypoint() {
		lightDownWaypoint();
		for (Integer id : roundWaypoints) {
			nodes.get(id).setState(State.NORMAL);
		}
		roundWaypoints.clear();
	}

	/**
	 * Light down the sections of the previous roads
	 */
	private void clearDisplayRoads() {
		lightDownRoads();
		for (String key : roundRoads) {
			sections.get(key).setStroke(BORDERNORMALCOLOR);
		}
		roundRoads.clear();
	}

	/*
	 * Add an enlightment of waypoints of the round.
	 */
	private void lightUpWaypoint(int idLastPoint) {

		// On récupère l'ordres des points à livrer.
		LinkedList<Integer> pointsToDeliver = getIdOfPointsToDeliver(idLastPoint);

		Iterator<Integer> idIterator = roundWaypoints.iterator();
		while (idIterator.hasNext()) {

			int id = idIterator.next();
			nodes.get(id).lightUp();
			lightedWaypoints.add(id);

			if (id == pointsToDeliver.getFirst()) {
				pointsToDeliver.removeFirst();
				if (pointsToDeliver.isEmpty()) {
					break;
				}
			}
		}
	}

	/*
	 * Add an enlightment of roads of the round.
	 */
	private void lightUpRoads(int idLastPoint) {

		// On récupère l'ordres des points à livrer.
		LinkedList<Integer> pointsToDeliver = getIdOfPointsToDeliver(idLastPoint);

		Iterator<String> sectionKey = roundRoads.iterator();
		while (sectionKey.hasNext()) {
			String key = sectionKey.next();

			sections.get(key).setStroke(BORDERLIGHTEDCOLOR);
			lightedRoads.add(key);

			if (Integer.parseInt(key.substring(key.lastIndexOf(':') + 1)) == pointsToDeliver.getFirst()
					|| Integer.parseInt(key.substring(0, key.lastIndexOf(':'))) == pointsToDeliver.getFirst()) {
				pointsToDeliver.removeFirst();
				if (pointsToDeliver.isEmpty()) {
					break;
				}
			}
		}
	}

	/*
	 * Remove the enlightment of waypoint of the round.
	 */
	private void lightDownWaypoint() {
		for (Integer id : lightedWaypoints) {
			nodes.get(id).lightDown();
		}
		lightedWaypoints.clear();
	}

	/*
	 * Remove the enlightment of roads of the round.
	 */
	private void lightDownRoads() {
		for (String key : lightedRoads) {
			sections.get(key).setStroke(Color.RED);
		}
		lightedRoads.clear();
	}

	/**
	 * Return a list of id of the one given in parameter and id of points which
	 * be delivered before this certain point.
	 * 
	 * @param idLastPoint
	 *            The id of the ending point.
	 * @return List of id of points delivered on the path.
	 */
	private LinkedList<Integer> getIdOfPointsToDeliver(int idLastPoint) {
		// On récupère l'ordres des points à livrer.
		LinkedList<Integer> pointsToDeliver = new LinkedList<>();

		List<DeliveryTime> listDeliveries = round.getRoundTimeOrder(0);
		for (int i = 0; i < listDeliveries.size(); i++) {

			int id = listDeliveries.get(i).getCheckpoint().getId();
			pointsToDeliver.add(id);

			if (id == idLastPoint) {
				break;
			}
		}
		return pointsToDeliver;
	}

}
