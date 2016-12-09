package view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import controller.Controller;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.util.Callback;
import model.Checkpoint;

public class ModifyDateDeliveryDialog extends ModificationDialog {
	
	private TextField startDate;
	private TextField endDate;
	private SimpleDateFormat timingFormat = new SimpleDateFormat("HH:mm", Locale.FRANCE);
	
	protected ButtonType buttonRemoveConstraint;

	public ModifyDateDeliveryDialog(Controller controller) {
		super(controller);
		setHeaderText("Vous êtes sur le point de modifier une plage horraire d'une livraison de la tournée courante\n"
				+ "Sélectionez la livraison à modifier puis indiquez la plage horaire.");
		
		startDate = new TextField();
		endDate = new TextField();
		grid.add(new Label("Heure d'ouverture (HH:mm:ss) : "), 2, 1);
		grid.add(startDate, 2, 2);
		grid.add(new Label("Heure de fermeture (HH:mm:ss) : "), 2, 3);
		grid.add(endDate, 2, 4);
		
		buttonRemoveConstraint = new ButtonType("Supprimer la contrainte temporelle", ButtonData.APPLY);
		getDialogPane().getButtonTypes().add(buttonRemoveConstraint);
		
		Node buttonRem = getDialogPane().lookupButton(buttonRemoveConstraint);
		buttonRem.setDisable(true);
		
		onComboValueChanged();
		defineOnCloseAction(controller);
		
	}

	@Override
	protected void onComboValueChanged() {
		deliveryCombo.valueProperty().addListener(new ChangeListener<Checkpoint>() {
			@Override
			public void changed(ObservableValue<? extends Checkpoint> obs, Checkpoint oldValue, Checkpoint newValue) {
				Node buttonMod = getDialogPane().lookupButton(buttonOk);
				buttonMod.setDisable(newValue == null);
				Node buttonRem = getDialogPane().lookupButton(buttonRemoveConstraint);
				buttonRem.setDisable(newValue == null);
				
				if(newValue != null ) {

			        if(newValue.getTimeRangeStart() != null 
						&& newValue.getTimeRangeEnd() != null) {
			        	Date start = new Date (newValue.getTimeRangeStart().getTime());
			        	Date end = new Date (newValue.getTimeRangeEnd().getTime());
			        	startDate.setText(ModifyDateDeliveryDialog.this.timingFormat.format(start));
						endDate.setText(ModifyDateDeliveryDialog.this.timingFormat.format(end));
			        }
										
					
				}
			}
		});
		
	}

	@Override
	protected void defineOnCloseAction(Controller controller) {
		setResultConverter(new Callback<ButtonType, Object>() {
		    @Override
		    public Object call(ButtonType b) {
		        if (b == buttonOk) {
		        	Date start;
		        	Date end;
					try {
						start = timingFormat.parse(startDate.getText());
						end = timingFormat.parse(endDate.getText());
						
						System.out.println(start);
						System.out.println(end);
			        	controller.changeCheckpointTime(deliveryCombo.getValue(), start, end);
						
					} catch (ParseException e) {
						ErrorDisplayer.displayWarningMessageBox("Mauvais format de date");
					}
		        }
		        else if (b == buttonRemoveConstraint) {
		        	controller.changeCheckpointTime(deliveryCombo.getValue(), null, null);
		        }
		        return null;
		    }
		});
		
	}

}