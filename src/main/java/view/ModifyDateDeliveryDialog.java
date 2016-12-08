package view;

import controller.Controller;

public class ModifyDateDeliveryDialog extends ModificationDialog {
	
	
	public ModifyDateDeliveryDialog(Controller controller) {
		super(controller);
		setHeaderText("Vous êtes sur le point de modifier une plage horraire d'une livraison à la tournée courante\n" 
				+ "Sélectionez la livraison à modifier puis indiquez la plage horaire.");
		
		
	}

}