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
import model.Delivery;
import model.DeliveryRequest;


public class MainWindowController implements Initializable{
	private ListView<String> deliveryList;
	private GraphBuilder graphBuilder;
	
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
        
        deliveryList = new ListView<String>();
        graphBuilder = new GraphBuilder();
    }
    
    private void handleLoadDelivery() {
    	FileChooser filechooser = new FileChooser();
    	File deliveryRequestFile = filechooser.showOpenDialog(null);
    	
    	if(deliveryRequestFile != null) {
    		try {
    			XMLDeserializer.loadDeliveryRequest(deliveryRequestFile.getAbsolutePath().toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
    		
        	loadDeliveryButton.setVisible(false);
        	createDeliveriesList();
    	}
    }
    
    private void createDeliveriesList() {
    	ObservableList<String> deliveriesTexts = FXCollections.observableArrayList();
    	List<Delivery> deliveries = DeliveryRequest.getInstance().getDeliveryPointList();
    	for (Delivery d : deliveries) {
    		deliveriesTexts.add(deliveryToText(d));
    	}
    	deliveryList.setItems(deliveriesTexts);

    	AnchorPane.setTopAnchor(deliveryList, 0d);
    	AnchorPane.setBottomAnchor(deliveryList, 0d);
    	AnchorPane.setRightAnchor(deliveryList, 0d);
    	AnchorPane.setLeftAnchor(deliveryList, 0d);
    	deliveryPane.getChildren().add(deliveryList);
    }
    
    private String deliveryToText(Delivery d) {
    	String text = "Adresse : " + d.getAssociatedWaypoint().getId() + "\n";
    	text += "Durée : " + d.getDuration();
    	
    	return text;
    }

    private void handleLoadMap() {
    	FileChooser filechooser = new FileChooser();
    	File mapFile = filechooser.showOpenDialog(null);
    	
    	if(mapFile != null){
    		try {
				XMLDeserializer.loadMap(mapFile.getAbsolutePath().toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
    		loadMapButton.setVisible(false);
    		menuLoadDelivery.setDisable(false);
    		loadDeliveryButton.setDisable(false);
    		loadDeliveryButton.setText("Charger demande de livraisons");
    		graphBuilder.drawMap(canvasMap);
    	}
    }

}