package view;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;
import controller.xml.XMLDeserializer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import model.Checkpoint;
import model.DeliveryRequest;
import model.DeliveryTime;
import model.Map;
import model.Round;
import model.Section;


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
        deliveryList = new ListView<String>();
        
        graphBuilder = new GraphBuilder();
        
        setupCanvasMap(canvasMap);
        
        
    }
    
    private void handleLoadDelivery() {
    	FileChooser filechooser = new FileChooser();
    	File deliveryRequestFile = filechooser.showOpenDialog(null);
        deliveryRequest = new DeliveryRequest();
    	if(deliveryRequestFile != null) {
    		try {
    			XMLDeserializer.loadDeliveryRequest(deliveryRequestFile.getAbsolutePath().toString(), map, deliveryRequest);
			} catch (Exception e) {
				e.printStackTrace();
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Rapport d'erreur");
				alert.setHeaderText("Erreur chargement demande de livraison");
				alert.setContentText("Oups, il semble que le fichier que vous avez spécifié ne soit pas une demande de livraison");

				alert.showAndWait();
				return;
			}

        	Round round = new Round(deliveryRequest);
    		round.computePaths(map);
    		round.computeRound(map);
        	createDeliveriesList(round);
        	graphBuilder.drawRound(canvasRound, round);
        	setupCanvasRound(canvasRound, round);
    	}
    }
    
    private void createDeliveriesList(Round round) {
    	ObservableList<String> deliveriesTexts = FXCollections.observableArrayList();

    	/*List<DeliveryTime> deliveryTimes = round.getArrivalTimes();
    	for (int i = 0; i < deliveryTimes.size() - 1; i++) {
    		DeliveryTime dt = deliveryTimes.get(i);
    		deliveriesTexts.add(deliveryToText(dt.getCheckpoint(), round.getPath(dt.getCheckpoint().getAssociatedWaypoint().getId(), deliveryTimes.get(i+1).getCheckpoint().getAssociatedWaypoint().getId())));
    	}*/
    	//deliveriesTexts.add(deliveryToText(round));
    	String text = "";
    	
    	for(DeliveryTime dt:round.getRoundTimeOrder()){
    		text = "Adresse : " + dt.getCheckpoint().getAssociatedWaypoint().getId() + "\n";
    		if(dt.getArrivalTime() != null){
    			text += "Heure d'arrivée : " + new SimpleDateFormat("HH:mm").format(dt.getArrivalTime().getTime()) + "\n";
    			text += "Heure de depart : " + new SimpleDateFormat("HH:mm").format(dt.getDepartureTime().getTime());
    		}else{
    			text += "Heure de debut de tournée : " + new SimpleDateFormat("HH:mm").format(dt.getDepartureTime().getTime());
    		}
    		
    		deliveriesTexts.add(text);
    		deliveryList.setItems(deliveriesTexts);
    	}
    	//deliveryList.setItems(deliveriesTexts);

    	if (firstDeliveryLoad) {
	    	AnchorPane.setTopAnchor(deliveryList, 0d);
	    	AnchorPane.setBottomAnchor(deliveryList, 0d);
	    	AnchorPane.setRightAnchor(deliveryList, 0d);
	    	AnchorPane.setLeftAnchor(deliveryList, 0d);
	    	deliveryPane.getChildren().add(deliveryList);
			firstDeliveryLoad = false;
    	}
    }
    
    private String deliveryToText(Checkpoint c, List<Integer> path) {
    	   	
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
    	
    	text += "\n   Parcours :\n";
    	
    	for (int i = 0; i < path.size(); i++) {
    		
    		if (i < path.size() - 1) {
    			Section s = map.getSection(path.get(i), path.get(i+1));
    			
    			text += "      Prendre le tronçon : " + s.getStreetName() + " entre " + s.getOrigin().getId() + " et " + s.getDestination().getId() + "\n";
    		}
    	}
    	
    	return text;
    }
    
    private String deliveryToText(Round round){
    	String text = "";
    	for(DeliveryTime dt:round.getRoundTimeOrder()){
    		text = "Adresse : " + dt.getCheckpoint().getAssociatedWaypoint().getId() + "\n";
    		text += "Heure d'arrivée : " + dt.getArrivalTime().getTime();
    	}
    	return text;
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