package view;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import model.DeliveryTime;
import model.Map;
import model.Round;

/**
 * Corresponds to the ListView on the right containing the deliveries of a
 * computed round. Contains the ListView in question and methods to fill this
 * ListView when the round has been computed. When a new round is computed, it
 * is not necessary to create a new insytance of this class, calling the
 * 'createDeliveriesList' method is sufficient.
 */
public class DeliveriesListView {
	/**
	 * ListView that is displayed
	 */
	private ListView<String> deliveryList;
	
	/**
	 * List of the delivery points ID, in the chronological order
	 */
	private List<Integer> idDeliveryPoints;
	
	/**
	 * Pane where the ListView is displayed
	 */
	private AnchorPane deliveryPane;
	
	/**
	 * Graph corresponding to the map of the delivery request
	 */
	private Graph graph;

	/**
	 * Creates a new DeliveriesListView that is empty
	 * @param deliveryPane
	 * 		Pane where the ListView is displayed
	 * @param graph
	 * 		Graph corresponding to the city's map
	 */
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
		
		Graph.setDeliveriesListView(this);
	}

	/**
	 * Fill the ListView with deliveries' information in the chronological
	 * order. This method can directly be called when the round changes, there
	 * is no need to create a new DeliveriesListView instance.
	 * 
	 * @param round
	 *            Computed round
	 * @param map
	 *            City's map
	 */
	public void createDeliveriesList(Round round, Map map) {
		deliveryList.getItems().clear();
		idDeliveryPoints.clear();
		
		ObservableList<String> deliveriesTexts = FXCollections.observableArrayList();
		List<DeliveryTime> roundChosen = round.getRoundTimeOrder(0);

		displayStartRoundMessage(deliveriesTexts, roundChosen);
		idDeliveryPoints.add(roundChosen.get(0).getCheckpoint().getId());

		for (int i = 1; i < roundChosen.size() - 1; i++) {
			displayCheckpointRoundMessage(deliveriesTexts, roundChosen.get(i));
			idDeliveryPoints.add(roundChosen.get(i).getCheckpoint().getId());
		}

		displayEndRoundMessage(deliveriesTexts, roundChosen);
		idDeliveryPoints.add(roundChosen.get(0).getCheckpoint().getId());

		deliveryList.setItems(deliveriesTexts);
		addItemAction();
	}
	
	/**
	 * Selects the item corresponding to the given checkpoint's id
	 * @param idCheckpoint
	 * 		Checkpoint's ID to select
	 */
	public void selectItem(int idCheckpoint) {
		ObservableList<String> items = deliveryList.getItems();
		for (int i = 0; i < idDeliveryPoints.size(); i++) {
			if (items.get(i).contains("Adresse : " + idCheckpoint)) {
				deliveryList.getSelectionModel().select(i);
				break;
			}
		}
	}
	
	/**
	 * Clears the current item's selection
	 */
	public void clearSelectedItems() {
		deliveryList.getSelectionModel().clearSelection();
	}

	/**
	 * Reset all containers to erase tracks of the previous delivery request.
	 */
	public void clear() {
		deliveryList.getItems().clear();
		deliveryPane.getChildren().remove(deliveryList);
		idDeliveryPoints.clear();
	}

	private String millisToText(long millis) {
		return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), 
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}

	/**
	 * Add the interaction between the map representation and the list of
	 * delivery
	 */
	private void addItemAction() {

		deliveryList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue != null) {
					int idCheckpoint = idDeliveryPoints.get(deliveryList.getItems().indexOf(newValue));

					if(deliveryList.getItems().indexOf(newValue) == deliveryList.getItems().size()-1)
					{
						graph.lightUpPath(-1); // Allume toute la map, gère le retour à l'entrepot.
					}
					else if (idCheckpoint != -1) {
						graph.lightUpPath(idCheckpoint);
					}
					else {
						graph.lightDownPath();
					}
				}
			}
		});
	}
	
	private void displayCheckpointRoundMessage(ObservableList<String> deliveriesTexts, DeliveryTime delivery) {
		
		String text = "Adresse : " + delivery.getCheckpoint().getAssociatedWaypoint().getId() + "\n";
		if (delivery.getCheckpoint().getTimeRangeStart() != null
				&& delivery.getCheckpoint().getTimeRangeEnd() != null) {
			text += "Ouvert de "
					+ new SimpleDateFormat("HH:mm")
							.format(delivery.getCheckpoint().getTimeRangeStart().getTime())
					+ " à " + new SimpleDateFormat("HH:mm")
							.format(delivery.getCheckpoint().getTimeRangeEnd().getTime())
					+ "\n";
		}
		text += "Temps d'attente : " + millisToText(delivery.getWaitingTime()) + "\n";
	
		text += "Heure d'arrivée : "
				+ new SimpleDateFormat("HH:mm").format(delivery.getArrivalTime().getTime()) + "		";
		text += "Heure de depart : "
				+ new SimpleDateFormat("HH:mm").format(delivery.getDepartureTime().getTime());
	
		deliveriesTexts.add(text);
	}
	
	private void displayStartRoundMessage(ObservableList<String> deliveriesTexts, List<DeliveryTime> roundChosen) {
		
		String text = "Adresse : " + roundChosen.get(0).getCheckpoint().getAssociatedWaypoint().getId() + "\n";
		text += "Heure de debut de tournée : "
				+ new SimpleDateFormat("HH:mm").format(roundChosen.get(0).getDepartureTime().getTime());
		deliveriesTexts.add(text);
		
	}
	
	private void displayEndRoundMessage(ObservableList<String> deliveriesTexts, List<DeliveryTime> roundChosen) {
		String text = "Adresse : " + roundChosen.get(roundChosen.size() - 1).getCheckpoint().getAssociatedWaypoint().getId()
				+ "\n";
		text += "Heure de fin de tournée : " + new SimpleDateFormat("HH:mm")
				.format(roundChosen.get(roundChosen.size() - 1).getArrivalTime().getTime());
		deliveriesTexts.add(text);
	}
}
