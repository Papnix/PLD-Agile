package view;

import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import controller.Controller;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import model.Checkpoint;
import model.DeliveryTime;

public class ModificationDialog extends Dialog<Object>{
	
	protected GridPane grid;
	protected ComboBox<Checkpoint> deliveryCombo;

	protected ButtonType buttonCancel;
	protected ButtonType buttonOk;
	
	public ModificationDialog(Controller controller) {
		super();
		
		setTitle("Modification de la tournée");
		setResizable(true);
		grid = new GridPane();
		deliveryCombo = new ComboBox<Checkpoint>();
		
		Label label1 = new Label("Liste des points de livraison : ");
				
		grid.add(label1, 1, 1);
		grid.add(deliveryCombo, 1, 2);

		loadCombo(controller);
		
		getDialogPane().setContent(grid);	

		buttonCancel = new ButtonType("Annuler", ButtonData.CANCEL_CLOSE);
		buttonOk = new ButtonType("Modifier", ButtonData.APPLY);
		getDialogPane().getButtonTypes().add(buttonCancel);
		getDialogPane().getButtonTypes().add(buttonOk);
		
		Node loginButton = getDialogPane().lookupButton(buttonOk);
		loginButton.setDisable(true);
	}

	private void loadCombo(Controller controller) {
		
		deliveryCombo.getItems().clear();
		
		for(DeliveryTime delivery : controller.getCurrentRound().getRoundTimeOrder(0)) {
			deliveryCombo.getItems().add(delivery.getCheckpoint());
		}
	}
}

/*
 * super();
		
		setTitle("Modification de la tournée");
		setResizable(true);

		layout = new VBox();
		
		grid = new GridPane();
		layout.getChildren().add(grid);
		
		deliveryCombo = new ComboBox<Checkpoint>();
		
		Label label1 = new Label("Liste des points de livraison : ");
				
		grid.add(label1, 1, 1);
		grid.add(deliveryCombo, 1, 2);

		loadCombo(controller);
		
		getDialogPane().setContent(layout);	

		buttonCancel = new Button("Annuler");
		buttonOk = new Button("Modifier");
		HBox buttonLayout = new HBox();
		buttonLayout.getChildren().add(buttonOk);
		buttonLayout.getChildren().add(buttonCancel);
		layout.getChildren().add(buttonLayout);
		
*/
