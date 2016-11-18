package controller;

import controller.xml.XMLDeserializer;
import model.Map;
import model.Section;
import model.Waypoint;

import java.util.TreeMap;

public class Controller {

    public static void main(String args[]) {
        try {
            XMLDeserializer.loadMap("/home/nicolas/plan10x10.xml");
        } catch(Exception e) {
            e.printStackTrace();
        }
        for(java.util.Map.Entry<Integer, Waypoint> entry : Map.getInstance().getWaypoints().entrySet()) {
            System.out.println(entry.getValue());
        }

        for(java.util.Map.Entry<Integer, TreeMap<Integer, Section>> map : Map.getInstance().getSections().entrySet()) {
            for(java.util.Map.Entry<Integer, Section> entry : map.getValue().entrySet()) {
                System.out.println(entry.getValue());
            }
        }
    }

}
