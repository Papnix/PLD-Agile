package test;

import java.util.Map.Entry;

import org.junit.Test;

import controller.xml.XMLDeserializer;
import model.Checkpoint;
import model.DeliveryRequest;
import model.Map;
import model.Section;
import model.Waypoint;

public class TestXMLDeserializer {

	


	@Test
	public void testLoadMap() {
		Map map = new Map();
		try {
			// ouverture de la map de test (créé a cette effet)
			XMLDeserializer.loadMap("src/main/resources/archivePLD2016/plan2x2.xml", map);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("\n\n testLoadMap \n ");
		
		for(Section section : map.getAllSections() ){
			System.out.println(section.toString());
		}
		
		for(Entry<Integer, Waypoint> waypoint : map.getWaypoints().entrySet() ){
			System.out.println(waypoint.getValue().toString());
		}
	}
	
	@Test
	public void testLoadDeliveryRequest() {
		Map map = new Map();
		DeliveryRequest request = new DeliveryRequest();
		
		try {
			// ouverture de la map de test (créé a cette effet)
			XMLDeserializer.loadMap("src/main/resources/archivePLD2016/plan2x2.xml", map);
			XMLDeserializer.loadDeliveryRequest("src/main/resources/archivePLD2016/livraisons2x2.xml", map, request);
		} catch (Exception e) {

			e.printStackTrace();
		}

		System.out.println("\n\n testLoadDeliveryRequest \n ");
		
		for(Checkpoint point : request.getDeliveryPointList()) {
			System.out.println(point.toString());
		}		
	}
	
}
