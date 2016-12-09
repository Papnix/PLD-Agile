import java.util.Map.Entry;

import org.junit.Test;

import controller.xml.XMLDeserializer;
import model.Checkpoint;
import model.DeliveryRequest;
import model.Map;
import model.Section;
import model.Waypoint;

/**
 * @author Nicolas Sorin
 */
public class TestXMLDeserializer {

    /**
     * Test loading a Map from a XML file.
     */
    @Test
    public void testLoadMap() {
        Map map = new Map();
        try {
            // ouverture de la map de test (cr�� a cette effet)
            map = XMLDeserializer.loadMap("src/main/resources/archivePLD2016/plan2x2.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\n\n testLoadMap \n ");

        for (Section section : map.getAllSections()) {
            System.out.println(section.toString());
        }

        for (Entry<Integer, Waypoint> waypoint : map.getWaypoints().entrySet()) {
            System.out.println(waypoint.getValue().toString());
        }
    }

    /**
     * Test loading a DeliveryRequest from a XML file.
     */
    @Test
    public void testLoadDeliveryRequest() {
        Map map;
        DeliveryRequest request = new DeliveryRequest();

        try {
            // ouverture de la map de test (cr�� a cette effet)
            map = XMLDeserializer.loadMap("src/main/resources/archivePLD2016/plan2x2.xml");
            request = XMLDeserializer.loadDeliveryRequest("src/main/resources/archivePLD2016/livraisons2x2.xml", map);
        } catch (Exception e) {

            e.printStackTrace();
        }

        System.out.println("\n\n testLoadDeliveryRequest \n ");

        for (Checkpoint point : request.getDeliveryPointList()) {
            System.out.println(point.toString());
        }
    }
}
