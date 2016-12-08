package controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import controller.command.Addition;
import controller.command.CommandManager;
import controller.command.Deletion;
import controller.command.TimeChange;
import model.*;
import org.xml.sax.SAXException;

import controller.xml.XMLDeserializer;
import controller.xml.XMLException;
import model.DeliveryRequest;
import model.Map;
import model.Round;
import view.ErrorDisplayer;
import view.MainWindowController;

public class Controller {

	private MainWindowController window;
	private Round currentRound;
	private Map currentMap;
	private DeliveryRequest currentDeliveryRequest;
	private CommandManager commandManager;

	public Controller(MainWindowController mainwindow) {
		this.window = mainwindow;
		this.commandManager = new CommandManager();
		currentRound = null;
		currentMap = null;
		currentDeliveryRequest = null;
	}

	public void loadMap(String path) {
		Map newMap;

		if (path != null) {
			try {
				newMap = XMLDeserializer.loadMap(path);
			} catch (XMLException e) {
				ErrorDisplayer.displayWarningMessageBox(
						"Oups, il semble que le fichier que vous avez sp�cifi� ne soit pas une carte valide.");

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
						"Oups, il semble que le fichier que vous avez sp�cifi� ne soit pas une"
								+ " demande de livraison valide.");

				return;
			} catch (IOException | SAXException | ParserConfigurationException | ParseException e) {
				e.printStackTrace();
				ErrorDisplayer.displayWarningMessageBox("Oups, une erreur dans la lecture du fichier de livraison est survenue.");
				return;
			}

			if (newDeliveryRequest != null) {
				window.showWaitingDialog();
				
				currentDeliveryRequest = newDeliveryRequest;
				try {
					// Calcul de la tourn�e
					currentRound = new Round(currentDeliveryRequest);
					currentRound.computePaths(currentMap);
					currentRound.computeRound(currentMap);
				} catch (NullPointerException e) {
					ErrorDisplayer.displayWarningMessageBox(
							"La demande de livraison ne peut pas �tre trait�e, elle ne semble pas correspondre � la carte actuelle.");
					return;
				}
				
				window.closeWaitingDialog();
				handleSucessfulLoadDelivery();
				window.updateAfterLoadDelivery(); // update graphique
			}
		}
	}
	
	public void clearRound() {
		this.currentRound = null;
	}
	
	public Round deleteCheckpoint(Checkpoint checkpoint, Round round, Map map) {
        return this.commandManager.doCommand(new Deletion(round, checkpoint, map));
    }

    public Round addCheckpoint(Checkpoint checkpoint, Round round, Map map) {
        return this.commandManager.doCommand(new Addition(round, map,checkpoint));
    }

    public Round changeCheckpointTime(Checkpoint checkpoint, Round round, Date start, Date end, Map map) {
        return this.commandManager.doCommand(new TimeChange(checkpoint, round, start, end, map));
    }

    public Round undoLastCommand() {
        return this.commandManager.undoCommand();
    }

    public Round redoLastCommand() {
        return this.commandManager.redoCommand();
    }

	// GETTERS and SETTERS
	public Round getCurrentRound() {
		return currentRound;
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
    
	private void handleSucessfulLoadDelivery() {
		// Ecriture de la feuille de route
		Roadmap.writeRoadmap(currentRound, currentMap);
	}

}
