package view;

/*
 * Tooltip to display waypoint information
 * 
 */

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import model.Waypoint;

public class InfoBox extends VBox {
	
	private Text info;
	
	public InfoBox() {
		
		super();
		
		this.setBackground(new Background(new BackgroundFill(new Color(0.2,0.2,0.2,1), new CornerRadii(5), new Insets(-10))));

	}

	public void extractDataFromPoint(Waypoint data) {
		getChildren().clear();
		
		info = new Text("Waypoint ID : " + data.getId());
		info.setStroke(Color.WHITE);
		getChildren().addAll(info);
	}

}
