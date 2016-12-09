package view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.Checkpoint;
import model.Round;

public class ErrorHandler {

	
	/**
	 * Display a dialog to display some informations to users.
	 * 
	 * @param message
	 *            message to display.
	 **/
	public static void displayWarningMessageBox(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Rapport d'erreur");
		alert.setHeaderText("Une erreur est survenue");
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	public static void impossibleRound(Round round, MainWindowController window){
		ObservableList<String> deliveriesTexts = FXCollections.observableArrayList();
		
		window.closeWaitingDialog();
		
		ErrorDisplayer.displayWarningMessageBox("Aucune tournée n'est calculable avec les contraintes que vous demandez !");
		
		window.updateDeliveriesListView();

		window.getDeliveriesListView().showWarningLabel();
		List<Checkpoint> shortTimeRangeCheckpoint = new ArrayList<Checkpoint>();
		long minTimeRange = 30 * 60 * 1000;//Long.MAX_VALUE;
		for(Checkpoint ck:round.getRequest().getDeliveryPointList()){
			if(ck.getTimeRangeEnd() != null && ck.getTimeRangeStart() != null){
				long timeRange = ck.getTimeRangeEnd().getTime() - ck.getTimeRangeStart().getTime();
				if(timeRange < minTimeRange){
					shortTimeRangeCheckpoint.add(ck);
				}
			}
		}
		List<Integer> listIdCheckpoint = new ArrayList<Integer>();
		for(Checkpoint ck:shortTimeRangeCheckpoint){
			String text = "Adresse : " + ck.getId() +"\n ";
			listIdCheckpoint.add(ck.getId());
			text += "ouvert de "
					+ new SimpleDateFormat("HH:mm").format(ck.getTimeRangeStart().getTime())
					+ " à "
					+ new SimpleDateFormat("HH:mm").format(ck.getTimeRangeEnd().getTime())
					+ "\n";
			deliveriesTexts.add(text);
		}
		
		window.getDeliveriesListView().setObservableListInDeliveryList(deliveriesTexts);
		window.getDeliveriesListView().setDeliveryPointId(listIdCheckpoint);
		window.getDeliveriesListView().replaceItemAction();
	}
	
}
