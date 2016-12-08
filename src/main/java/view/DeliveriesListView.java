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
import model.Checkpoint;
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
	private static ListView<String> deliveryList;
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
	 * Extracts a delivery's information and returns the text to display in the
	 * ListView
	 * 
	 * @param c
	 *            Delivery's checkpoint
	 * @return Text to display directly in a ListView's cell
	 */
	public static String deliveryToText(Checkpoint c) {
		String text = "Adresse : " + c.getId() + "\n";

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
	
	public static void setObservableListInDeliveryList(ObservableList<String> deliveriesTexts){
		deliveryList.setItems(deliveriesTexts);
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

		setObservableListInDeliveryList(deliveriesTexts);
		addItemAction();
	}

	private String millisToText(long millis) {
		return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), 
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
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
			text += "ouvert de "
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
