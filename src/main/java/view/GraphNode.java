package view;

import java.util.Date;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import model.Waypoint;

/**
 * Graphic widget to display a waypoint or checkpoint
 */
public class GraphNode extends Group {

	public enum State {
		NORMAL, WAYPOINT, DELIVERYPOINT, WAREHOUSE
	}

	public final static double SIZE = 6.0;
	public final static double OFFSET_TOOLTIP = 20.;

	private final static Color NORMALCOLOR = new Color(0.4, 0.4, 0.4, 1);
	private final static Color WAYPOINTCOLOR = new Color(0.8, 0.2, 0.2, 1);
	private final static Color DELIVERYPOINTCOLOR = new Color(0.2, 0.8, 0, 1);
	private final static Color WAREHOUSECOLOR = new Color(0, 0, 1, 1);
	
	private Polygon icon;
	private InfoBox infobox;
	private State state;

	public GraphNode(Waypoint data) {
		super();

		setupIcon();
		setupInfobox();
		setupMouseListener();
		setInformations(data);

		state = State.NORMAL;
	}

	public void setState(State state) {
		this.state = state;
		setStateColor();
		
		if (state == State.WAREHOUSE) {
			infobox.addWarehouseText();
		}
	}
	
	public State getState() {
		return state;
	}
	
	/**
	 * Adds a line in the tooltip saying the departure time from this location
	 * @param departureTime
	 * 		Departure time from this location
	 */
	public void addDepartureTime(Date departureTime) {
		infobox.addDepartureTime(departureTime);
	}
	
	/**
	 * Adds a line in the tooltip saying the arrival time to this location
	 * @param arrivalTime
	 * 		Arrival time to this location
	 */
	public void addArrivalTime(Date arrivalTime) {
		infobox.addArrivalTime(arrivalTime);
	}

	public void lightUp() {
		icon.setStroke(Graph.BORDERLIGHTEDCOLOR);
	}

	public void lightDown() {
		icon.setStroke(Graph.BORDERNORMALCOLOR);
	}

	// PRIVATE ----------------------------------------------------------------
	private void setStateColor() {
		switch (state) {
		case NORMAL:
			icon.setFill(NORMALCOLOR);
			break;
		case WAYPOINT:
			icon.setFill(WAYPOINTCOLOR);
			break;
		case DELIVERYPOINT:
			icon.setFill(DELIVERYPOINTCOLOR);
			break;
		case WAREHOUSE:
			icon.setFill(WAREHOUSECOLOR);
			break;
		}

	}

	private void setInformations(Waypoint data) {

		infobox.extractDataFromPoint(data);
	}

	private void setupMouseListener() {
		this.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent t) {
				infobox.setVisible(true);
				icon.setFill(Color.GOLDENROD);
				toFront();
			}
		});

		this.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent t) {
				infobox.setVisible(false);
				setStateColor();
			}
		});
	}

	private void setupIcon() {
		icon = new Polygon();
		icon.setStroke(new Color(0.1, 0.1, 0.1, 1));
		icon.setStrokeWidth(2);
		icon.setFill(NORMALCOLOR);
		icon.getPoints().addAll(new Double[] { -SIZE, 0., 0., SIZE, SIZE, 0., 0., -SIZE });
		this.getChildren().add(icon);
	}

	private void setupInfobox() {
		infobox = new InfoBox();
		infobox.setVisible(false);
		infobox.relocate(OFFSET_TOOLTIP, OFFSET_TOOLTIP);
		this.getChildren().add(infobox);
	}

}
