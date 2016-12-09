package controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.ParserConfigurationException;

import controller.command.Addition;
import controller.command.CommandManager;
import controller.command.Deletion;
import controller.command.TimeChange;
import model.*;
import org.xml.sax.SAXException;

import controller.xml.XMLDeserializer;
import controller.xml.XMLException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import model.DeliveryRequest;
import model.Map;
import model.Round;

import view.MainWindowController;
import view.ErrorDisplayer;
import view.ErrorHandler;

public class Controller {

	private MainWindowController window;
	private Round currentRound;
	private Map currentMap;
	private DeliveryRequest currentDeliveryRequest;
	private CommandManager commandManager;
	private int currentTimeOrder;

	public Controller(MainWindowController mainwindow) {
		this.window = mainwindow;
		this.commandManager = new CommandManager();
		currentRound = null;
		currentMap = null;
		currentDeliveryRequest = null;
		currentTimeOrder = 0;
	}
	
	public Checkpoint getCheckpoint(int idCheckpoint){
		return currentDeliveryRequest.getDeliveryPointId(idCheckpoint);
	}

	public void loadMap(String path) {
		Map newMap;

		if (path != null) {
			try {
				newMap = XMLDeserializer.loadMap(path);
			} catch (XMLException e) {
				ErrorDisplayer.displayWarningMessageBox(
						"Oups, il semble que le fichier que vous avez spï¿½cifiï¿½ ne soit pas une carte valide.");

				return;
			} catch (IOException | SAXException | ParserConfigurationException e) {
				e.printStackTrace();
				ErrorDisplayer.displayWarningMessageBox("Oups, une erreur non attendue est survenue.");
				return;
			}

			if (newMap != null) {
				currentMap = newMap;

				window.updateAfterLoadMap();
			}
			
			
		}
	}

	/**
	 * Loads a deliveries request and computes the corresponding round. Updates the display when it's done.
	 * @param path
	 * 		Path of the deliveries request's XML file
	 */
	public void loadDeliveryRequest(String path) {
		if (path != null && currentMap != null) {
			DeliveryRequest newDeliveryRequest;
			try {
				newDeliveryRequest = XMLDeserializer.loadDeliveryRequest(path, currentMap);
			} catch (XMLException e) {

				ErrorDisplayer.displayWarningMessageBox(
						"Oups, il semble que le fichier que vous avez spécifié ne soit pas une"
								+ " demande de livraison valide.");

				return;
			} catch (IOException | SAXException | ParserConfigurationException | ParseException e) {
				e.printStackTrace();
				ErrorDisplayer.displayWarningMessageBox("Oups, une erreur dans la lecture du fichier de livraison est survenue.");
				return;
			}
			
			computeRound(newDeliveryRequest);
			
		}
	}
	
	public void computeRound(DeliveryRequest newDeliveryRequest){
		if (newDeliveryRequest != null) {
			window.showWaitingDialog();
			
			currentDeliveryRequest = newDeliveryRequest;
			currentRound = new Round(currentDeliveryRequest);
			try {
				// Calcul de la tournï¿½e
				currentRound.computePaths(currentMap);
				currentRound.computeRound(currentMap);
				handleSucessfulLoadDelivery();
			} catch (NullPointerException e) {
				if(currentRound.getNumOfRound() == 0){
					ErrorHandler.impossibleRound(currentRound, window);
				}else{
					ErrorDisplayer.displayWarningMessageBox(
						"La demande de livraison ne peut pas être traitée, elle ne semble pas correspondre à la carte actuelle.");
				}
				return;
			}
			window.closeWaitingDialog();
			handleSucessfulLoadDelivery();

			window.updateAfterLoadNewRound(); // update graphique
		}
	}
	
	public void clearRound() {
		this.currentRound = null;
		currentTimeOrder = 0;
	}
	
	public void setCurrentTimeOrder(int value) {
		currentTimeOrder = value;
		handleSucessfulLoadDelivery();
	}

	public void deleteCheckpoint(Checkpoint checkpoint) {
		Round round = this.commandManager.doCommand(new Deletion(currentRound, checkpoint, currentMap));
		if( round != null) {
			currentRound = round;
			window.updateAfterLoadNewRound();
		}
		else {
    		ErrorDisplayer.displayWarningMessageBox("Modification impossible, erreur survenue");
    	}
    }

    public void addCheckpoint(Checkpoint checkpoint) {
    	Round round = this.commandManager.doCommand(new Addition(currentRound, currentMap,checkpoint));
    	if( round != null) {
			currentRound = round;
			window.updateAfterLoadNewRound();
			handleSucessfulLoadDelivery();
		}
    	else {
    		ErrorDisplayer.displayWarningMessageBox("Modification impossible, conflit avec les livraisons en cours");
    	}
    }

    public void changeCheckpointTime(Checkpoint checkpoint, Date start, Date end) {
    	Round round = this.commandManager.doCommand(new TimeChange(checkpoint, currentRound, start, end, currentMap));
    	if( round != null) {
			currentRound = round;
			window.updateAfterLoadNewRound();
			handleSucessfulLoadDelivery();
		}
    	else{
    		Alert alert = new Alert(AlertType.CONFIRMATION);
    				
			alert.setTitle("Calcul d'une nouvelle tournée");
    		alert.setHeaderText("Les modifications que vous demandez ne sont pas possible sans impacter le reste de la tournée");
    		alert.setContentText("Que voulez-vous faire ?");

    		ButtonType buttonTypeOk = new ButtonType("Calculer une nouvelle tournée");
    		ButtonType buttonTypeCancel = new ButtonType("Faire un nouveau changement", ButtonData.CANCEL_CLOSE);

    		alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);

    		Optional<ButtonType> result = alert.showAndWait();
    		if (result.get() == buttonTypeOk ){
		    	try {
		    		currentDeliveryRequest.changeACheckpoint(checkpoint.getId(), start, end);
					computeRound(currentDeliveryRequest);
				} catch (Exception e) {
					ErrorDisplayer.displayWarningMessageBox("Modification impossible, conflit avec les livraisons en cours");
					e.printStackTrace();
				}
    		}
    	}
    }

    public void undoLastCommand() {
    	Round round = this.commandManager.undoCommand();
    	if( round != null) {
			currentRound = round;
			window.updateAfterLoadNewRound();
			handleSucessfulLoadDelivery();
		}
    	else {
    		ErrorDisplayer.displayWarningMessageBox("Rien à annuler");
    	}
    }

    public void redoLastCommand() {
    	Round round = this.commandManager.redoCommand();
    	if( round != null) {
			currentRound = round;
			window.updateAfterLoadNewRound();
			handleSucessfulLoadDelivery();
		}
    	else {
    		ErrorDisplayer.displayWarningMessageBox("Rien à restaurer");
    	}
    }

	// GETTERS and SETTERS
	public Round getCurrentRound() {
		return currentRound;
	}
	
	public List<Section> getCurrentRoute() {
		return currentRound.getRoute(currentTimeOrder);
	}

	public Map getCurrentMap() {
		return currentMap;
	}
	
	public MainWindowController getWindow() {
		return window;
	}
	
	public DeliveryRequest getCurrentDeliveryRequest() {
		return currentDeliveryRequest;
	}
    
    // -- PRIVATES ------------------------------------------------------------
    
	private void handleSucessfulLoadDelivery(){
		// Ecriture de la feuille de route
		Roadmap.writeRoadmap(this);
	}

	public List<DeliveryTime> getCurrentRoundTimeOrder() {

		return currentRound.getRoundTimeOrder(currentTimeOrder);
	}

}
