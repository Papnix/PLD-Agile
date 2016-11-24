package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import controller.pathfinder.RoundCalculator;
import controller.xml.XMLDeserializer;
import controller.xml.XMLException;
import model.DeliveryRequest;
import model.Map;
import model.Section;

public class RoundCalculatorTest {
	@Test
	 public void evaluatePaths(){
		 try {	
			  // ouverture de la map de test (créé a cette effet)
			  XMLDeserializer.loadMap("src/main/resources/archivePLD2016/plan5x5.xml");
		  } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		  }	
		 
		 try {
			XMLDeserializer.loadDeliveryRequest("src/main/resources/archivePLD2016/livraisons5x5-4.xml");
		} catch (ParserConfigurationException | SAXException | IOException | XMLException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 DeliveryRequest req = DeliveryRequest.getInstance();
		 
		 RoundCalculator rc = new RoundCalculator(req);
		 
		 rc.computePaths();
		 
		 assertEquals(rc.getCost(1, 1), 0);
		 assertEquals(rc.getCost(3, 3), 0);
		 assertEquals(rc.getCost(9, 9), 0);
		 assertEquals(rc.getCost(13, 13), 0);
		 assertEquals(rc.getCost(21, 21), 0);
		 
		 assertEquals(rc.getCost(1, 3), 388);
		 assertEquals(rc.getCost(1, 9), 879);
		 assertEquals(rc.getCost(1, 13), 788);
		 assertEquals(rc.getCost(1, 21), 1426);
		 
		 assertEquals(rc.getCost(3, 1), 396);
		 assertEquals(rc.getCost(3, 9), 491);
		 assertEquals(rc.getCost(3, 13), 931);
		 assertEquals(rc.getCost(3, 21), 1822);
		 
		 assertEquals(rc.getCost(9, 1), 888);
		 assertEquals(rc.getCost(9, 3), 492);
		 assertEquals(rc.getCost(9, 13), 1423);
		 assertEquals(rc.getCost(9, 21), 2314);
		 
		 assertEquals(rc.getCost(13, 1), 721);
		 assertEquals(rc.getCost(13, 3), 881);
		 assertEquals(rc.getCost(13, 9), 1372);
		 assertEquals(rc.getCost(13, 21), 1305);
		 
		 assertEquals(rc.getCost(21, 1), 1539);
		 assertEquals(rc.getCost(21, 3), 1927);
		 assertEquals(rc.getCost(21, 9), 2418);
		 assertEquals(rc.getCost(21, 13), 1443);
	 }
	
	@Test
	public void evaluateRoundCalculator(){
		try {	
			  // ouverture de la map de test (créé a cette effet)
			  XMLDeserializer.loadMap("src/main/resources/archivePLD2016/plan5x5.xml");
		  } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		  }	
		 
		 try {
			XMLDeserializer.loadDeliveryRequest("src/main/resources/archivePLD2016/livraisons5x5-4.xml");
		} catch (ParserConfigurationException | SAXException | IOException | XMLException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 DeliveryRequest req = DeliveryRequest.getInstance();
		 
		 RoundCalculator rc = new RoundCalculator(req);
		 
		 rc.computePaths();
		 int [] tabRound = rc.computeRound();
		 int[] expectedRound = {21, 13, 3, 9, 1, 21};
		 
		 Assert.assertArrayEquals(tabRound, expectedRound);
		 
		 Map map = Map.getInstance();
		 
		 List<Section> activeSections = map.getActiveSections();
		 
		 List<Section> expectedActive = new ArrayList<Section>();
		 
		 expectedActive.add(map.getSection(21, 16));
		 expectedActive.add(map.getSection(16, 11));
		 expectedActive.add(map.getSection(11, 12));
		 expectedActive.add(map.getSection(12, 13));
		 expectedActive.add(map.getSection(13, 8));
		 expectedActive.add(map.getSection(8, 7));
		 expectedActive.add(map.getSection(7, 2));
		 expectedActive.add(map.getSection(2, 3));
		 expectedActive.add(map.getSection(3, 4));
		 expectedActive.add(map.getSection(4, 9));
		 expectedActive.add(map.getSection(9, 4));
		 expectedActive.add(map.getSection(4, 3));
		 expectedActive.add(map.getSection(3, 2));
		 expectedActive.add(map.getSection(2, 1));
		 expectedActive.add(map.getSection(1, 0));
		 expectedActive.add(map.getSection(0, 5));
		 expectedActive.add(map.getSection(5, 10));
		 expectedActive.add(map.getSection(10, 11));
		 expectedActive.add(map.getSection(11, 16));
		 expectedActive.add(map.getSection(16, 21));
		 
		 assertTrue(activeSections.containsAll(expectedActive) && expectedActive.containsAll(activeSections));
	}
}
