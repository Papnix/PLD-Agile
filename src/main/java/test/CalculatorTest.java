package test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

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
  public void evaluateGetSuccessor()
  {
	  try {	
	  // ouverture de la map de test (créé a cette effet)
		XMLDeserializer.loadMap("src/main/ressources/plan2x2.xml");
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
	  int [] successor = Dijkstra.getSuccessor(2);
	  assertEquals(result, successor);
	  
	//recupere les successeurs du noeud d'id = 1, classé par id croissant.
	  int [] result1 = {2,6}; 
	  successor = Dijkstra.getSuccessor(1);
	  assertEquals(result1, successor);
	  
	//recupere les successeurs du noeud d'id = 7, classé par id croissant.
	  successor = Dijkstra.getSuccessor(7);
	  assertEquals(result1, successor);
  }
  
//permet d'evaluer la methode getSuccessor de l'algorithme de Dijkstra (premier pas vers le calcul de la tourné)
 @Test
 public void evaluateMinimalPath()
 {
	  try {	
	  // ouverture de la map de test (créé a cette effet)
		XMLDeserializer.loadMap("src/main/ressources/plan2x2.xml");
		
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
