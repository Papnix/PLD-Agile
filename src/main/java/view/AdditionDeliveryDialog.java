package view;

import controller.Controller;


public class AdditionDeliveryDialog extends ModificationDialog {
	
	
	public AdditionDeliveryDialog(Controller controller) {
		super(controller);
		setHeaderText("Vous �tes sur le point d'ajouter une livraison � la tourn�e courante\n" 
					+ "(Notez que l'ajout peut �tre refus� dans le cas o� la nouvelle livraison est impossible"
					+ " car entrerait en conflit avec celles existantes)");
		
		
		
	}

}