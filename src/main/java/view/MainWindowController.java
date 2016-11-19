package view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;


public class MainWindowController implements Initializable{

    @FXML
    private Button loadDeliveryButton;
    @FXML
    private Button loadMapButton;
    @FXML
    private MenuItem menuLoadMap;
    @FXML
    private MenuItem menuLoadDelivery;

    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert loadDeliveryButton != null : "fx:id=\"loadDeliveryButton\" was not injected: check your FXML file 'simple.fxml'.";
        loadDeliveryButton.setOnAction((event) -> {
        	HandleLoadDelivery();
        });
                
        assert loadMapButton != null : "fx:id=\"loadMapButton\" was not injected: check your FXML file 'simple.fxml'.";
        loadMapButton.setOnAction((event) -> {
        	HandleLoadMap();
        });
        
        assert menuLoadMap != null : "fx:id=\"menuLoadMap\" was not injected: check your FXML file 'simple.fxml'.";
        menuLoadMap.setOnAction((event) -> {
        	HandleLoadMap();
        });
        
        assert menuLoadDelivery != null : "fx:id=\"menuLoadDelivery\" was not injected: check your FXML file 'simple.fxml'.";
        menuLoadDelivery.setOnAction((event) -> {
        	HandleLoadDelivery();
        });
    }
    
    private void HandleLoadDelivery() {
    	System.out.println("Chargement demande livraison");
    }
    private void HandleLoadMap() {
	
    	System.out.println("Chargement Map");
    }

}