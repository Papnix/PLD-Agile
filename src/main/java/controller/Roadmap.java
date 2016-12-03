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
				writer.println("De l'adresse " + checkpointsList.get(i).getAssociatedWaypoint().getId()
							   + " à l'adresse " + checkpointsList.get(i+1).getAssociatedWaypoint().getId() + " :");
				writer.println(Roadmap.routeToDelivery(round.getPath(checkpointsList.get(i).getAssociatedWaypoint().getId(),
																	 checkpointsList.get(i+1).getAssociatedWaypoint().getId()),
													   map));
			}
			
			// Description du parcours du dernier point de livraison jusqu'à l'entrepôt
			writer.println("De l'adresse " + checkpointsList.get(checkpointsList.size() - 1).getAssociatedWaypoint().getId()
					   	   + " à l'adresse " + checkpointsList.get(0).getAssociatedWaypoint().getId() + " :");
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
    	
		// Pour le premier tronçon, on n'indique pas s'il faut tourner à droite, à gauche...
		Section nextSection = map.getSection(routeWithWaypointsID.get(0), routeWithWaypointsID.get(1));
		text += "Prendre le tronçon " + nextSection.getStreetName() + " entre " + nextSection.getOrigin().getId()
				+ " et " + nextSection.getDestination().getId() + "\r\n";
		
		Section prevSection;
    	for (int i = 1; i < routeWithWaypointsID.size() - 1; i++) {
    		prevSection = nextSection;
    		nextSection = map.getSection(routeWithWaypointsID.get(i), routeWithWaypointsID.get(i+1));
			text += Roadmap.getDescriptionForBend(prevSection, nextSection)
					+ " et prendre le tronçon " + nextSection.getStreetName() + " entre " + nextSection.getOrigin().getId()
					+ " et " + nextSection.getDestination().getId() + "\r\n";
    	}
    	
    	return text;
	}
	
	/**
	 * Describe the bend to get to the next section.
	 * For example : "Turn right"
	 * @param previousSection
	 * 		Section that has been travelled. The driver is currently at the end of this section.
	 * @param nextSection
	 * 		Section that will now be travelled. The driver is currently at the beginning of this section.
	 * @return
	 * 		The bend's description
	 */
	public static String getDescriptionForBend(Section previousSection, Section nextSection) {
		String text;

		// Détermine la direction à prendre en calculant le produit vectoriel des 2 sections
		int xVectorPrevSection = previousSection.getDestination().getXCoord() - previousSection.getOrigin().getXCoord();
		int yVectorPrevSection = previousSection.getDestination().getYCoord() - previousSection.getOrigin().getYCoord();
		int xVectorNextSection = nextSection.getDestination().getXCoord() - nextSection.getOrigin().getXCoord();
		int yVectorNextSection = nextSection.getDestination().getYCoord() - nextSection.getOrigin().getYCoord();
		int crossProduct = xVectorPrevSection * yVectorNextSection - yVectorPrevSection * xVectorNextSection;
		
		if (crossProduct == 0) {
			text = (xVectorPrevSection * xVectorNextSection >= 0 && yVectorPrevSection * yVectorNextSection >= 0) ?
				   "Continuer tout droit" : "Faire demi-tour";
		} else if (crossProduct < 0) { // Attention : axe Y vers le bas
			text = "Tourner à gauche";
		} else {
			text = "Tourner à droite";
		}
		
		return text;
	}
}
