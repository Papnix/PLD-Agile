import controller.command.Deletion;

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

/**
 * @author Nicolas Sorin
 */
public class TestDeletion {
    /**
     * test method to evaluate the Deletion command and its undo/redo functions
     */
    @Test
    public void evaluateDeleteCheckpoint() throws ParserConfigurationException, SAXException, IOException, XMLException, ParseException {

        Map map = XMLDeserializer.loadMap("src/test/resources/testMap.xml");
        DeliveryRequest deliveryRequest = XMLDeserializer.loadDeliveryRequest("src/test/resources/testLivraisons.xml", map);
        Round round = new Round(deliveryRequest);
        round.computeRound(map);

        Deletion deletion = new Deletion(round, round.getRoundTimeOrder(0).get(1).getCheckpoint(), map);

        Round newRound = deletion.doCommand();

        Assert.assertEquals(3, newRound.getRoundTimeOrder(0).size());
        Assert.assertEquals(0, newRound.getRoundTimeOrder(0).get(0).getCheckpoint().getId());
        Assert.assertEquals(2, newRound.getRoundTimeOrder(0).get(1).getCheckpoint().getId());
        Assert.assertEquals(0, newRound.getRoundTimeOrder(0).get(2).getCheckpoint().getId());

        Round undoRound = deletion.undoCommand();
        Assert.assertEquals(round.getRoundTimeOrder(0).size(), undoRound.getRoundTimeOrder(0).size());
        Assert.assertEquals(round.getRoundTimeOrder(0).get(0).getCheckpoint().getId(), undoRound.getRoundTimeOrder(0).get(0).getCheckpoint().getId());
        Assert.assertEquals(round.getRoundTimeOrder(0).get(1).getCheckpoint().getId(), undoRound.getRoundTimeOrder(0).get(1).getCheckpoint().getId());
        Assert.assertEquals(round.getRoundTimeOrder(0).get(2).getCheckpoint().getId(), undoRound.getRoundTimeOrder(0).get(2).getCheckpoint().getId());
        Assert.assertEquals(round.getRoundTimeOrder(0).get(3).getCheckpoint().getId(), undoRound.getRoundTimeOrder(0).get(3).getCheckpoint().getId());

        Round redoRound = deletion.redoCommand();
        Assert.assertEquals(redoRound.getRoundTimeOrder(0).size(), newRound.getRoundTimeOrder(0).size());
        Assert.assertEquals(redoRound.getRoundTimeOrder(0).get(0).getCheckpoint().getId(), newRound.getRoundTimeOrder(0).get(0).getCheckpoint().getId());
        Assert.assertEquals(redoRound.getRoundTimeOrder(0).get(1).getCheckpoint().getId(), newRound.getRoundTimeOrder(0).get(1).getCheckpoint().getId());
        Assert.assertEquals(redoRound.getRoundTimeOrder(0).get(2).getCheckpoint().getId(), newRound.getRoundTimeOrder(0).get(2).getCheckpoint().getId());

    }
}
