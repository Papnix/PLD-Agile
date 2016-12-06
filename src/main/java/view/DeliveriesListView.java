package view;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

		String text = "";
		try {
			List<DeliveryTime> rdt = round.getRoundTimeOrder(0);

			text = "Adresse : " + rdt.get(0).getCheckpoint().getAssociatedWaypoint().getId() + "\n";
			text += "Heure de debut de tournée : "
					+ new SimpleDateFormat("HH:mm").format(rdt.get(0).getDepartureTime().getTime());
			deliveriesTexts.add(text);

			for (int i = 1; i < rdt.size()-1; i++) {
				text = "Adresse : " + rdt.get(i).getCheckpoint().getAssociatedWaypoint().getId() + "\n";
				if (rdt.get(i).getCheckpoint().getTimeRangeStart() != null
						&& rdt.get(i).getCheckpoint().getTimeRangeEnd() != null) {
					text += "ouvert de "
							+ new SimpleDateFormat("HH:mm").format(rdt.get(i).getCheckpoint().getTimeRangeStart().getTime())
							+ " à "
							+ new SimpleDateFormat("HH:mm").format(rdt.get(i).getCheckpoint().getTimeRangeEnd().getTime())
							+ "\n";
				}
				if (rdt.get(i).getWaitingTime() != 0) {
					text += "Temps d'attente : " + millisToText(rdt.get(i).getWaitingTime()) + "\n";
				} else {
					text += "aucune attente \n";
				}

				text += "Heure d'arrivée : " + new SimpleDateFormat("HH:mm").format(rdt.get(i).getArrivalTime().getTime())
						+ "		";
				text += "Heure de depart : "
						+ new SimpleDateFormat("HH:mm").format(rdt.get(i).getDepartureTime().getTime());

				deliveriesTexts.add(text);
			}

			text = "Adresse : " + rdt.get(rdt.size()-1).getCheckpoint().getAssociatedWaypoint().getId() + "\n";
			text += "Heure de fin de tournée : "
					+ new SimpleDateFormat("HH:mm").format(rdt.get(rdt.size()-1).getArrivalTime().getTime());
			deliveriesTexts.add(text);
			deliveryList.setItems(deliveriesTexts);
		} catch (Exception e) {
			errorHandler.impossibleRound(round, deliveriesTexts);
			deliveryList.setItems(deliveriesTexts);
		}

		
	}
	
	private String millisToText(long millis){
		return String.format("%02d:%02d", 
				TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis) -  
				TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
				TimeUnit.MILLISECONDS.toSeconds(millis) - 
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}

	public void clear() {
		deliveryList.getItems().clear();
		deliveryPane.getChildren().remove(deliveryList);
	}
}
