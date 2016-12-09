package view;

import controller.Controller;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import model.Checkpoint;

public class SuppressionDeliveryDialog extends ModificationDialog {
	
	
	public SuppressionDeliveryDialog(Controller controller) {
		super(controller);
		setHeaderText("Vous �tes sur le point de supprimer une livraison � la tourn�e courante\n" 
				+ "S�lectionez la livraison � enlever.");
		
		deliveryCombo.valueProperty().addListener(new ChangeListener<Checkpoint>() {
			@Override
			public void changed(ObservableValue<? extends Checkpoint> obs, Checkpoint oldValue, Checkpoint newValue) {
				Node loginButton = getDialogPane().lookupButton(buttonOk);
				loginButton.setDisable(newValue == null);
			}
		});

	}

}