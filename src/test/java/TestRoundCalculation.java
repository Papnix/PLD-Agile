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

/**
 * 
 * @author fmacerouss
 *
 *         This class tests the RoundCalculator (it checks the correctness of
 *         the best round and the cost of each step).
 */

public class TestRoundCalculation {

	/**
	 * This method checks that the cost of the paths between each node of the
	 * round is correct.
	 */
	@Test
	public void testCostCalculation() {
		Map map = new Map();
		DeliveryRequest request = new DeliveryRequest();
		try {
			// ouverture de la map de test (créé a cette effet)
			XMLDeserializer.loadMap("src/main/resources/archivePLD2016/plan5x5.xml", map);
			XMLDeserializer.loadDeliveryRequest("src/main/resources/archivePLD2016/livraisons5x5-4.xml", map, request);
		} catch (ParserConfigurationException | SAXException | IOException | XMLException | ParseException e) {
			e.printStackTrace();
		}

		Round round = new Round(request);

		round.computePaths(map);

		assertEquals(round.getCost(1, 1), 0);
		assertEquals(round.getCost(3, 3), 0);
		assertEquals(round.getCost(9, 9), 0);
		assertEquals(round.getCost(13, 13), 0);
		assertEquals(round.getCost(21, 21), 0);

		assertEquals(round.getCost(1, 3), 388);
		assertEquals(round.getCost(1, 9), 879);
		assertEquals(round.getCost(1, 13), 788);
		assertEquals(round.getCost(1, 21), 1426);

		assertEquals(round.getCost(3, 1), 396);
		assertEquals(round.getCost(3, 9), 491);
		assertEquals(round.getCost(3, 13), 931);
		assertEquals(round.getCost(3, 21), 1822);

		assertEquals(round.getCost(9, 1), 888);
		assertEquals(round.getCost(9, 3), 492);
		assertEquals(round.getCost(9, 13), 1423);
		assertEquals(round.getCost(9, 21), 2314);

		assertEquals(round.getCost(13, 1), 721);
		assertEquals(round.getCost(13, 3), 881);
		assertEquals(round.getCost(13, 9), 1372);
		assertEquals(round.getCost(13, 21), 1305);

		assertEquals(round.getCost(21, 1), 1539);
		assertEquals(round.getCost(21, 3), 1927);
		assertEquals(round.getCost(21, 9), 2418);
		assertEquals(round.getCost(21, 13), 1443);
	}

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
			XMLDeserializer.loadMap("src/main/resources/archivePLD2016/plan5x5.xml", map);
			XMLDeserializer.loadDeliveryRequest("src/main/resources/archivePLD2016/livraisons5x5-4.xml", map, request);
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
		for (DeliveryTime deliveryTime : round.getArrivalTimes()) {
			CheckpointId.add(deliveryTime.getCheckpoint().getId());
		}

		Assert.assertTrue(expectedRound.containsAll(CheckpointId) && CheckpointId.containsAll(expectedRound));
	}
}
