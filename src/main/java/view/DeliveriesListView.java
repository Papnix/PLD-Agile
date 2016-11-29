package view;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import model.Checkpoint;
import model.DeliveryTime;
import model.Map;
import model.Round;
import model.Section;

/**
 * Descriptif de la classe :
 * Correspond à la ListView à droite contenant les livraisons d'une tournée calculée.
 * Contient la ListView en question ainsi que des méthodes pour remplir cette ListView lorsque la tournée a été calculée.
 * Lorsqu'une nouvelle tournée est calculée, il n'est pas nécessaire de créer une nouvelle instance de cette classe,
 * il suffit d'appeler la méthode createDeliveriesList.
 */
public class DeliveriesListView {
	private ListView<String> deliveryList;
	private AnchorPane deliveryPane;
	
	
	public DeliveriesListView(AnchorPane deliveryPane) {
        deliveryList = new ListView<String>();
        this.deliveryPane = deliveryPane;
        
    	AnchorPane.setTopAnchor(deliveryList, 0d);
    	AnchorPane.setBottomAnchor(deliveryList, 0d);
    	AnchorPane.setRightAnchor(deliveryList, 0d);
    	AnchorPane.setLeftAnchor(deliveryList, 0d);
    	this.deliveryPane.getChildren().add(deliveryList);
	}
	
	/**
	 * Remplit la ListView avec les infos des livraisons dans l'ordre chronologique.
	 * Cette méthode peut directement être appelée lorsque la tournée change,
	 * il n'y a pas besoin de créer une nouvelle instance de DeliveriesListView
	 * @param round Correspond à la tournée calculée
	 * @param map Correspond au graphe du plan de la ville
	 */
	public void createDeliveriesList(Round round, Map map) {
    	ObservableList<String> deliveriesTexts = FXCollections.observableArrayList();

    	List<DeliveryTime> deliveryTimes = round.getArrivalTimes();
    	for (int i = 0; i < deliveryTimes.size() - 1; i++) {
    		DeliveryTime dt = deliveryTimes.get(i);
    		deliveriesTexts.add(deliveryToText(dt.getCheckpoint(),
    										   round.getPath(dt.getCheckpoint().getAssociatedWaypoint().getId(),
    												   		 deliveryTimes.get(i+1).getCheckpoint().getAssociatedWaypoint().getId()),
    										   map));
    	}
    	deliveryList.setItems(deliveriesTexts);
    }
    
	/**
	 * Extrait les informations d'une livraison et renvoie le texte à afficher dans la ListView
	 * @param c Livraison
	 * @param path Ensemble de Routes pour aller du point de livraison 'c' au point de livraison suivant
	 * @param map Graphe de la ville
	 * @return Texte à afficher directement dans une case de la ListView
	 */
    private String deliveryToText(Checkpoint c, List<Integer> path, Map map) {
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
    			
    			text += "      Prendre le tronçon : " + s.getStreetName() + " entre " + s.getOrigin().getId() + " et "
    					+ s.getDestination().getId() + "\n";
    		}
    	}
    	
    	return text;
    }
}
