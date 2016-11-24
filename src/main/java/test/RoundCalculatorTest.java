package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import controller.pathfinder.Dijkstra;
import controller.pathfinder.RoundCalculator;
import controller.xml.XMLDeserializer;
import controller.xml.XMLException;
import model.DeliveryRequest;

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
		 
		 Dijkstra dj = new Dijkstra();
		 
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
		 
		 Dijkstra dj = new Dijkstra();
		 
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
		 int [] expected1 = {21,13,3,9,1,21};
		 int [] expected2 = {21,13,9,3,1,21};
		 assertTrue((Arrays.equals(expected1, tabRound) | Arrays.equals(expected2, tabRound)));
		 
		 
	}
}
