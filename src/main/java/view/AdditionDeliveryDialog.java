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


public class AdditionDeliveryDialog extends ModificationDialog {
	
	private TextField idWaypointField;
	private TextField startDateField;
	private TextField endDateField;
	private TextField durationField;
	private SimpleDateFormat timingFormat = new SimpleDateFormat("HH:mm:ss", Locale.FRANCE);

	public AdditionDeliveryDialog(Controller controller) {
		super(controller);
		setHeaderText("Vous �tes sur le point de modifier une plage horraire d'une livraison � la tourn�e courante\n" 
				+ "S�lectionez la livraison � modifier puis indiquez la plage horaire.");
		
		idWaypointField = new TextField();
		startDateField = new TextField();
		endDateField = new TextField();
		durationField = new TextField();
		
		grid.add(new Label("Id du lieu : "), 2, 1);
		grid.add(idWaypointField, 2, 2);
		grid.add(new Label("Date d'arriv� : "), 2, 3);
		grid.add(startDateField, 2, 4);
		grid.add(new Label("Date de d�part : "), 2, 5);
		grid.add(endDateField, 2, 6);
		grid.add(new Label("Dur�e de la livraison : "), 2, 7);
		grid.add(durationField, 2, 8);
		
		deliveryCombo.setVisible(false);
		labelCombo.setVisible(false);
		Node loginButton = getDialogPane().lookupButton(buttonOk);
		loginButton.setDisable(false);
		defineOnCloseAction(controller);
		
	}

	@Override
	protected void onComboValueChanged() {
		
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
						int id = Integer.parseInt(idWaypointField.getText());
						int duration = Integer.parseInt(idWaypointField.getText());
						
						start = timingFormat.parse(startDateField.getText());
						end = timingFormat.parse(endDateField.getText());
						
			        	controller.addCheckpoint(new Checkpoint( controller.getCurrentMap().getWaypoint(id), duration, start, end));
						
					} catch (ParseException e) {
						ErrorDisplayer.displayWarningMessageBox("Mauvais format de date");
					}
		        }
		        return null;
		    }
		});
		
	}

}