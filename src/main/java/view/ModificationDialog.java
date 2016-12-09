package view;

import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;

import java.util.List;

import controller.Controller;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import model.Checkpoint;
import model.DeliveryTime;
import model.Round;

public abstract class ModificationDialog extends Dialog<Object>{
	
	protected GridPane grid;
	protected ComboBox<Checkpoint> deliveryCombo;
	Label labelCombo;

	protected ButtonType buttonCancel;
	protected ButtonType buttonOk;
	
	protected abstract void onComboValueChanged();
	protected abstract void defineOnCloseAction(Controller controller);
	
	public ModificationDialog(Controller controller) {
		super();
		
		setTitle("Modification de la tournée");
		setResizable(true);
		grid = new GridPane();
		grid.setPadding(new Insets(5));
		grid.setHgap(5);
		deliveryCombo = new ComboBox<Checkpoint>();
		
		labelCombo = new Label("Liste des points de livraison : ");
		grid.add(labelCombo, 1, 1);
		grid.add(deliveryCombo, 1, 2);

		loadCombo(controller);
		
		getDialogPane().setContent(grid);	

		buttonCancel = new ButtonType("Annuler", ButtonData.CANCEL_CLOSE);
		buttonOk = new ButtonType("Modifier", ButtonData.APPLY);
		getDialogPane().getButtonTypes().add(buttonCancel);
		getDialogPane().getButtonTypes().add(buttonOk);
		
		Node buttonMod = getDialogPane().lookupButton(buttonOk);
		buttonMod.setDisable(true);
	}
	
	protected void setCheckpoint(Checkpoint checkpoint){
		deliveryCombo.getItems().add(checkpoint);
	}

	private void loadCombo(Controller controller) {
		
		deliveryCombo.getItems().clear();
		List<DeliveryTime> DeliveriesTimeList = controller.getCurrentRoundTimeOrder();
		if(DeliveriesTimeList != null){
			for(int index = 1; index < controller.getCurrentRoundTimeOrder().size() - 1; ++index) {
				deliveryCombo.getItems().add(controller.getCurrentRoundTimeOrder().get(index).getCheckpoint());
			}
		}
	}
	
	
}

