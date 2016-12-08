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
import model.Checkpoint;
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
	private ListView<String> deliveryList;
	private List<Integer> idDeliveryPoints;
	private Controller controller;

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
					setupSelectedDeliveryOrder(controller.getCurrentRound().getRoundTimeOrder(index));
				}
			}
		});
		deliveryList.setMaxHeight(Double.MAX_VALUE);
		addItemAction();

		this.setPadding(new Insets(10, 5, 10, 5));
		this.setSpacing(10);
		DeliveriesListView.setVgrow(deliveryList, Priority.ALWAYS);

		this.getChildren().add(new Label("Liste des diff�rents itin�raire possibles :"));
		this.getChildren().add(roundCombo);
		this.getChildren().add(new Label("Liste des points de livraison : "));
		this.getChildren().add(deliveryList);

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
		text += "Dur�e : " + duration;

		return text;
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
	public void loadDeliveriesList() {

		deliveryList.getItems().clear();
		idDeliveryPoints.clear();
		roundCombo.getItems().clear();

		for (int index = 0; index < controller.getCurrentRound().getRoundTimeOrders().size(); ++index) {
			roundCombo.getItems().add("Possibilit� de livraison n : " + (index + 1));
		}
		roundCombo.getSelectionModel().selectFirst();

	}

	/*
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
							// Allume toute la map, g�re le retour � l'entrepot.
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

	private void displayCheckpointRoundMessage(ObservableList<String> deliveriesTexts, DeliveryTime delivery) {

		String text = "Adresse : " + delivery.getCheckpoint().getAssociatedWaypoint().getId() + "\n";
		if (delivery.getCheckpoint().getTimeRangeStart() != null
				&& delivery.getCheckpoint().getTimeRangeEnd() != null) {
			text += "ouvert de "
					+ new SimpleDateFormat("HH:mm").format(delivery.getCheckpoint().getTimeRangeStart().getTime())
					+ " � " + new SimpleDateFormat("HH:mm").format(delivery.getCheckpoint().getTimeRangeEnd().getTime())
					+ "\n";
		}
		text += "Temps d'attente : " + millisToText(delivery.getWaitingTime()) + "\n";

		text += "Heure d'arriv�e : " + new SimpleDateFormat("HH:mm").format(delivery.getArrivalTime().getTime())
				+ "		";
		text += "Heure de depart : " + new SimpleDateFormat("HH:mm").format(delivery.getDepartureTime().getTime());

		deliveriesTexts.add(text);
	}

	private void displayStartRoundMessage(ObservableList<String> deliveriesTexts, List<DeliveryTime> roundChosen) {

		String text = "Adresse : " + roundChosen.get(0).getCheckpoint().getAssociatedWaypoint().getId() + "\n";
		text += "Heure de debut de tourn�e : "
				+ new SimpleDateFormat("HH:mm").format(roundChosen.get(0).getDepartureTime().getTime());
		deliveriesTexts.add(text);

	}

	private void displayEndRoundMessage(ObservableList<String> deliveriesTexts, List<DeliveryTime> roundChosen) {
		String text = "Adresse : "
				+ roundChosen.get(roundChosen.size() - 1).getCheckpoint().getAssociatedWaypoint().getId() + "\n";
		text += "Heure de fin de tourn�e : " + new SimpleDateFormat("HH:mm")
				.format(roundChosen.get(roundChosen.size() - 1).getArrivalTime().getTime());
		deliveriesTexts.add(text);
	}

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
