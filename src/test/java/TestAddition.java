import controller.command.Addition;
import controller.xml.XMLDeserializer;
import controller.xml.XMLException;
import model.*;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**

 * @author hhumbert1
 *         <p>
 *         This is a test class to validate the Addition Command.
 */
public class TestAddition {
    /**
     * test method to evaluate the Addition command and its undo/redo functions
     */
    @Test
    public void evaluateAddCheckpoint() throws ParserConfigurationException, SAXException, IOException, XMLException, ParseException {
        Map map = XMLDeserializer.loadMap("src/main/resources/archivePLD2016/plan10x10.xml");
        DeliveryRequest deliveryRequest = XMLDeserializer.loadDeliveryRequest("src/main/resources/archivePLD2016/livraisons10x10-9-TW.xml", map);
        Round round = new Round(deliveryRequest);
        round.computeRound(map);

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        Date start = dateFormat.parse("10:0:0");
        Date end = dateFormat.parse("12:0:0");

        Checkpoint checkpoint = new Checkpoint(map.getWaypoint(94),300, start, end);

        Addition addition = new Addition(round, map, checkpoint);

        Round newRound = addition.doCommand();
        Round undoRound = addition.undoCommand();
        Round redoRound = addition.redoCommand();

        Assert.assertEquals(11, round.getRoundTimeOrder(0).size());
        Assert.assertEquals(51, round.getRoundTimeOrder(0).get(0).getCheckpoint().getId());
        Assert.assertEquals(56, round.getRoundTimeOrder(0).get(1).getCheckpoint().getId());
        Assert.assertEquals(72, round.getRoundTimeOrder(0).get(2).getCheckpoint().getId());
        Assert.assertEquals(39, round.getRoundTimeOrder(0).get(3).getCheckpoint().getId());
        Assert.assertEquals(66, round.getRoundTimeOrder(0).get(4).getCheckpoint().getId());
        Assert.assertEquals(95, round.getRoundTimeOrder(0).get(5).getCheckpoint().getId());
        Assert.assertEquals(29, round.getRoundTimeOrder(0).get(6).getCheckpoint().getId());
        Assert.assertEquals(47, round.getRoundTimeOrder(0).get(7).getCheckpoint().getId());
        Assert.assertEquals(68, round.getRoundTimeOrder(0).get(8).getCheckpoint().getId());
        Assert.assertEquals(59, round.getRoundTimeOrder(0).get(9).getCheckpoint().getId());
        Assert.assertEquals(51, round.getRoundTimeOrder(0).get(10).getCheckpoint().getId());

        Assert.assertEquals(12, newRound.getRoundTimeOrder(0).size());
        Assert.assertEquals(51, newRound.getRoundTimeOrder(0).get(0).getCheckpoint().getId());
        Assert.assertEquals(56, newRound.getRoundTimeOrder(0).get(1).getCheckpoint().getId());
        Assert.assertEquals(72, newRound.getRoundTimeOrder(0).get(2).getCheckpoint().getId());
        Assert.assertEquals(39, newRound.getRoundTimeOrder(0).get(3).getCheckpoint().getId());
        Assert.assertEquals(66, newRound.getRoundTimeOrder(0).get(4).getCheckpoint().getId());
        Assert.assertEquals(94, newRound.getRoundTimeOrder(0).get(5).getCheckpoint().getId());
        Assert.assertEquals(95, newRound.getRoundTimeOrder(0).get(6).getCheckpoint().getId());
        Assert.assertEquals(29, newRound.getRoundTimeOrder(0).get(7).getCheckpoint().getId());
        Assert.assertEquals(47, newRound.getRoundTimeOrder(0).get(8).getCheckpoint().getId());
        Assert.assertEquals(68, newRound.getRoundTimeOrder(0).get(9).getCheckpoint().getId());
        Assert.assertEquals(59, newRound.getRoundTimeOrder(0).get(10).getCheckpoint().getId());
        Assert.assertEquals(51, newRound.getRoundTimeOrder(0).get(11).getCheckpoint().getId());


        Assert.assertEquals(11, undoRound.getRoundTimeOrder(0).size());
        Assert.assertEquals(round.getRoundTimeOrder(0).get(0).getCheckpoint().getId(), undoRound.getRoundTimeOrder(0).get(0).getCheckpoint().getId());
        Assert.assertEquals(round.getRoundTimeOrder(0).get(1).getCheckpoint().getId(), undoRound.getRoundTimeOrder(0).get(1).getCheckpoint().getId());
        Assert.assertEquals(round.getRoundTimeOrder(0).get(2).getCheckpoint().getId(), undoRound.getRoundTimeOrder(0).get(2).getCheckpoint().getId());
        Assert.assertEquals(round.getRoundTimeOrder(0).get(3).getCheckpoint().getId(), undoRound.getRoundTimeOrder(0).get(3).getCheckpoint().getId());
        Assert.assertEquals(round.getRoundTimeOrder(0).get(4).getCheckpoint().getId(), undoRound.getRoundTimeOrder(0).get(4).getCheckpoint().getId());
        Assert.assertEquals(round.getRoundTimeOrder(0).get(5).getCheckpoint().getId(), undoRound.getRoundTimeOrder(0).get(5).getCheckpoint().getId());
        Assert.assertEquals(round.getRoundTimeOrder(0).get(6).getCheckpoint().getId(), undoRound.getRoundTimeOrder(0).get(6).getCheckpoint().getId());
        Assert.assertEquals(round.getRoundTimeOrder(0).get(7).getCheckpoint().getId(), undoRound.getRoundTimeOrder(0).get(7).getCheckpoint().getId());
        Assert.assertEquals(round.getRoundTimeOrder(0).get(8).getCheckpoint().getId(), undoRound.getRoundTimeOrder(0).get(8).getCheckpoint().getId());
        Assert.assertEquals(round.getRoundTimeOrder(0).get(9).getCheckpoint().getId(), undoRound.getRoundTimeOrder(0).get(9).getCheckpoint().getId());
        Assert.assertEquals(round.getRoundTimeOrder(0).get(10).getCheckpoint().getId(), undoRound.getRoundTimeOrder(0).get(10).getCheckpoint().getId());

        Assert.assertEquals(12, redoRound.getRoundTimeOrder(0).size());
        Assert.assertEquals(newRound.getRoundTimeOrder(0).get(0).getCheckpoint().getId(), redoRound.getRoundTimeOrder(0).get(0).getCheckpoint().getId());
        Assert.assertEquals(newRound.getRoundTimeOrder(0).get(1).getCheckpoint().getId(), redoRound.getRoundTimeOrder(0).get(1).getCheckpoint().getId());
        Assert.assertEquals(newRound.getRoundTimeOrder(0).get(2).getCheckpoint().getId(), redoRound.getRoundTimeOrder(0).get(2).getCheckpoint().getId());
        Assert.assertEquals(newRound.getRoundTimeOrder(0).get(3).getCheckpoint().getId(), redoRound.getRoundTimeOrder(0).get(3).getCheckpoint().getId());
        Assert.assertEquals(newRound.getRoundTimeOrder(0).get(4).getCheckpoint().getId(), redoRound.getRoundTimeOrder(0).get(4).getCheckpoint().getId());
        Assert.assertEquals(newRound.getRoundTimeOrder(0).get(5).getCheckpoint().getId(), redoRound.getRoundTimeOrder(0).get(5).getCheckpoint().getId());
        Assert.assertEquals(newRound.getRoundTimeOrder(0).get(6).getCheckpoint().getId(), redoRound.getRoundTimeOrder(0).get(6).getCheckpoint().getId());
        Assert.assertEquals(newRound.getRoundTimeOrder(0).get(7).getCheckpoint().getId(), redoRound.getRoundTimeOrder(0).get(7).getCheckpoint().getId());
        Assert.assertEquals(newRound.getRoundTimeOrder(0).get(8).getCheckpoint().getId(), redoRound.getRoundTimeOrder(0).get(8).getCheckpoint().getId());
        Assert.assertEquals(newRound.getRoundTimeOrder(0).get(9).getCheckpoint().getId(), redoRound.getRoundTimeOrder(0).get(9).getCheckpoint().getId());
        Assert.assertEquals(newRound.getRoundTimeOrder(0).get(10).getCheckpoint().getId(), redoRound.getRoundTimeOrder(0).get(10).getCheckpoint().getId());
        Assert.assertEquals(newRound.getRoundTimeOrder(0).get(11).getCheckpoint().getId(), redoRound.getRoundTimeOrder(0).get(11).getCheckpoint().getId());

    }
}