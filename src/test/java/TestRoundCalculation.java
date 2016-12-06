import static org.junit.Assert.*;

import java.io.IOException;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import controller.xml.XMLDeserializer;
import controller.xml.XMLException;
import model.DeliveryRequest;
import model.DeliveryTime;
import model.Map;
import model.Round;
import model.Section;

/**
 * 
 * @author fmacerouss
 *
 *         This class tests the RoundCalculator (it checks the correctness of
 *         the best round and the cost of each step).
 */

public class TestRoundCalculation {

	/**
	 * This method checks thats the RoundCalculator gives the best round, and
	 * sets active the associated sections.
	 */
	@Test
	public void testRoundCalculation() {
		Map map = new Map();
		DeliveryRequest request = new DeliveryRequest();

		try {
			// ouverture de la map de test (créé a cette effet)
			map = XMLDeserializer.loadMap("src/main/resources/archivePLD2016/plan5x5.xml");
			request = XMLDeserializer.loadDeliveryRequest("src/main/resources/archivePLD2016/livraisons5x5-4.xml", map);
		} catch (ParserConfigurationException | SAXException | IOException | XMLException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Round round = new Round(request);

		round.computePaths(map);
		round.computeRound(map);

		List<Integer> expectedRound = new ArrayList<Integer>();

		expectedRound.add(21);
		expectedRound.add(13);
		expectedRound.add(3);
		expectedRound.add(9);
		expectedRound.add(1);
		expectedRound.add(21);

		List<Integer> CheckpointId = new ArrayList<Integer>();
		for (DeliveryTime deliveryTime : round.getRoundTimeOrder(0)) {
			CheckpointId.add(deliveryTime.getCheckpoint().getId());
		}

		Assert.assertTrue(expectedRound.containsAll(CheckpointId) && CheckpointId.containsAll(expectedRound));
	}

	/**
	 * This method checks the respect of the time range of the round compute by
	 * the TSP. check for one node, if departure time is superior of arrival
	 * time. check for two successive node, if departure time of the first node
	 * is inferior of arrival time of the successor.
	 */
	@Test
	public void testOrderOfRound() {
		Map map = new Map();
		DeliveryRequest request = new DeliveryRequest();

		try {
			// ouverture de la map de test (créé a cette effet)
			map = XMLDeserializer.loadMap("src/main/resources/archivePLD2016/plan10x10.xml");
			request = XMLDeserializer.loadDeliveryRequest("src/main/resources/archivePLD2016/livraisons10x10-9-TW.xml",
					map);
		} catch (ParserConfigurationException | SAXException | IOException | XMLException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Round round = new Round(request);

		round.computePaths(map);
		round.computeRound(map);

		for (int i = 0; i < round.getNumOfRound(); i++) {
			List<DeliveryTime> roundList = round.getRoundTimeOrder(i);
			Assert.assertTrue(roundList.get(0).getArrivalTime() != null);
			for (int j = 1; j < roundList.size()-1; j++) {
				Assert.assertTrue(
						roundList.get(j).getArrivalTime().getTime() < roundList.get(j).getDepartureTime().getTime());
				Assert.assertTrue(roundList.get(j).getDepartureTime().getTime() < roundList.get(j + 1).getArrivalTime()
						.getTime());
			}
		}
	}

	@Test
	public void testRouteSuccession() {
		Map map = new Map();
		DeliveryRequest request = new DeliveryRequest();

		try {
			// ouverture de la map de test (créé a cette effet)
			map = XMLDeserializer.loadMap("src/main/resources/archivePLD2016/plan10x10.xml");
			request = XMLDeserializer.loadDeliveryRequest("src/main/resources/archivePLD2016/livraisons10x10-9-TW.xml",
					map);
		} catch (ParserConfigurationException | SAXException | IOException | XMLException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Round round = new Round(request);

		round.computePaths(map);
		round.computeRound(map);

		for (int i = 0; i < round.getNumOfRound(); i++) {
			List<Section> routeList = round.getRoute(i);
			for (int j = 0; j < routeList.size() - 1; j++) {
				Assert.assertEquals(routeList.get(j).getDestination().getId(),
						routeList.get(j + 1).getOrigin().getId());
				// System.out.println(routeList.get(j+1).getOrigin().getId() +"
				// -> "+ routeList.get(j).getDestination().getId());
			}
		}
	}

}
