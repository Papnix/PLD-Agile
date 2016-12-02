package view;

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
		  NORMAL,
		  CHECKPOINT,
		  WAREHOUSE  
	}
	public final static double SIZE = 6.0;
	public final static double OFFSET_TOOLTIP = 20.;
	
	private final static Color normalColor = new Color(0.4,0.4,0.4,1);
	private final static Color checkpointColor = new Color(0.8,0.2,0.2,1);
	private final static Color warehouseColor = new Color(0,0,1,1);
	
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
	
	public void setState( State state) {
		this.state = state;
		setStateColor();
	}
		
	// PRIVATE ----------------------------------------------------------------
	private void setStateColor() {
		switch(state) {
		case NORMAL :
			icon.setFill(normalColor);
			break;
		case CHECKPOINT :
			icon.setFill(checkpointColor);
			break;
		case WAREHOUSE : 
			icon.setFill(warehouseColor);
			break;
		}
		
	}
	
	private void setInformations(Waypoint data) {
		
		infobox.extractDataFromPoint(data);
	}
	
	private void setupMouseListener()
	{
		this.setOnMouseEntered(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent t) {
        		infobox.setVisible(true);
        		icon.setFill(Color.GOLDENROD);
        		toFront();
            }
        });
		
		this.setOnMouseExited(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent t) {
        		infobox.setVisible(false);
        		setStateColor();
            }
        });
	}
	
	private void setupIcon() {
		icon = new Polygon();	
		icon.setStroke(new Color(0.1,0.1,0.1,1));
		icon.setStrokeWidth(2);
		icon.setFill(normalColor);
		icon.getPoints().addAll(new Double []{
				- SIZE , 0.,
					0. , SIZE,
				  SIZE , 0.,
				    0. , - SIZE	
		});
		this.getChildren().add(icon);
	}
	
	private void setupInfobox() {
		infobox = new InfoBox();
		infobox.setVisible(false);
		infobox.relocate(OFFSET_TOOLTIP, OFFSET_TOOLTIP);
		this.getChildren().add(infobox);
	}
		
}
