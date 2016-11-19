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
  
  // permet d'evaluer la methode getSuccessor de l'algorithme de Dijkstra (premier pas vers le calcul de la tourné)
  @Test
  public void evaluateGetSuccessors()
  {
	  try {	
	  // ouverture de la map de test (créé a cette effet)
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
	  //recupere les successeurs du noeud d'id = 2, classé par id croissant.
	  int [] result = {1,6,7}; 
	  int [] successors = Dijkstra.getSuccessors(2);
	  Arrays.sort(successors);
	  Arrays.sort(result);
	  //assertEquals(result, successors);
	  Assert.assertArrayEquals(result, successors);
	  
	//recupere les successeurs du noeud d'id = 1, classé par id croissant.
	  int [] result1 = {2,6}; 
	  successors = Dijkstra.getSuccessors(1);
	  Arrays.sort(successors);
	  Arrays.sort(result1);
	  //assertEquals(result1, successors);
	  Assert.assertArrayEquals(result1, successors);
	  
	//recupere les successeurs du noeud d'id = 7, classé par id croissant.
	  successors = Dijkstra.getSuccessors(7);
	  Arrays.sort(successors);
	  //assertEquals(result1, successors);
	  Assert.assertArrayEquals(result1, successors);
  }
  
//permet d'evaluer la methode getSuccessor de l'algorithme de Dijkstra (premier pas vers le calcul de la tourné)
 @Test
 public void evaluateMinimalPath()
 {
	  try {	
	  // ouverture de la map de test (créé a cette effet)
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
