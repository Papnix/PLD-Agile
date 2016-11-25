package test;

import java.util.Map.Entry;

import org.junit.Test;

import controller.xml.XMLDeserializer;
import model.Map;
import model.Section;
import model.Waypoint;

public class XMLtest {

	/**
	 * test method to evaluate all the successor of a node of a graph.
	 */
	@Test
	public void testLoad() {
		Map map = new Map();
		try {
	// ouverture de la map de test (créé a cette effet)
			XMLDeserializer.loadMap("src/main/resources/archivePLD2016/plan2x2.xml", map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for(Section section : map.getAllSections() ){
			System.out.println(section.toString());
		}
		
		for(Entry<Integer, Waypoint> waypoint : map.getWaypoints().entrySet() ){
			System.out.println(waypoint.getValue().toString());
		}
		
	}
	
}
