package view;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import controller.xml.XMLDeserializer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
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


@SuppressWarnings("restriction")
public class MainWindowController implements Initializable{
	private boolean firstDeliveryLoad;
	private DeliveryRequest deliveryRequest;
	private ListView<String> deliveryList;
	
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
        deliveryRequest = new DeliveryRequest();
        deliveryList = new ListView<String>();
        
        graphBuilder = new GraphBuilder();
        map = new Map();
    }
    
    private void handleLoadDelivery() {
    	FileChooser filechooser = new FileChooser();
    	File deliveryRequestFile = filechooser.showOpenDialog(null);
    	
    	if(deliveryRequestFile != null) {
    		try {
    			XMLDeserializer.loadDeliveryRequest(deliveryRequestFile.getAbsolutePath().toString(), map, deliveryRequest);
			} catch (Exception e) {
				e.printStackTrace();
			}

        	Round round = new Round(deliveryRequest);
    		round.computePaths(map);
    		round.computeRound(map);
        	createDeliveriesList(round);
        	graphBuilder.drawRound(canvasRound, round);
    	}
    }
    
    private void createDeliveriesList(Round round) {
    	ObservableList<String> deliveriesTexts = FXCollections.observableArrayList();

    	List<DeliveryTime> deliveryTimes = round.getArrivalTimes();
    	for (DeliveryTime dt : deliveryTimes) {
    		deliveriesTexts.add(deliveryToText(dt.getCheckpoint()));
    	}
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
    
    private String deliveryToText(Checkpoint c) {
    	String text = "Adresse : " + c.getAssociatedWaypoint().getId() + "\n";
    	
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

    private void handleLoadMap() {
    	FileChooser filechooser = new FileChooser();
    	File mapFile = filechooser.showOpenDialog(null);
    	
    	if(mapFile != null){
    		try {
				XMLDeserializer.loadMap(mapFile.getAbsolutePath().toString(), map);
			} catch (Exception e) {
				e.printStackTrace();
			}
    		loadMapButton.setVisible(false);
    		menuLoadDelivery.setDisable(false);
    		loadDeliveryButton.setDisable(false);
    		loadDeliveryButton.setText("Charger demande de livraisons");
    		graphBuilder.drawMap(canvasMap, map);
    	}
    }

}