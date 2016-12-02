package view;

import java.util.Map.Entry;
import java.util.List;
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
	private final static Color PATH_COLOR = new Color(20f / 255f, 20f / 255f, 20f / 255f, 1);
	private final static int WAYPOINT_SIZE = 5;
	private final static double OFFSET_ID = 10;

	public void drawMap(Canvas canvas, Map map) {
		
		GraphicsContext graphContext = canvas.getGraphicsContext2D();
		
		graphContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		getSectionsMapAndDraw(graphContext, map);
		getWaypointsMapAndDraw(graphContext, map);

	}
	
	public void drawRound(Canvas canvas, Round round){
		
		GraphicsContext graphContext = canvas.getGraphicsContext2D();

		graphContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		drawRoundSections(round, graphContext);
		drawRoundWaypoints(round, graphContext);
	}
	
	public void clearCanvas(Canvas canvas) {
		GraphicsContext graphContext = canvas.getGraphicsContext2D();
		graphContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	//-- PRIVATE ---------------------------------------------------------------------------------
	
	private void drawWaypoint(Waypoint waypoint, GraphicsContext graph, Color color) {

		double sourceX = waypoint.getXCoord();
		double sourceY = waypoint.getYCoord();
		
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

	private void drawSection(Section section, GraphicsContext graph, Color color) {

		double originX = section.getOrigin().getXCoord();
		double originY = section.getOrigin().getYCoord();

		double destinationX = section.getDestination().getXCoord();
		double destinationY = section.getDestination().getYCoord();
		
		graph.setStroke(color);
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
		
				drawSection(section, graph, PATH_COLOR);
			}
		}
	}
	
		
	private void drawRoundWaypoints(Round round, GraphicsContext graph) {
		
		List<DeliveryTime> deliveryTimes = round.getRoundTimeOrder();
		for (int i = 0; i < deliveryTimes.size(); i++) {
			DeliveryTime dt = deliveryTimes.get(i);
			
			if (i == deliveryTimes.size() - 1) {
				drawWaypoint(dt.getCheckpoint().getAssociatedWaypoint(), graph, Color.BLUE);
			} else {
				drawWaypoint(dt.getCheckpoint().getAssociatedWaypoint(), graph, Color.RED);
			}
		}
	}
	
	private void drawRoundSections(Round round, GraphicsContext graph) {
		List<Section> route = round.getRoute();
		
		for (Section s : route)
		{
			drawSection(s, graph, Color.RED);
		}
	}
}

