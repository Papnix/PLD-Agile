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

public class Controller {

    private CommandManager commandManager;

    public Controller() {
        this.commandManager = new CommandManager();
    }

    public Round computeRound(DeliveryRequest request, Map map) {
        Round round = new Round(request);
        round.computeRound(map);
        return round;
    }

    public static Map loadMap(String path) throws ParserConfigurationException, SAXException, IOException, XMLException {
        return XMLDeserializer.loadMap(path);
    }

    public static DeliveryRequest loadDeliveryRequest(String path, Map map) throws ParserConfigurationException, SAXException, IOException, XMLException, ParseException {
        return XMLDeserializer.loadDeliveryRequest(path, map);
    }

    public void deleteCheckpoint(Checkpoint checkpoint, Round round) {
        this.commandManager.doCommand(new Deletion(checkpoint), round);
    }

    public void addCheckpoint(Checkpoint checkpoint, Round round) {
        this.commandManager.doCommand(new Addition(checkpoint), round);
    }

    public void changeCheckpointTime(Checkpoint checkpoint, Round round) {
        this.commandManager.doCommand(new TimeChange(checkpoint), round);
    }

    public void undoLastCommand(Round round) {
        this.commandManager.undoCommand(round);
    }

    public void redoLastCommand(Round round) {
        this.commandManager.redoCommand(round);
    }

}
