package controller.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import model.Map;
import model.Section;
import model.Waypoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class XMLDeserializer {

    // Map constants
    private static final String MAP_NODE_NAME = "reseau";
    private static final String MAP_WAYPOINT_NODE_NAME = "noeud";
    private static final String MAP_SECTION_NODE_NAME = "troncon";

    /**
     * Ouvre un fichier xml et cree plan a partir du contenu du fichier
     *
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XMLException
     */
    public static void loadMap() throws ParserConfigurationException, SAXException, IOException, XMLException {

        File xml = XMLReader.getInstance().open(true);
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = docBuilder.parse(xml);
        Element root = document.getDocumentElement();

        if (root.getNodeName().equals(MAP_NODE_NAME)) {
            buildMapFromDOMXML(root);
        } else {
            throw new XMLException("Document non conforme");
        }
    }

    private static void buildMapFromDOMXML(Element rootNode) throws XMLException, NumberFormatException {
        NodeList waypointNodes = rootNode.getElementsByTagName(MAP_WAYPOINT_NODE_NAME);
        for (int i = 0; i < waypointNodes.getLength(); i++) {
            Element node = (Element) waypointNodes.item(i);
            Waypoint waypoint = new Waypoint(
                    Integer.parseInt(node.getAttribute("id")),
                    Integer.parseInt(node.getAttribute("x")),
                    Integer.parseInt(node.getAttribute("y"))
            );
            Map.getInstance().addWaypoint(waypoint);
        }

        NodeList sectionNodes = rootNode.getElementsByTagName(MAP_SECTION_NODE_NAME);
        for (int i = 0; i < sectionNodes.getLength(); i++) {
            Element node = (Element) sectionNodes.item(i);
            Section section = new Section(
                    node.getAttribute("nomRue"),
                    Integer.parseInt(node.getAttribute("vitesse")),
                    Integer.parseInt(node.getAttribute("longueur")),
                    Map.getInstance().getWaypoint(Integer.parseInt(node.getAttribute("origine"))),
                    Map.getInstance().getWaypoint(Integer.parseInt(node.getAttribute("destination")))
            );
            Map.getInstance().addSection(section);
        }
    }

}
