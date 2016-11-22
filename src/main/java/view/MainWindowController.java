package view;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import controller.xml.XMLDeserializer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;


public class MainWindowController implements Initializable{

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
        
    	assert loadDeliveryButton != null : "fx:id=\"loadDeliveryButton\" was not injected: check your FXML file 'view.fxml'.";
        loadDeliveryButton.setOnAction((event) -> {
        	handleLoadDelivery();
        });
                
        assert loadMapButton != null : "fx:id=\"loadMapButton\" was not injected: check your FXML file 'view.fxml'.";
        loadMapButton.setOnAction((event) -> {
        	handleLoadMap();
        });
        
        assert deliveryPane != null : "fx:id=\"deliveryPane\" was not injected: check your FXML file 'view.fxml'.";
        
        assert canvasMap != null : "fx:id=\"canvasMap\" was not injected: check your FXML file 'view.fxml'.";
        
        graphBuilder = new GraphBuilder();
        
        
    }
    
    private void handleLoadDelivery() {
    	System.out.println("Chargement demande livraison");
    }
    private void handleLoadMap() {
	
    	System.out.println("Chargement Map");
    	FileChooser filechooser = new FileChooser();
    	filechooser.setTitle("Import de map");
    	
    	File mapFile = filechooser.showOpenDialog(null);
    	if(mapFile != null){
    		try {
				XMLDeserializer.loadMap(mapFile.getAbsolutePath().toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		loadMapButton.setVisible(false);
    		graphBuilder.drawMap(canvasMap);
    	}
    }

}