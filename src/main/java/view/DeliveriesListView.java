package view;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
	private List<Integer> idDeliveryPoints;
	private AnchorPane deliveryPane;
	private Graph graph; 
	
	
	public DeliveriesListView(AnchorPane deliveryPane, Graph graph) {
        deliveryList = new ListView<String>();        
        idDeliveryPoints = new ArrayList<>();
        
        this.deliveryPane = deliveryPane;
        this.graph = graph;
        
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
    		idDeliveryPoints.add(dt.getCheckpoint().getId());
    	}
    	deliveryList.setItems(deliveriesTexts);
    	
    	addItemAction();
    }

	/*
	 * Reset all containers to erase tracks of the previous delivery request.
	 */
	public void clear() {
		deliveryList.getItems().clear();
		deliveryPane.getChildren().remove(deliveryList);
		idDeliveryPoints.clear();
	}
	
	/**
	 * Add the interaction between the map representation and the list of delivery
	 */
	private void addItemAction() {
		
		deliveryList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
	            if(newValue != null) {
	            	int idCheckpoint = idDeliveryPoints.get(deliveryList.getItems().indexOf(newValue));
		        	
		        	if(idCheckpoint != -1) {
		        		graph.lightUpPath(idCheckpoint);
		        	} else {
		        		graph.lightDownPath();
		        	}
	            }        	
	        }
	    });
	}
}
