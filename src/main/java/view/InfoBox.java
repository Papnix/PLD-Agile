package view;

import java.text.SimpleDateFormat;
import java.util.Date;

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
	
	/**
	 * Adds a line saying that this location is a warehouse
	 */
	public void addWarehouseText() {
		info.setText(info.getText() + "\nEntrepôt");
	}
	
	/**
	 * Adds a line saying the departure time from this location
	 * @param departureTime
	 * 		Departure time from this location
	 */
	public void addDepartureTime(Date departureTime) {
		info.setText(info.getText() + "\nHeure de départ : " + new SimpleDateFormat("HH:mm").format(departureTime.getTime()));
	}
	
	/**
	 * Adds a line saying the arrival time to this location
	 * @param arrivalTime
	 * 		Arrival time to this location
	 */
	public void addArrivalTime(Date arrivalTime) {
		info.setText(info.getText() + "\nHeure d'arrivée : " + new SimpleDateFormat("HH:mm").format(arrivalTime.getTime()));
	}
}
