import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import controller.xml.XMLDeserializer;
import model.Map;
import org.junit.Test;

public class XMLTest {

    @Test
    public void deserializeXML() {
        try {
            XMLDeserializer.loadMap(getClass().getResource("../testMap.xml").getPath());
            assertEquals(134, Map.getInstance().getWaypoint(0).getxCoord());
            assertEquals(9234, Map.getInstance().getSection(0, 1).getLength());
            Map.getInstance().clear();
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}
