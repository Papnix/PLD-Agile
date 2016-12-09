import controller.command.Addition;
import controller.command.CommandManager;
import controller.command.Deletion;
import controller.xml.XMLDeserializer;
import controller.xml.XMLException;
import model.Checkpoint;
import model.DeliveryRequest;
import model.Map;
import model.Round;
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
 * @author Nicolas Sorin
 */
public class TestCommandManager {
    /**
     * test method to evaluate the Command Manager and its stacks done and undone.
     */
    @Test
    public void evaluateCommandManager() throws ParserConfigurationException, SAXException, IOException, XMLException, ParseException {

        Map map = XMLDeserializer.loadMap("src/test/resources/testMap.xml");
        DeliveryRequest deliveryRequest = XMLDeserializer.loadDeliveryRequest("src/test/resources/testLivraisons.xml", map);
        Round round = new Round(deliveryRequest);
        round.computeRound(map);

        Deletion suppression = new Deletion(round, round.getRoundTimeOrder(0).get(1).getCheckpoint(),map);
        Deletion suppression1 = new Deletion(round, round.getRoundTimeOrder(0).get(2).getCheckpoint(),map);

        CommandManager commandManager = new CommandManager();

        Assert.assertEquals(0,commandManager.getDone().size());
        Assert.assertEquals(0,commandManager.getUndone().size());

        commandManager.doCommand(suppression);

        Assert.assertEquals(1, commandManager.getDone().size());
        Assert.assertEquals(suppression, commandManager.getDone().get(0));
        Assert.assertEquals(0, commandManager.getUndone().size());

        commandManager.doCommand(suppression1);

        Assert.assertEquals(2, commandManager.getDone().size());
        Assert.assertEquals(suppression, commandManager.getDone().get(0));
        Assert.assertEquals(suppression1, commandManager.getDone().get(1));
        Assert.assertEquals(0, commandManager.getUndone().size());

        commandManager.undoCommand();

        Assert.assertEquals(1, commandManager.getDone().size());
        Assert.assertEquals(suppression, commandManager.getDone().get(0));
        Assert.assertEquals(1, commandManager.getUndone().size());
        Assert.assertEquals(suppression1, commandManager.getUndone().get(0));


        commandManager.undoCommand();

        Assert.assertEquals(0, commandManager.getDone().size());
        Assert.assertEquals(2, commandManager.getUndone().size());
        Assert.assertEquals(suppression1, commandManager.getUndone().get(0));
        Assert.assertEquals(suppression, commandManager.getUndone().get(1));



        commandManager.redoCommand();

        Assert.assertEquals(1, commandManager.getDone().size());
        Assert.assertEquals(suppression, commandManager.getDone().get(0));
        Assert.assertEquals(1, commandManager.getUndone().size());
        Assert.assertEquals(suppression1, commandManager.getUndone().get(0));


        commandManager.doCommand(suppression1);

        Assert.assertEquals(2, commandManager.getDone().size());
        Assert.assertEquals(suppression1, commandManager.getDone().get(1));
        Assert.assertEquals(suppression, commandManager.getDone().get(0));
        Assert.assertEquals(0, commandManager.getUndone().size());

    }
}

