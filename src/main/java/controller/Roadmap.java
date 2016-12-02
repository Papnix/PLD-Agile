package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import model.Checkpoint;
import model.Map;
import model.Round;
import model.Section;

/**
 * Handles the generation of the roadmap and its writing in a text file.
 */
public class Roadmap {
	
	/**
	 * Writes the roadmap of the given round on a text file
	 * @param round
	 * 		Round to describe on the roadmap
	 * @param map
	 * 		City's map
	 */
	public static void writeRoadmap(Round round, Map map) {
		try {
			PrintWriter writer = new PrintWriter("roadmap.txt", "UTF-8");
			List<Checkpoint> checkpointsList = round.getRequest().getDeliveryPointList();
			
			for (int i = 0; i < checkpointsList.size() - 1; i++) {
				writer.println(Roadmap.routeToDelivery(round.getPath(checkpointsList.get(i).getAssociatedWaypoint().getId(),
																	 checkpointsList.get(i+1).getAssociatedWaypoint().getId()),
													   map));
			}
			
			// Description du parcours du dernier point de livraison jusqu'à l'entrepôt
			writer.println(Roadmap.routeToDelivery(round.getPath(checkpointsList.get(checkpointsList.size() - 1).getAssociatedWaypoint().getId(),
																 checkpointsList.get(0).getAssociatedWaypoint().getId()),
												   map));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Writes a string containing the description corresponding to the given route.
	 * @param routeWithWaypointsID
	 * 		Route to follow. This consists in a list of Waypoints' ID
	 * @param map
	 * 		City's map
	 * @return
	 * 		Description of the route
	 */
	public static String routeToDelivery(List<Integer> routeWithWaypointsID, Map map) {
		String text = "";
    	
    	for (int i = 0; i < routeWithWaypointsID.size(); i++) {
    		if (i < routeWithWaypointsID.size() - 1) {
    			Section s = map.getSection(routeWithWaypointsID.get(i), routeWithWaypointsID.get(i+1));
    			
    			text += "Prendre le tronçon : " + s.getStreetName() + " entre " + s.getOrigin().getId()
    					+ " et " + s.getDestination().getId() + "\r\n";
    		}
    	}
    	
    	return text;
	}
}
