import static org.junit.Assert.assertEquals;

import controller.xml.XMLDeserializer;
import model.DeliveryRequest;
import model.Map;
import org.junit.Test;

public class XMLTest {

    @Test
    public void deserializeMapXML() {
        try {
            XMLDeserializer.loadMap("src/test/resources/testMap.xml");
        } catch(Exception e) {
            e.printStackTrace();
        }

        assertEquals(134, Map.getInstance().getWaypoint(0).getxCoord());
        assertEquals(11218, Map.getInstance().getSection(1, 2).getLength());
    }

    @Test
    public void deserializeDeliveryRequestXML() {
        try {
            XMLDeserializer.loadDeliveryRequest("src/test/resources/testLivraisons.xml");
        } catch(Exception e) {
            e.printStackTrace();
        }

        assertEquals(0, DeliveryRequest.getInstance().getWarehouse().getAssociatedWaypoint().getId());
        assertEquals(1, DeliveryRequest.getInstance().getDeliveryPoint(0).getAssociatedWaypoint().getId());
    }

}
