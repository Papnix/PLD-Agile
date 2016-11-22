package view;

import java.util.Map.Entry;
import java.util.TreeMap;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Map;
import model.Section;
import model.Waypoint;

public class GraphBuilder {

	private final static Color WAYPOINT_STROKE_COLOR = new Color(0,0,0, 1);
	private final static Color WAYPOINT_FILL_COLOR = new Color(41f / 255f, 128f / 255f, 185f / 255f, 1);
	private final static Color PATH_COLOR = new Color(20f / 255f, 20f / 255f, 20f / 255f, 1);
	// private final static Color CHOSEN_PATH_COLOR = new Color(0,255,0,255);
	private final static int WAYPOINT_SIZE = 5;
	private final static double OFFSET_ID = 10;

	public void drawMap(Canvas canvas) {

		GraphicsContext graphContext = canvas.getGraphicsContext2D();

		graphContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		getSectionsMapAndDraw(graphContext);
		getWaypointsMapAndDraw(graphContext);

	}

	private void drawWaypoint(Waypoint waypoint, GraphicsContext graph) {

		double sourceX = waypoint.getxCoord();
		double sourceY = waypoint.getyCoord();
		
		double[] pointsX = { sourceX - WAYPOINT_SIZE, sourceX, sourceX + WAYPOINT_SIZE, sourceX };
		double[] pointsY = { sourceY, sourceY + WAYPOINT_SIZE, sourceY, sourceY - WAYPOINT_SIZE };

		graph.setFill(WAYPOINT_FILL_COLOR);
		graph.setLineWidth(2);
		graph.fillPolygon(pointsX, pointsY, 4);
		graph.setStroke(WAYPOINT_STROKE_COLOR);
		graph.strokePolygon(pointsX, pointsY, 4);
		
		graph.setStroke(Color.WHITESMOKE);
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

	private void getWaypointsMapAndDraw(GraphicsContext graph) {
		
		for (Entry<Integer, Waypoint> entry : Map.getInstance().getWaypoints().entrySet()) {

			Waypoint waypoint = entry.getValue();

			drawWaypoint(waypoint, graph);
		}
	}

	private void getSectionsMapAndDraw(GraphicsContext graph) {
		for (Entry<Integer, TreeMap<Integer, Section>> source : Map.getInstance().getSections().entrySet()) {

			// On récupère le point source
			TreeMap<Integer, Section> waypoint = source.getValue();

			// On va maintenant chercher les destinations
			for (Entry<Integer, Section> sectionFromWaypoint : waypoint.entrySet()) {

				Section section = sectionFromWaypoint.getValue();
		
				drawSection(section, graph);
			}
		}
	}
}
