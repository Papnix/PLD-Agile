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

public class Controller {	
	
	public Round computeRound(DeliveryRequest request, Map map){
		Round round = new Round(request);
		round.computeRound(map);
		return round;
	}
	
	public static Map loadMap(String path) throws ParserConfigurationException, SAXException, IOException, XMLException{
		Map map = new Map();
		XMLDeserializer.loadMap(path, map);
		return map;
	}
	
	public static DeliveryRequest loadDeliveryRequest(String path, Map map) throws ParserConfigurationException, SAXException, IOException, XMLException, ParseException{
		DeliveryRequest request = new DeliveryRequest();
		XMLDeserializer.loadDeliveryRequest(path, map, request);
		return request;
	}
	
}
