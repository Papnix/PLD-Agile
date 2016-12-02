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

}
