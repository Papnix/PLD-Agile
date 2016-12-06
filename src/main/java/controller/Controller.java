package controller;

import java.io.IOException;
import java.text.ParseException;

import javax.xml.parsers.ParserConfigurationException;

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

	public Controller(MainWindowController mainwindow) {
		this.window = mainwindow;
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
						"Oups, il semble que le fichier que vous avez sp�cifi� ne soit pas une carte valide.");

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
						"Oups, il semble que le fichier que vous avez sp�cifi� ne soit pas une"
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
					// Calcul de la tourn�e
					currentRound = new Round(currentDeliveryRequest);
					currentRound.computePaths(currentMap);
					currentRound.computeRound(currentMap);
				} catch (NullPointerException e) {
					errorDisplayer.displayWarningMessageBox(
							"La demande de livraison ne peut pas �tre trait�e, elle ne semble pas correspondre � la carte actuelle.");
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

	private void handleSucessfulLoadDelivery() {
		// Ecriture de la feuille de route
		Roadmap.writeRoadmap(currentRound, currentMap);
	}
	
}
