package view;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import controller.Controller;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import model.DeliveryTime;

/**
 * Corresponds to the ListView on the right containing the deliveries of a
 * computed round. Contains the ListView in question and methods to fill this
 * ListView when the round has been computed. When a new round is computed, it
 * is not necessary to create a new insytance of this class, calling the
 * 'createDeliveriesList' method is sufficient.
 */
public class DeliveriesListView extends VBox {

	private ComboBox<String> roundCombo;

	/**
	 * ListView that is displayed
	 */
	private ListView<String> deliveryList;
	
	/**
	 * List of the delivery points ID, in the chronological order
	 */
	private List<Integer> idDeliveryPoints;
	
	private Controller controller;

	/**
	 * Only one constructor of the widget
	 * @param controller A reference to the controller of the application.
	 */
	public DeliveriesListView(Controller controller) {
		super();

		roundCombo = new ComboBox<String>();
		deliveryList = new ListView<String>();
		idDeliveryPoints = new ArrayList<>();
		this.controller = controller;

		AnchorPane.setTopAnchor(this, 0d);
		AnchorPane.setBottomAnchor(this, 0d);
		AnchorPane.setRightAnchor(this, 0d);
		AnchorPane.setLeftAnchor(this, 0d);

		roundCombo.setMaxWidth(Double.MAX_VALUE);
		roundCombo.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> ov, String oldValue, String newValue) {

				int index = roundCombo.getSelectionModel().getSelectedIndex();
				if (index >= 0 && index < controller.getCurrentRound().getRoundTimeOrders().size()) {
					controller.setCurrentTimeOrder(index);
					setupSelectedDeliveryOrder(controller.getCurrentRoundTimeOrder());
				}
			}
		});
		deliveryList.setMaxHeight(Double.MAX_VALUE);
		addItemAction();

		this.setPadding(new Insets(10, 5, 10, 5));
		this.setSpacing(10);
		DeliveriesListView.setVgrow(deliveryList, Priority.ALWAYS);

		this.getChildren().add(new Label("Liste des différents itinéraire possibles :"));
		this.getChildren().add(roundCombo);
		this.getChildren().add(new Label("Liste des points de livraison : "));
		this.getChildren().add(deliveryList);
		
		Graph.setDeliveriesListView(this);
	}

	/**
	 * Fill the ListView with deliveries' information in the chronological
	 * order. This method can directly be called when the round changes, there
	 * is no need to create a new DeliveriesListView instance.
	 * 
	 */
	public void loadDeliveriesList() {

		deliveryList.getItems().clear();
		idDeliveryPoints.clear();
		roundCombo.getItems().clear();

		for (int index = 0; index < controller.getCurrentRound().getRoundTimeOrders().size(); ++index) {
			roundCombo.getItems().add("Possibilité de livraison n : " + (index + 1));
		}
		roundCombo.getSelectionModel().selectFirst();

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
		roundCombo.getItems().clear();
		deliveryList.getItems().clear();
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

					if (idCheckpoint != -1) {
						if (deliveryList.getItems().indexOf(newValue) == deliveryList.getItems().size() - 1) {
							// Allume toute la map, gère le retour à l'entrepot.
							controller.getWindow().getMapDisplayer().lightUpPath(-1);
						} else {
							controller.getWindow().getMapDisplayer().lightUpPath(idCheckpoint);
						}

					} else {
						controller.getWindow().getMapDisplayer().lightDownPath();
					}
				}
				else {
					controller.getWindow().getMapDisplayer().lightDownPath();
				}
			}
		});
	}
	
	/**
	 * Create a message with informations about a delivery point and add it to the list of texts to display.
	 * @param deliveriesTexts The list of texts diplayed by the widget.
	 * @param delivery The delivery with which will be created the message.
	 */
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

		text += "Heure d'arrivée : " + new SimpleDateFormat("HH:mm").format(delivery.getArrivalTime().getTime())
				+ "		";
		text += "Heure de départ : " + new SimpleDateFormat("HH:mm").format(delivery.getDepartureTime().getTime());

		deliveriesTexts.add(text);
	}

	/**
	 * Create a message with informations on the start of the round add it to the list of texts to display.
	 * @param deliveriesTexts The list of texts diplayed by the widget.
	 * @param delivery The delivery with which will be created the message.
	 */
	private void displayStartRoundMessage(ObservableList<String> deliveriesTexts, List<DeliveryTime> roundChosen) {

		String text = "Adresse : " + roundChosen.get(0).getCheckpoint().getAssociatedWaypoint().getId() + "\n";
		text += "Heure de debut de tournée : "
				+ new SimpleDateFormat("HH:mm").format(roundChosen.get(0).getDepartureTime().getTime());
		deliveriesTexts.add(text);

	}

	/**
	 * Create a message with informations on the end of the round add it to the list of texts to display.
	 * @param deliveriesTexts The list of texts diplayed by the widget.
	 * @param delivery The delivery with which will be created the message.
	 */
	private void displayEndRoundMessage(ObservableList<String> deliveriesTexts, List<DeliveryTime> roundChosen) {
		String text = "Adresse : "
				+ roundChosen.get(roundChosen.size() - 1).getCheckpoint().getAssociatedWaypoint().getId() + "\n";
		text += "Heure de fin de tournée : " + new SimpleDateFormat("HH:mm")
				.format(roundChosen.get(roundChosen.size() - 1).getArrivalTime().getTime());
		deliveriesTexts.add(text);
	}

	/**
	 * Load a deliveryOrder in the differents containers to display it and allow usability;
	 * @param deliveryOrder
	 */
	private void setupSelectedDeliveryOrder(List<DeliveryTime> deliveryOrder) {

		idDeliveryPoints.clear();
		deliveryList.getItems().clear();

		ObservableList<String> deliveriesTexts = FXCollections.observableArrayList();
		displayStartRoundMessage(deliveriesTexts, deliveryOrder);
		idDeliveryPoints.add(deliveryOrder.get(0).getCheckpoint().getId());

		for (int i = 1; i < deliveryOrder.size() - 1; i++) {
			displayCheckpointRoundMessage(deliveriesTexts, deliveryOrder.get(i));
			idDeliveryPoints.add(deliveryOrder.get(i).getCheckpoint().getId());
		}

		displayEndRoundMessage(deliveriesTexts, deliveryOrder);
		idDeliveryPoints.add(deliveryOrder.get(0).getCheckpoint().getId());

		deliveryList.setItems(deliveriesTexts);
	}

	/**
	 * Convert a time in millisecond into a text to display
	 * 
	 * @param millis
	 * @return a string with the time o display
	 */
	private String millisToText(long millis) {
		return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}
}
