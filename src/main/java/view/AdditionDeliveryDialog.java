package view;

import controller.Controller;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.util.Callback;
import model.Checkpoint;


public class AdditionDeliveryDialog extends ModificationDialog {
	
	
	public AdditionDeliveryDialog(Controller controller) {
		super(controller);
		setHeaderText("Vous �tes sur le point d'ajouter une livraison � la tourn�e courante\n" 
					+ "(Notez que l'ajout peut �tre refus� dans le cas o� la nouvelle livraison est impossible"
					+ " car entrerait en conflit avec celles existantes)");
		
		
		
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
		        	
		        	
		        	
		        	controller.addCheckpoint(deliveryCombo.getValue());
		        }
		        return null;
		    }
		});
		
	}

}