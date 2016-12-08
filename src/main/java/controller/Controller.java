package controller;

import java.io.IOException;
import java.text.ParseException;

import javax.xml.parsers.ParserConfigurationException;

import controller.command.Addition;
import controller.command.CommandManager;
import controller.command.Deletion;
import controller.command.TimeChange;
import model.Checkpoint;
import org.xml.sax.SAXException;

import controller.xml.XMLDeserializer;
import controller.xml.XMLException;
import model.DeliveryRequest;
import model.Map;
import model.Round;
import view.MainWindowController;
import view.errorDisplayer;

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
				errorDisplayer.displayWarningMessageBox(
						"Oups, il semble que le fichier que vous avez spécifié ne soit pas une carte valide.");

				return;
			} catch (IOException | SAXException | ParserConfigurationException e) {
				e.printStackTrace();
				errorDisplayer.displayWarningMessageBox("Oups, une erreur non attendue est survenue.");
				return;
			}

			if (newMap != null) {
				currentMap = newMap;

				window.updateAfterLoadMap();
			}
		}
	}

	public void loadDeliveryRequest(String path) {

		if (path != null && currentMap != null) {
			DeliveryRequest newDeliveryRequest;
			try {
				newDeliveryRequest = XMLDeserializer.loadDeliveryRequest(path, currentMap);
			} catch (XMLException e) {

				errorDisplayer.displayWarningMessageBox(
						"Oups, il semble que le fichier que vous avez spécifié ne soit pas une"
								+ " demande de livraison valide.");

				return;
			} catch (IOException | SAXException | ParserConfigurationException | ParseException e) {
				e.printStackTrace();
				errorDisplayer.displayWarningMessageBox("Oups, une erreur dans la lecture du fichier de livraison est survenue.");
				return;
			}

			if (newDeliveryRequest != null) {
				currentDeliveryRequest = newDeliveryRequest;
				try {
					// Calcul de la tournée
					currentRound = new Round(currentDeliveryRequest);
					currentRound.computePaths(currentMap);
					currentRound.computeRound(currentMap);
				} catch (NullPointerException e) {
					errorDisplayer.displayWarningMessageBox(
							"La demande de livraison ne peut pas être traitée, elle ne semble pas correspondre à la carte actuelle.");
					return;
				}

				handleSucessfulLoadDelivery();
				
				// update graphique
				window.updateAfterLoadDelivery();
			}
		}
	}
	
	public void clearRound() {
		this.currentRound = null;
	}

	// GETTERS and SETTERS
	public Round getCurrentRound() {
		return currentRound;
	}

	public Map getCurrentMap() {
		return currentMap;
	}
	
	public DeliveryRequest getCurrentDeliveryRequest() {
		return currentDeliveryRequest;
	}
	
	public Round deleteCheckpoint(Checkpoint checkpoint, Round round) {
        return this.commandManager.doCommand(new Deletion(round), checkpoint);
    }

    public Round addCheckpoint(Checkpoint checkpoint, Round round) {
        return this.commandManager.doCommand(new Addition(round), checkpoint);
    }

    public Round changeCheckpointTime(Checkpoint checkpoint, Round round) {
        return this.commandManager.doCommand(new TimeChange(round), checkpoint);
    }

    public Round undoLastCommand(Round round) {
        return this.commandManager.undoCommand(round);
    }

    public Round redoLastCommand(Round round) {
        return this.commandManager.redoCommand(round);
    }

    // -- PRIVATES ------------------------------------------------------------
    
	private void handleSucessfulLoadDelivery() {
		// Ecriture de la feuille de route
		Roadmap.writeRoadmap(currentRound, currentMap);
	}
	
    

}
