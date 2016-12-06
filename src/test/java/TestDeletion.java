import controller.Controller;
import controller.command.Deletion;
import controller.pathfinder.Dijkstra;
import controller.xml.XMLDeserializer;
import controller.xml.XMLException;
import model.DeliveryRequest;
import model.Map;
import model.Round;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedList;

/**
 *
 * @author fmacerouss
 *
 * This is a test class to validate the Deletion Command.
 */
public class TestDeletion {
	/**
	 * test method to evaluate the Deletion command and its undo/redo functions
	 */
	@Test
	public void evaluateDeleteCheckpoint() throws ParserConfigurationException, SAXException, IOException, XMLException, ParseException {
		Map map = Controller.loadMap("src/test/resources/testMap.xml");
		DeliveryRequest deliveryRequest = Controller.loadDeliveryRequest("src/test/resources/testLivraisons.xml", map);
		Round round = new Round(deliveryRequest);
		round.computeRound(map);

		Deletion deletion = new Deletion(round);
		Round newRound = deletion.doCommand(round.getArrivalTimes().get(1).getCheckpoint());

		Assert.assertEquals(3, newRound.getArrivalTimes().size());
		Assert.assertEquals(0, newRound.getArrivalTimes().get(0).getCheckpoint().getId());
		Assert.assertEquals(1, newRound.getArrivalTimes().get(1).getCheckpoint().getId());
		Assert.assertEquals(0, newRound.getArrivalTimes().get(2).getCheckpoint().getId());

		Round undoRound = deletion.undoCommand();
		Assert.assertEquals(round.getArrivalTimes().size(), undoRound.getArrivalTimes().size());
		Assert.assertEquals(round.getArrivalTimes().get(0).getCheckpoint().getId(), undoRound.getArrivalTimes().get(0).getCheckpoint().getId());
		Assert.assertEquals(round.getArrivalTimes().get(1).getCheckpoint().getId(), undoRound.getArrivalTimes().get(1).getCheckpoint().getId());
		Assert.assertEquals(round.getArrivalTimes().get(2).getCheckpoint().getId(), undoRound.getArrivalTimes().get(2).getCheckpoint().getId());
		Assert.assertEquals(round.getArrivalTimes().get(3).getCheckpoint().getId(), undoRound.getArrivalTimes().get(3).getCheckpoint().getId());

		Round redoRound = deletion.redoCommand();
		Assert.assertEquals(redoRound.getArrivalTimes().size(), newRound.getArrivalTimes().size());
		Assert.assertEquals(redoRound.getArrivalTimes().get(0).getCheckpoint().getId(), newRound.getArrivalTimes().get(0).getCheckpoint().getId());
		Assert.assertEquals(redoRound.getArrivalTimes().get(1).getCheckpoint().getId(), newRound.getArrivalTimes().get(1).getCheckpoint().getId());
		Assert.assertEquals(redoRound.getArrivalTimes().get(2).getCheckpoint().getId(), newRound.getArrivalTimes().get(2).getCheckpoint().getId());

	}
}
