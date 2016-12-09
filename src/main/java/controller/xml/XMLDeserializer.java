package controller.xml;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Nicolas Sorin
 */
public class XMLDeserializer {

    // Map constants
    private static final String MAP_NODE_NAME = "reseau";
    private static final String MAP_WAYPOINT_NODE_NAME = "noeud";
    private static final String MAP_SECTION_NODE_NAME = "troncon";

    private static final String ID_NAME = "id";
    private static final String X_NAME = "x";
    private static final String Y_NAME = "y";

    private static final String STREET_NAME = "nomRue";
    private static final String ORIGIN_NAME = "origine";
    private static final String SPEED_NAME = "vitesse";
    private static final String LENGTH_NAME = "longueur";
    private static final String DESTINATION_NAME = "destination";

    private static final String ADDRESS_NAME = "adresse";
    private static final String DURATION_NAME = "duree";
    private static final String START_TIME_NAME = "heureDepart";
    private static final String TIMERANGE_START_NAME = "debutPlage";
    private static final String TIMERANGE_END_NAME = "finPlage";

    // Delivery request constants
    private static final String DELIVERY_NODE_NAME = "demandeDeLivraisons";
    private static final String DELIVERY_WAREHOUSE_NODE_NAME = "entrepot";
    private static final String DELIVERY_LOCATION_NODE_NAME = "livraison";

    /**
     * Load a XML file and create the Map from its content.
     *
     * @param path the path of the desired file
     * @return the new Map
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XMLException
     */
    public static Map loadMap(String path) throws ParserConfigurationException, SAXException, IOException, XMLException {

        Map map = new Map();
        File xml = new File(path);
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = docBuilder.parse(xml);
        Element root = document.getDocumentElement();

        if (root.getNodeName().equals(MAP_NODE_NAME)) {
            buildMapFromDOMXML(root, map);
        } else {
            throw new XMLException("Document non conforme");
        }

        return map;
    }

    /**
     * Load a XML file and create the DeliveryRequest from its content.
     *
     * @param path the path of the desired file
     * @param map the current map
     * @return the new DeliveryRequest
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XMLException
     */
    public static DeliveryRequest loadDeliveryRequest(String path, Map map) throws ParserConfigurationException,
            SAXException, IOException, XMLException, ParseException {

        DeliveryRequest request = new DeliveryRequest();
        File xml = new File(path);
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = docBuilder.parse(xml);
        Element root = document.getDocumentElement();

        if (root.getNodeName().equals(DELIVERY_NODE_NAME)) {
            buildDeliveryRequestFromDOMXML(root, map, request);
        } else {
            throw new XMLException("Document non conforme");
        }

        return request;
    }

    /**
     * Parse the DOM and fill a Map with its content.
     *
     * @param rootNode Root element containing all the information
     * @param map      Map to fill
     * @throws XMLException
     * @throws NumberFormatException
     */
    private static void buildMapFromDOMXML(Element rootNode, Map map) throws XMLException, NumberFormatException {
        NodeList waypointNodes = rootNode.getElementsByTagName(MAP_WAYPOINT_NODE_NAME);
        for (int i = 0; i < waypointNodes.getLength(); i++) {
            Element node = (Element) waypointNodes.item(i);
            Waypoint waypoint = new Waypoint(
                    Integer.parseInt(node.getAttribute(ID_NAME)),
                    Integer.parseInt(node.getAttribute(X_NAME)),
                    Integer.parseInt(node.getAttribute(Y_NAME))
            );
            map.addWaypoint(waypoint);
        }

        NodeList sectionNodes = rootNode.getElementsByTagName(MAP_SECTION_NODE_NAME);
        for (int i = 0; i < sectionNodes.getLength(); i++) {
            Element node = (Element) sectionNodes.item(i);
            Section section = new Section(
                    node.getAttribute(STREET_NAME),
                    Integer.parseInt(node.getAttribute(SPEED_NAME)),
                    Integer.parseInt(node.getAttribute(LENGTH_NAME)), false,
                    map.getWaypoint(Integer.parseInt(node.getAttribute(ORIGIN_NAME))),
                    map.getWaypoint(Integer.parseInt(node.getAttribute(DESTINATION_NAME)))
            );
            map.addSection(section);
        }
    }

    /**
     * Parse the DOM and fill a DeliveryRequest with its content
     *
     * @param rootNode Root element containing all the information
     * @param map      Map containing the Waypoints referenced by the DeliveryRequest
     * @param request  DeliveryRequest to fill
     * @throws XMLException
     * @throws NumberFormatException
     * @throws ParseException
     */
    private static void buildDeliveryRequestFromDOMXML(Element rootNode, Map map, DeliveryRequest request) throws XMLException, NumberFormatException, ParseException {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        // Warehouse
        NodeList warehouseNodes = rootNode.getElementsByTagName(DELIVERY_WAREHOUSE_NODE_NAME);
        Element warehouseElement = (Element) warehouseNodes.item(0);

        String startTime = warehouseElement.getAttribute(START_TIME_NAME);
        Date startDate = startTime != null ? dateFormat.parse(startTime) : null;

        Checkpoint warehouse = new Checkpoint(
                map.getWaypoint(Integer.parseInt(warehouseElement.getAttribute(ADDRESS_NAME))),
                0,
                startDate,
                null
        );
        request.addCheckpoint(warehouse);


        // Deliveries
        NodeList deliveryNodes = rootNode.getElementsByTagName(DELIVERY_LOCATION_NODE_NAME);
        for (int i = 0; i < deliveryNodes.getLength(); i++) {
            Element node = (Element) deliveryNodes.item(i);

            String timeRangeStart = node.getAttribute(TIMERANGE_START_NAME);
            Date timeRangeStartDate = !timeRangeStart.equals("") ? dateFormat.parse(timeRangeStart) : null;
            String timeRangeEnd = node.getAttribute(TIMERANGE_END_NAME);
            Date timeRangeEndDate = !timeRangeEnd.equals("") ? dateFormat.parse(timeRangeEnd) : null;

            Checkpoint delivery = new Checkpoint(
                    map.getWaypoint(Integer.parseInt(node.getAttribute(ADDRESS_NAME))),
                    Integer.parseInt(node.getAttribute(DURATION_NAME)),
                    timeRangeStartDate,
                    timeRangeEndDate
            );
            request.addCheckpoint(delivery);
        }
    }

}
