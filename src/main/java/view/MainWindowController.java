package view;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import controller.Roadmap;
import controller.xml.XMLDeserializer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import model.DeliveryRequest;
import model.Map;
import model.Round;


public class MainWindowController implements Initializable{
	private boolean firstDeliveryLoad;
	private DeliveriesListView deliveriesListView;
	private DeliveryRequest deliveryRequest;
	
	private GraphBuilder graphBuilder;
	private Map map;
	
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
    @FXML
    private Canvas canvasMap;
    @FXML
    private Canvas canvasRound;

    
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    	assert menuLoadMap != null : "fx:id=\"menuLoadMap\" was not injected: check your FXML file 'view.fxml'.";
        menuLoadMap.setOnAction((event) -> {
        	handleLoadMap();
        });
        
        assert menuLoadDelivery != null : "fx:id=\"menuLoadDelivery\" was not injected: check your FXML file 'view.fxml'.";
        menuLoadDelivery.setOnAction((event) -> {
        	handleLoadDelivery();
        });
        menuLoadDelivery.setDisable(true);
        
    	assert loadDeliveryButton != null : "fx:id=\"loadDeliveryButton\" was not injected: check your FXML file 'view.fxml'.";
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
        
        assert canvasMap != null : "fx:id=\"canvasMap\" was not injected: check your FXML file 'view.fxml'.";
        
        firstDeliveryLoad = true;
        graphBuilder = new GraphBuilder();
        
        setupCanvasMap(canvasMap);
    }
    
    private void handleLoadDelivery() {
    	// Demande à l'utilisateur de sélectionner un fichier à charger
    	FileChooser filechooser = new FileChooser();
    	File deliveryRequestFile = filechooser.showOpenDialog(null);
        deliveryRequest = new DeliveryRequest();
        
    	if(deliveryRequestFile != null) {
    		// Vérifie qu'il s'agit bien d'un fichier de demande de livraisons
    		try {
    			XMLDeserializer.loadDeliveryRequest(deliveryRequestFile.getAbsolutePath().toString(), map, deliveryRequest);
			} catch (Exception e) {
				e.printStackTrace();
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Rapport d'erreur");
				alert.setHeaderText("Erreur chargement demande de livraison");
				alert.setContentText("Oups, il semble que le fichier que vous avez spécifié ne soit pas une demande de livraisons");

				alert.showAndWait();
				return;
			}

    		// Calcul de la tournée
        	Round round = new Round(deliveryRequest);
    		round.computePaths(map);
    		round.computeRound(map);
    		
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
        	graphBuilder.drawRound(canvasRound, round);
        	setupCanvasRound(canvasRound, round);
    	}
    }

    private void handleLoadMap() {
    	FileChooser filechooser = new FileChooser();
    	File mapFile = filechooser.showOpenDialog(null);
    	
        map = new Map();

    	if(mapFile != null){
    		try {
				XMLDeserializer.loadMap(mapFile.getAbsolutePath().toString(), map);
			} catch (Exception e) {
				e.printStackTrace();
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Rapport d'erreur");
				alert.setHeaderText("Erreur chargement carte");
				alert.setContentText("Oups, il semble que le fichier que vous avez spécifié ne soit pas une carte");

				alert.showAndWait();
				return;
			}
    	
    		loadMapButton.setVisible(false);
    		menuLoadDelivery.setDisable(false);
    		loadDeliveryButton.setDisable(false);
    		loadDeliveryButton.setText("Charger demande de livraisons");
    		graphBuilder.drawMap(canvasMap, map);
    	}
    }
    
	private void setupCanvasMap(Canvas canvas) {
	    	
	    	canvas.widthProperty().bind(mapPane.widthProperty());
	        canvas.heightProperty().bind(mapPane.heightProperty());
	        canvas.widthProperty().addListener(evt -> graphBuilder.drawMap(canvas, map));
	        canvas.heightProperty().addListener(evt -> graphBuilder.drawMap(canvas, map));
	}
	
	private void setupCanvasRound(Canvas canvas, Round round) {
    	
    	canvas.widthProperty().bind(mapPane.widthProperty());
        canvas.heightProperty().bind(mapPane.heightProperty());
        canvas.widthProperty().addListener(evt -> graphBuilder.drawRound(canvas, round));
        canvas.heightProperty().addListener(evt -> graphBuilder.drawRound(canvas, round));
	}
}