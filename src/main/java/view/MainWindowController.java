package view;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import java.util.ResourceBundle;

import controller.Roadmap;
import controller.xml.XMLDeserializer;

import java.text.ParseException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import controller.xml.XMLException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import model.DeliveryRequest;
import model.Map;
import model.Round;

/**
 * MainWindowController : the main window of the application
 */
public class MainWindowController implements Initializable {
	private boolean firstDeliveryLoad;
	private DeliveriesListView deliveriesListView;
	private DeliveryRequest deliveryRequest;
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
	private ScrollPane mapPane;

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
    	// Demande à l'utilisateur de sélectionner un fichier à charger
    	File deliveryRequestFile = getFileFromExplorer();
        
		if (deliveryRequestFile != null) {
			DeliveryRequest newDeliveryRequest;
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
				try{
					// Calcul de la tournée
		        	round = new Round(deliveryRequest);
		    		round.computePaths(map);
		    		round.computeRound(map);
				}
				catch(NullPointerException e) {
					displayWarningMessageBox("La demande de livraison ne peut pas être traitée, elle ne semble pas correspondre à la carte actuelle.");
					return;
				}
	    		
	    		// Ecriture de la feuille de route
	    		Roadmap.writeRoadmap(round, map);
	    		
	    		// Crée la ListView à droite si c'est le premier chargement de demande de livraisons
	    		if (firstDeliveryLoad) {
	    			deliveriesListView = new DeliveriesListView(deliveryPane);
	    			firstDeliveryLoad = false;
	    		}
	    		
	    		// Met à jour l'interface graphique
	        	deliveriesListView.createDeliveriesList(round, map);
	        	loadDeliveryButton.setVisible(false);
	        	mapDisplayer.displayRound(round);
	        					
			}
		}
	}

	/**
	 * Remove the round from the list view and clear it's display.
	 */
	private void clearPreviousRound() {
		// On enlève la tournée affichée
		round = null;
		if(firstDeliveryLoad == false) {
			deliveriesListView.clear();
			loadDeliveryButton.setVisible(true);
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
		mapPane.setContent(mapDisplayer);
		AnchorPane.setTopAnchor(mapDisplayer, 0d);
		AnchorPane.setBottomAnchor(mapDisplayer, 0d);
		AnchorPane.setRightAnchor(mapDisplayer, 0d);
		AnchorPane.setLeftAnchor(mapDisplayer, 0d);
		mapDisplayer.setVisible(false);
	}
}