package view;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import model.Checkpoint;
import model.DeliveryTime;
import model.Map;
import model.Round;

/**
 * Corresponds to the ListView on the right containing the deliveries of a computed round.
 * Contains the ListView in question and methods to fill this ListView when the round has been computed.
 * When a new round is computed, it is not necessary to create a new insytance of this class,
 * calling the 'createDeliveriesList' method is sufficient.
 */
public class DeliveriesListView {
	private ListView<String> deliveryList;
	private AnchorPane deliveryPane;
	
	
	public DeliveriesListView(AnchorPane deliveryPane) {
        deliveryList = new ListView<String>();
        this.deliveryPane = deliveryPane;
        
    	AnchorPane.setTopAnchor(deliveryList, 0d);
    	AnchorPane.setBottomAnchor(deliveryList, 0d);
    	AnchorPane.setRightAnchor(deliveryList, 0d);
    	AnchorPane.setLeftAnchor(deliveryList, 0d);
    	this.deliveryPane.getChildren().add(deliveryList);
	}
    
	/**
	 * Extracts a delivery's information and returns the text to display in the ListView
	 * @param c
	 * 		Delivery's checkpoint
	 * @return
	 * 		Text to display directly in a ListView's cell
	 */
    public static String deliveryToText(Checkpoint c) {
    	String text = "Adresse : " + c.getAssociatedWaypoint().getId() + "\n";
    	
    	int hours = c.getDuration() / 3600;
    	int minutes = (c.getDuration() % 3600) / 60;
    	String duration = "";
    	if (hours < 10) {
    		duration = "0";
    	}
    	duration += Integer.toString(hours) + "h";
    	if (minutes < 10) {
    		duration += "0";
    	}
    	duration += Integer.toString(minutes);
    	text += "Durée : " + duration;
    	
    	return text;
    }
	
	/**
	 * Fill the ListView with deliveries' information in the chronological order.
	 * This method can directly be called when the round changes,
	 * there is no need to create a new DeliveriesListView instance.
	 * @param round
	 * 		Computed round
	 * @param map
	 * 		City's map
	 */
	public void createDeliveriesList(Round round, Map map) {
    	ObservableList<String> deliveriesTexts = FXCollections.observableArrayList();

    	List<DeliveryTime> deliveryTimes = round.getArrivalTimes();
    	for (int i = 0; i < deliveryTimes.size() - 1; i++) {
    		DeliveryTime dt = deliveryTimes.get(i);
    		deliveriesTexts.add(DeliveriesListView.deliveryToText(dt.getCheckpoint()));
    	}
    	deliveryList.setItems(deliveriesTexts);
    }
}
