package view;

import controller.Controller;

public class ModifyDateDeliveryDialog extends ModificationDialog {
	
	
	public ModifyDateDeliveryDialog(Controller controller) {
		super(controller);
		setHeaderText("Vous �tes sur le point de modifier une plage horraire d'une livraison � la tourn�e courante\n" 
				+ "S�lectionez la livraison � modifier puis indiquez la plage horaire.");
		
		
	}

}