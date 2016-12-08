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
import javafx.util.Callback;
import model.Checkpoint;

public class ModifyDateDeliveryDialog extends ModificationDialog {
	
	private TextField startDate;
	private TextField endDate;
	private SimpleDateFormat timingFormat = new SimpleDateFormat("h a", Locale.FRANCE);

	public ModifyDateDeliveryDialog(Controller controller) {
		super(controller);
		setHeaderText("Vous êtes sur le point de modifier une plage horraire d'une livraison à la tournée courante\n" 
				+ "Sélectionez la livraison à modifier puis indiquez la plage horaire.");
		
		startDate = new TextField();
		endDate = new TextField();
		grid.add(new Label("Date d'arrivé : "), 2, 1);
		grid.add(startDate, 2, 2);
		grid.add(new Label("Date de départ : "), 2, 3);
		grid.add(endDate, 2, 4);
		
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
				
				if(newValue != null) {
			        
					Date start = new Date (newValue.getTimeRangeStart().getTime());
					startDate.setText(start.toString());
					Date end = new Date (newValue.getTimeRangeEnd().getTime());
					endDate.setText(end.toString());
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
			        	controller.changeCheckpointTime(deliveryCombo.getValue(), start, end);
						
					} catch (ParseException e) {
						ErrorDisplayer.displayWarningMessageBox("Mauvais format de date");
					}
		        }
		        return null;
		    }
		});
		
	}

}