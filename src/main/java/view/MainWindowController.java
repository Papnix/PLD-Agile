package view;

import java.io.File;
import java.net.URL;

import java.util.ResourceBundle;

import controller.Controller;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;


/**
 * MainWindowController : the main window of the application
 */
public class MainWindowController implements Initializable {
	Alert waitingDialog;
	
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
		waitingDialog = new Alert(AlertType.INFORMATION);
		waitingDialog.setTitle("Calcul en cours");
		waitingDialog.setHeaderText("Patientez...");
		waitingDialog.setContentText("La tourn�e est en cours de calcul.");
		
		// On cache tous les boutons pour emp�cher l'utilisateur de fermer la fen�tre
		for (ButtonType bt : waitingDialog.getDialogPane().getButtonTypes()) {
			Button b = (Button)(waitingDialog.getDialogPane().lookupButton(bt));
			b.setVisible(false);
		}
		
		firstDeliveryLoad = true;
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
		
		deliveriesListView = new DeliveriesListView(controller);
		deliveryPane.getChildren().add(deliveriesListView);
		deliveriesListView.setVisible(false);
		

		setupGraphDisplayer();
	}

	

	public void showWaitingDialog() {
		waitingDialog.show();
	}
	
	public void closeWaitingDialog() {
		waitingDialog.close();
	}
	
	public void updateAfterLoadMap() {
		loadMapButton.setVisible(false);
		menuLoadDelivery.setDisable(false);
		menuAddDelivery.setDisable(true);
		menuRemoveDelivery.setDisable(true);
		menuModifyDelivery.setDisable(true);
		loadDeliveryButton.setDisable(false);
		loadDeliveryButton.setText("Charger demande de livraisons");
		clearPreviousRound();

		mapDisplayer.setMap(controller.getCurrentMap());
		mapDisplayer.setVisible(true);
	}

	public void updateAfterLoadNewRound() {

		
		// Cr�e la ListView � droite si c'est le premier chargement de demande de livraisons
		if (firstDeliveryLoad) {
			deliveriesListView.setVisible(true);
			firstDeliveryLoad = false;
		}

		// Met � jour l'interface graphique
		deliveriesListView.loadDeliveriesList();
		loadDeliveryButton.setVisible(false);
		menuAddDelivery.setDisable(false);
		menuRemoveDelivery.setDisable(false);
		menuModifyDelivery.setDisable(false);
		mapDisplayer.setRound(controller.getCurrentRound());
	}
	
	public Graph getMapDisplayer() {
		return mapDisplayer;
	}
	
	/**
	 * Loads a map from an xml source chosen by the user in an explorer.
	 */
	private void handleLoadMap() {
		
		// Demande � l'utilisateur de s�lectionner un fichier � charger
		File mapFile = getFileFromExplorer();
		if (mapFile != null) {
			controller.loadMap(mapFile.getAbsolutePath().toString());
		}
	}
	
	/**
	 * Loads a delivery request from an xml source chosen by the user in an explorer
	 */
	private void handleLoadDelivery() {
		
		// Demande � l'utilisateur de s�lectionner un fichier � charger
		File deliveryRequestFile = getFileFromExplorer();
		if(deliveryRequestFile != null) {
			
			if (lastFolderExplored != null) {
				lastFolderExplored = deliveryRequestFile.getParent();
			}

			controller.loadDeliveryRequest(deliveryRequestFile.getAbsolutePath().toString());
		}
	}

	/**
	 * Removes the round from the list view and clear its display.
	 */
	private void clearPreviousRound() {
		
		// On enl�ve la tourn�e affich�e
		controller.clearRound();

		if (firstDeliveryLoad == false) {
			deliveriesListView.clear();
			deliveriesListView.setVisible(false);
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
			AdditionDeliveryDialog dialog = new AdditionDeliveryDialog(controller);
			dialog.show();
		});
		menuAddDelivery.setDisable(true);
		
		assert menuRemoveDelivery != null : "fx:id=\"menuRemoveDelivery\" was not injected: check your FXML file"
				+ " 'view.fxml'.";
		menuRemoveDelivery.setOnAction((event) -> {
			SuppressionDeliveryDialog dialog = new SuppressionDeliveryDialog(controller);
			dialog.show();
		});
		menuRemoveDelivery.setDisable(true);
		
		assert menuModifyDelivery != null : "fx:id=\"menuModifyDelivery\" was not injected: check your FXML file"
				+ " 'view.fxml'.";
		menuModifyDelivery.setOnAction((event) -> {
			ModifyDateDeliveryDialog dialog = new ModifyDateDeliveryDialog(controller);
			dialog.show();
		});
		menuModifyDelivery.setDisable(true);
	}
}