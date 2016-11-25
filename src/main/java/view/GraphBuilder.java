package view;

import java.util.Map.Entry;
import java.util.TreeMap;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.DeliveryTime;
import model.Map;
import model.Round;
import model.Section;
import model.Waypoint;

public class GraphBuilder {

	private final static Color WAYPOINT_STROKE_COLOR = new Color(0,0,0, 1);
	private final static Color WAYPOINT_FILL_COLOR = new Color(41f / 255f, 128f / 255f, 185f / 255f, 1);
	private final static Color PATH_COLOR = new Color(20f / 255f, 20f / 255f, 20f / 255f, 1);
	// private final static Color CHOSEN_PATH_COLOR = new Color(0,255,0,255);
	private final static int WAYPOINT_SIZE = 5;
	private final static double OFFSET_ID = 10;

	public void drawMap(Canvas canvas, Map map) {

		GraphicsContext graphContext = canvas.getGraphicsContext2D();

		graphContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		getSectionsMapAndDraw(graphContext, map);
		getWaypointsMapAndDraw(graphContext, map);

	}

	private void drawWaypoint(Waypoint waypoint, GraphicsContext graph, Color color) {

		double sourceX = waypoint.getxCoord();
		double sourceY = waypoint.getyCoord();
		
		double[] pointsX = { sourceX - WAYPOINT_SIZE, sourceX, sourceX + WAYPOINT_SIZE, sourceX };
		double[] pointsY = { sourceY, sourceY + WAYPOINT_SIZE, sourceY, sourceY - WAYPOINT_SIZE };

		graph.setFill(color);
		graph.setLineWidth(2);
		graph.fillPolygon(pointsX, pointsY, 4);
		graph.setStroke(WAYPOINT_STROKE_COLOR);
		graph.strokePolygon(pointsX, pointsY, 4);
		
		graph.setStroke(color);
		graph.strokeText(String.valueOf(waypoint.getId()), sourceX + OFFSET_ID, sourceY - OFFSET_ID);
	}

	private void drawSection(Section section, GraphicsContext graph) {

		double originX = section.getOrigin().getxCoord();
		double originY = section.getOrigin().getyCoord();

		double destinationX = section.getDestination().getxCoord();
		double destinationY = section.getDestination().getyCoord();
		
		graph.setStroke(PATH_COLOR);
		graph.strokeLine(originX, originY, destinationX, destinationY);
	}

	private void getWaypointsMapAndDraw(GraphicsContext graph, Map map) {
		
		for (Entry<Integer, Waypoint> entry : map.getWaypoints().entrySet()) {

			Waypoint waypoint = entry.getValue();

			drawWaypoint(waypoint, graph, Color.WHITESMOKE);
		}
	}

	private void getSectionsMapAndDraw(GraphicsContext graph, Map map) {
		for (Entry<Integer, TreeMap<Integer, Section>> source : map.getSections().entrySet()) {

			// On récupère le point source
			TreeMap<Integer, Section> waypoint = source.getValue();

			// On va maintenant chercher les destinations
			for (Entry<Integer, Section> sectionFromWaypoint : waypoint.entrySet()) {

				Section section = sectionFromWaypoint.getValue();
		
				drawSection(section, graph);
			}
		}
	}
	
	public void drawRound(Canvas canvas, Round round)
	{
		GraphicsContext graphContext = canvas.getGraphicsContext2D();

		graphContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		drawRoundWaypoints(round, graphContext);
	}
	
	private void drawRoundWaypoints(Round round, GraphicsContext graph) {
		for (DeliveryTime dt : round.getArrivalTimes()) {
			drawWaypoint(dt.getCheckpoint().getAssociatedWaypoint(), graph, Color.RED);
		}
	}
}