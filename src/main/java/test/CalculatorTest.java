package test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import controller.pathfinder.Dijkstra;
import controller.pathfinder.RoundCalculator;
import controller.xml.XMLDeserializer;
import controller.xml.XMLException;
import model.Calculator;
import model.DeliveryRequest;

public class CalculatorTest {
  @Test
  public void evaluatesExpression() {
    Calculator calculator = new Calculator();
    int sum = calculator.evaluate("1+2+3");
    assertEquals(6, sum);
  }
  
  // permet d'evaluer la methode getSuccessor de l'algorithme de Dijkstra 
  	//(premier pas vers le calcul de la tourné)
  @Test
  public void evaluateGetSuccessors()
  {
	  try {	
		  // ouverture de la map de test (créé a cette effet)
		  XMLDeserializer.loadMap("src/main/resources/archivePLD2016/plan2x2.xml");
	  } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	  }	  

	  Dijkstra dj = new Dijkstra();
	  
	  //recupere les successeurs du noeud d'id = 2, classé par id croissant.
	  int [] result = {1,6,7}; 
	  int [] successors = dj.getSuccessors(2);
	  Arrays.sort(successors);
	  Assert.assertArrayEquals(result, successors);
	  
	//recupere les successeurs du noeud d'id = 1, classé par id croissant.
	  int [] result1 = {2,6}; 
	  successors = dj.getSuccessors(1);
	  Arrays.sort(successors);
	  Assert.assertArrayEquals(result1, successors);
	  
	//recupere les successeurs du noeud d'id = 7, classé par id croissant.
	  successors = dj.getSuccessors(7);
	  Arrays.sort(successors);
	  Assert.assertArrayEquals(result1, successors);
  }
  
  @Test
  public void evaluateCost()
  {
	  try {	
		  // ouverture de la map de test (créé a cette effet)
		  XMLDeserializer.loadMap("src/main/resources/archivePLD2016/plan2x2.xml");
	  } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	  }	  

	  Dijkstra dj = new Dijkstra();
	  //respectivemet sommet s1, s2, s3;
	  int s1, s2, s3;
	  s1 = 2;
	  s2 = 6;
	  s3 = 7;
	  double costS1S2Estimated, costS2S1Estimated, costS1S3Estimated;
	  double costS2S3Estimated;
	  
	  //Calcul et test cout de l'"arrete" s1-s2.
	  costS1S2Estimated = 9082.0/29.0;
	  Assert.assertEquals(costS1S2Estimated,dj.computeCost(s1,s2), 0.01);
	  
	//Calcul et test cout de l'"arrete" s2-s1.
	  costS2S1Estimated = 9082.0/26.0;
	  Assert.assertEquals(costS2S1Estimated,dj.computeCost(s2,s1), 0.01);
	  
	//Calcul et test cout de l'"arrete" s1-s3.
	  costS1S3Estimated = 10257.0/38.0;
	  Assert.assertEquals(costS1S3Estimated,dj.computeCost(s1,s3), 0.01);
	  
	//Calcul et test cout de l'"arrete" s2-s3.
	  costS2S3Estimated = 5440.0/43.0;
	  Assert.assertEquals(costS2S3Estimated,dj.computeCost(s2,s3), 0.01);
	  
  }
  
//permet d'evaluer la methode getSuccessor de l'algorithme de Dijkstra 
  	//(premier pas vers le calcul de la tourné)
 @Test
 public void evaluateMinimalPath()
 {
	  try {	
		  // ouverture de la map de test (créé a cette effet)
		  XMLDeserializer.loadMap("src/main/resources/archivePLD2016/plan2x2.xml");
		
		/**
		 * recuperation de la demande de livraison 
		 */
		
	  } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	  }
	  
	  int source = 1;
	  int target = 7;
	  Dijkstra dj = new Dijkstra();
	  dj.execute(source);
	  LinkedList<Integer> result = new LinkedList<Integer>();
	  result.push(7);
	  result.push(6);
	  result.push(1);
	  
	  Assert.assertTrue(result.equals(dj.getPath(target)));
	  Assert.assertEquals(227, dj.getTargetPathCost(target));
	  
	  source = 2;
	  target = 6;
	  dj.execute(source);
	  result.clear();
	  result.push(6);
	  result.push(2);
	  
	  Assert.assertTrue(result.equals(dj.getPath(target)));
	  Assert.assertEquals(313, dj.getTargetPathCost(target));
 }
 
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
 
}
