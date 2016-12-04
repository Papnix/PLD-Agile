package view;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import java.text.SimpleDateFormat;

import java.text.ParseException;

import java.util.List;
import java.util.ResourceBundle;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import controller.xml.XMLDeserializer;
import controller.xml.XMLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import model.Checkpoint;
import model.DeliveryRequest;
import model.DeliveryTime;
import model.Map;
import model.Round;
import model.Section;

/**
 * MainWindowController : the main window of the application
 */
public class MainWindowController implements Initializable {
	private boolean firstDeliveryLoad;
	private DeliveryRequest deliveryRequest;
	private ListView<String> deliveryList;

	private Map map;
	private Round round;
	private String lastFolderExplored;

	private Graph mapDisplayer;

	@FXML
	private MenuItem menuLoadDelivery;
	@FXML
	private MenuItem menuLoadMap;
	@FXML
	private Button loadDeliveryButton;
	@FXML
	private Button loadMapButton;
	@FXML
	private AnchorPane deliveryPane;
	@FXML
	private AnchorPane mapPane;

	/**
	 * Constructor of the main window, initialize links with fxml file for GUI
	 * components.
	 */
	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
		assert menuLoadMap != null : "fx:id=\"menuLoadMap\" was not injected: check your FXML file 'view.fxml'.";
		menuLoadMap.setOnAction((event) -> {
			handleLoadMap();
		});

		assert menuLoadDelivery != null : "fx:id=\"menuLoadDelivery\" was not injected: check your FXML file"
				+ " 'view.fxml'.";
		menuLoadDelivery.setOnAction((event) -> {
			handleLoadDelivery();
		});
		menuLoadDelivery.setDisable(true);

		assert loadDeliveryButton != null : "fx:id=\"loadDeliveryButton\" was not injected: check your FXML"
				+ " file 'view.fxml'.";
		loadDeliveryButton.setOnAction((event) -> {
			handleLoadDelivery();
		});
		loadDeliveryButton.setDisable(true);
		loadDeliveryButton.setText("Charger demande de livraisons\n(Vous devez d'abord charger une carte)");

		assert loadMapButton != null : "fx:id=\"loadMapButton\" was not injected: check your FXML file 'view.fxml'.";
		loadMapButton.setOnAction((event) -> {
			handleLoadMap();
		});

		assert deliveryPane != null : "fx:id=\"deliveryPane\" was not injected: check your FXML file 'view.fxml'.";
		assert mapPane != null : "fx:id=\"mapPane\" was not injected: check your FXML file 'view.fxml'.";

		firstDeliveryLoad = true;
		deliveryList = new ListView<String>();

		setupGraphDisplayer();

	}

	/**
	 * Load a map from an xml source chosen by the user in an explorer.
	 */
	private void handleLoadMap() {

		File mapFile = getFileFromExplorer();

		Map newMap;

		if (mapFile != null) {
			try {
				newMap = XMLDeserializer.loadMap(mapFile.getAbsolutePath().toString());
			} catch (XMLException e) {
				displayWarningMessageBox(
						"Oups, il semble que le fichier que vous avez spécifié ne soit pas" + " une carte valide.");
				return;
			} catch (IOException | SAXException | ParserConfigurationException e) {
				e.printStackTrace();
				displayWarningMessageBox("Oups, une erreur non attendue est survenue.");
				return;
			}

			if (newMap != null) {
				map = newMap;
				loadMapButton.setVisible(false);
				menuLoadDelivery.setDisable(false);
				loadDeliveryButton.setDisable(false);
				loadDeliveryButton.setText("Charger demande de livraisons");
				clearPreviousRound();

				mapDisplayer.setMap(newMap);
				mapDisplayer.setVisible(true);
			}
		}
	}

	/**
	 * Load a delivery request from an xml source chosen by the user in an
	 * explorer
	 */
	private void handleLoadDelivery() {

		File deliveryRequestFile = getFileFromExplorer();

		DeliveryRequest newDeliveryRequest;

		if (deliveryRequestFile != null) {
			try {
				newDeliveryRequest = XMLDeserializer
						.loadDeliveryRequest(deliveryRequestFile.getAbsolutePath().toString(), map);
			} catch (XMLException e) {
				displayWarningMessageBox("Oups, il semble que le fichier que vous avez spécifié ne soit pas une"
						+ " demande de livraison valide.");
				return;
			} catch (IOException | SAXException | ParserConfigurationException | ParseException e) {
				e.printStackTrace();
				displayWarningMessageBox("Oups, une erreur non attendue est survenue.");
				return;
			}

			if (newDeliveryRequest != null) {
				deliveryRequest = newDeliveryRequest;
				try {
					round = new Round(deliveryRequest);
					round.computePaths(map);
					round.computeRound(map);
				} catch (NullPointerException e) {
					e.printStackTrace();
					displayWarningMessageBox("La demande de livraison ne peut pas être traitée, elle ne semble pas"
							+ " correspondre à la carte actuelle.");
					return;
				}
				createDeliveriesList(round);
				mapDisplayer.displayRound(round);
			}
		}
	}

	/**
	 * Create the list of delivery from a round
	 * 
	 * @param round
	 *            the round with delivery point to display
	 */
	private void createDeliveriesList(Round round) {
		ObservableList<String> deliveriesTexts = FXCollections.observableArrayList();

		String text = "";
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

		if (firstDeliveryLoad) {
			AnchorPane.setTopAnchor(deliveryList, 0d);
			AnchorPane.setBottomAnchor(deliveryList, 0d);
			AnchorPane.setRightAnchor(deliveryList, 0d);
			AnchorPane.setLeftAnchor(deliveryList, 0d);
			deliveryPane.getChildren().add(deliveryList);
			firstDeliveryLoad = false;
		}
	}
	
	private String millisToText(long timeInMillis){
		long sec = timeInMillis/1000;
		long min = sec % 60;
		long heure = min / 60;
		
		return heure + ":" + min;
	}

	/**
	 * Remove the round from the list view and clear it's display.
	 */
	private void clearPreviousRound() {
		// On enlève la tournée affichée
		round = null;
		//////////////////////////////////////////// graphBuilder.clearCanvas(canvasRound);
		if (firstDeliveryLoad == false) {
			deliveryPane.getChildren().remove(deliveryList);
			firstDeliveryLoad = true;
		}

	}

	/**
	 * Open an explorer to select a file and return it.
	 **/
	private File getFileFromExplorer() {

		FileChooser explorer = new FileChooser();
		explorer.setTitle("Selectionner un fichier xml");

		if (lastFolderExplored != null) {
			try {
				explorer.setInitialDirectory(new File(lastFolderExplored));
			} catch (Exception e) {
			}
		}

		File file = explorer.showOpenDialog(null);

		lastFolderExplored = file.getParent();

		return file;
	}

	/**
	 * Display a dialog to display some informations to users.
	 * 
	 * @param message
	 *            message to display.
	 **/
	public static void displayWarningMessageBox(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Rapport d'erreur");
		alert.setHeaderText("Une erreur est survenue");
		alert.setContentText(message);
		alert.showAndWait();
	}

	/**
	 * Initialize the map displayer system
	 */
	private void setupGraphDisplayer() {
		mapDisplayer = new Graph();
		mapPane.getChildren().add(mapDisplayer);
		AnchorPane.setTopAnchor(mapDisplayer, 0d);
		AnchorPane.setBottomAnchor(mapDisplayer, 0d);
		AnchorPane.setRightAnchor(mapDisplayer, 0d);
		AnchorPane.setLeftAnchor(mapDisplayer, 0d);
		mapDisplayer.setVisible(false);
	}
}