package view;

import java.io.File;
import java.net.URL;

import java.util.ResourceBundle;

import controller.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * MainWindowController : the main window of the application
 */
public class MainWindowController implements Initializable {
	private boolean firstDeliveryLoad;
	private DeliveriesListView deliveriesListView;

	private String lastFolderExplored;
	private Graph mapDisplayer;

	private Controller controller;

	@FXML
	private MenuItem menuLoadDelivery;
	@FXML
	private MenuItem menuLoadMap;
	@FXML
	private MenuItem menuAddDelivery;
	@FXML
	private MenuItem menuRemoveDelivery;
	@FXML
	private MenuItem menuModifyDelivery;
	@FXML
	private Button loadDeliveryButton;
	@FXML
	private Button loadMapButton;
	@FXML
	private AnchorPane deliveryPane;
	@FXML
	private ScrollPane mapPane;

	/**
	 * Constructor of the main window, initializes links with fxml file for GUI components.
	 */
	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

		controller = new Controller(this);

		initializeMenu();

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

	

	public void updateAfterLoadMap() {
		loadMapButton.setVisible(false);
		menuLoadDelivery.setDisable(false);
		loadDeliveryButton.setDisable(false);
		loadDeliveryButton.setText("Charger demande de livraisons");
		clearPreviousRound();

		mapDisplayer.setMap(controller.getCurrentMap());
		mapDisplayer.setVisible(true);
	}

	public void updateAfterLoadDelivery() {
		
		// Crée la ListView à droite si c'est le premier chargement de
		// demande de livraisons
		if (firstDeliveryLoad) {
			deliveriesListView = new DeliveriesListView(deliveryPane, mapDisplayer);
			firstDeliveryLoad = false;
		}

		// Met à jour l'interface graphique
		deliveriesListView.createDeliveriesList(controller.getCurrentRound(), controller.getCurrentMap());
		loadDeliveryButton.setVisible(false);
		menuAddDelivery.setVisible(true);
		menuModifyDelivery.setVisible(true);
		menuModifyDelivery.setVisible(true);
		mapDisplayer.setRound(controller.getCurrentRound());
	}
	
	/**
	 * Loads a map from an xml source chosen by the user in an explorer.
	 */
	private void handleLoadMap() {
		
		// Demande à l'utilisateur de sélectionner un fichier à charger
		File mapFile = getFileFromExplorer();
		if (mapFile != null) {
			controller.loadMap(mapFile.getAbsolutePath().toString());
		}
	}
	
	/**
	 * Loads a delivery request from an xml source chosen by the user in an explorer
	 */
	private void handleLoadDelivery() {
		
		// Demande à l'utilisateur de sélectionner un fichier à charger
		File deliveryRequestFile = getFileFromExplorer();
		if(deliveryRequestFile != null) {
			controller.loadDeliveryRequest(deliveryRequestFile.getAbsolutePath().toString());
		}
	}

	/**
	 * Removes the round from the list view and clear its display.
	 */
	private void clearPreviousRound() {
		
		// On enlève la tournée affichée
		controller.clearRound();

		if (firstDeliveryLoad == false) {
			deliveriesListView.clear();
			loadDeliveryButton.setVisible(true);
			firstDeliveryLoad = true;
		}
	}

	/**
	 * Opens an explorer to select a file and return it.
	 **/
	private File getFileFromExplorer() {
		FileChooser explorer = new FileChooser();
		explorer.setTitle("Selectionner un fichier xml");
		explorer.getExtensionFilters().add(new ExtensionFilter("Fichiers XML", "*.xml"));

		if (lastFolderExplored != null) {
			try {
				explorer.setInitialDirectory(new File(lastFolderExplored));
			} catch (Exception e) {
			}
		}

		File file = explorer.showOpenDialog(null);

		if (file != null) {
			lastFolderExplored = file.getParent();
		}

		return file;
	}


	/**
	 * Initializes the map displayer system
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
	
	
	private void initializeMenu() {
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
		
		assert menuAddDelivery != null : "fx:id=\"menuAddDelivery\" was not injected: check your FXML file"
				+ " 'view.fxml'.";
		menuAddDelivery.setOnAction((event) -> {
			System.out.println("not implemented");
		});
		menuAddDelivery.setDisable(true);
		
		assert menuRemoveDelivery != null : "fx:id=\"menuRemoveDelivery\" was not injected: check your FXML file"
				+ " 'view.fxml'.";
		menuRemoveDelivery.setOnAction((event) -> {
			System.out.println("not implemented");
		});
		menuRemoveDelivery.setDisable(true);
		
		assert menuModifyDelivery != null : "fx:id=\"menuModifyDelivery\" was not injected: check your FXML file"
				+ " 'view.fxml'.";
		menuModifyDelivery.setOnAction((event) -> {
			System.out.println("not implemented");
		});
		menuModifyDelivery.setDisable(true);
	}
}