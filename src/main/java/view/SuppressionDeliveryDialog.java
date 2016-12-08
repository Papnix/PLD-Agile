package view;

import controller.Controller;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.util.Callback;
import model.Checkpoint;

public class SuppressionDeliveryDialog extends ModificationDialog {
	
	
	public SuppressionDeliveryDialog(Controller controller) {
		super(controller);
		setHeaderText("Vous êtes sur le point de supprimer une livraison à la tournée courante\n" 
				+ "Sélectionez la livraison à enlever.");
		
		onComboValueChanged();
		defineOnCloseAction(controller);
	}

	@Override
	protected void onComboValueChanged() {
		deliveryCombo.valueProperty().addListener(new ChangeListener<Checkpoint>() {
			@Override
			public void changed(ObservableValue<? extends Checkpoint> obs, Checkpoint oldValue, Checkpoint newValue) {
				Node loginButton = getDialogPane().lookupButton(buttonOk);
				loginButton.setDisable(newValue == null);
			}
		});
		
	}

	@Override
	protected void defineOnCloseAction(Controller controller) {
		setResultConverter(new Callback<ButtonType, Object>() {
		    @Override
		    public Object call(ButtonType b) {
		        if (b == buttonOk) {
		        	controller.deleteCheckpoint(deliveryCombo.getValue());
		        }
		        return null;
		    }
		});
		
	}

}