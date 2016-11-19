package test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import controller.pathfinder.Dijkstra;
import controller.xml.XMLDeserializer;
import controller.xml.XMLException;
import model.Calculator;

public class CalculatorTest {
  @Test
  public void evaluatesExpression() {
    Calculator calculator = new Calculator();
    int sum = calculator.evaluate("1+2+3");
    assertEquals(6, sum);
  }
  
  // permet d'evaluer la methode getSuccessor de l'algorithme de Dijkstra (premier pas vers le calcul de la tourn�)
  @Test
  public void evaluateGetSuccessors()
  {
	  try {	
	  // ouverture de la map de test (cr�� a cette effet)
		XMLDeserializer.loadMap("src/main/resources/archivePLD2016/plan2x2.xml");
	} catch (ParserConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SAXException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (XMLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  //recupere les successeurs du noeud d'id = 2, class� par id croissant.
	  int [] result = {1,6,7}; 
	  int [] successors = Dijkstra.getSuccessors(2);
	  Arrays.sort(successors);
	  Assert.assertArrayEquals(result, successors);
	  
	//recupere les successeurs du noeud d'id = 1, class� par id croissant.
	  int [] result1 = {2,6}; 
	  successors = Dijkstra.getSuccessors(1);
	  Arrays.sort(successors);
	  Assert.assertArrayEquals(result1, successors);
	  
	//recupere les successeurs du noeud d'id = 7, class� par id croissant.
	  successors = Dijkstra.getSuccessors(7);
	  Arrays.sort(successors);
	  Assert.assertArrayEquals(result1, successors);
  }
  
  @Test
  public void evaluateCost()
  {
	  try {	
		  // ouverture de la map de test (cr�� a cette effet)
			XMLDeserializer.loadMap("src/main/resources/archivePLD2016/plan2x2.xml");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  
	  //respectivemet sommet s1, s2, s3;
	  int s1, s2, s3;
	  s1 = 2;
	  s2 = 6;
	  s3 = 7;
	  double costS1S2Estimated, costS2S1Estimated, costS1S3Estimated, costS2S3Estimated;
	  
	  //Calcul et test cout de l'"arrete" s1-s2.
	  costS1S2Estimated = 9082/29;
	  Assert.assertEquals(costS1S2Estimated,Dijkstra.calculateCost(s1,s2), 0.0001);
	  
	//Calcul et test cout de l'"arrete" s2-s1.
	  costS2S1Estimated = 9082/26;
	  Assert.assertEquals(costS2S1Estimated,Dijkstra.calculateCost(s2,s1), 0.0001);
	  
	//Calcul et test cout de l'"arrete" s1-s3.
	  costS1S3Estimated = 10257/38;
	  Assert.assertEquals(costS1S3Estimated,Dijkstra.calculateCost(s1,s3), 0.0001);
	  
	//Calcul et test cout de l'"arrete" s2-s3.
	  costS2S3Estimated = 5440/43;
	  Assert.assertEquals(costS2S3Estimated,Dijkstra.calculateCost(s2,s3), 0.0001);
	  
  }
  
//permet d'evaluer la methode getSuccessor de l'algorithme de Dijkstra (premier pas vers le calcul de la tourn�)
 @Test
 public void evaluateMinimalPath()
 {
	  try {	
	  // ouverture de la map de test (cr�� a cette effet)
		XMLDeserializer.loadMap("src/main/resources/archivePLD2016/plan2x2.xml");
		
		/**
		 * recuperation de la demande de livraison 
		 */
		
	} catch (ParserConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SAXException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (XMLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  
 }
 
}
