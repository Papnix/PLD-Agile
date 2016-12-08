package view;

import controller.Controller;


public class AdditionDeliveryDialog extends ModificationDialog {
	
	
	public AdditionDeliveryDialog(Controller controller) {
		super(controller);
		setHeaderText("Vous êtes sur le point d'ajouter une livraison à la tournée courante\n" 
					+ "(Notez que l'ajout peut être refusé dans le cas où la nouvelle livraison est impossible"
					+ " car entrerait en conflit avec celles existantes)");
		
		
		
	}

}