package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import model.Checkpoint;
import model.DeliveryTime;
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
			List<DeliveryTime> deliveriesList = round.getArrivalTimes(); // Liste des livraisons dans l'ordre chronologique
			
			Roadmap.writeRouteToDelivery(deliveriesList.get(0).getCheckpoint(), deliveriesList.get(1).getCheckpoint(),
										 null, round, map, writer);
			for (int i = 1; i < deliveriesList.size() - 1; i++) {
				Roadmap.writeRouteToDelivery(deliveriesList.get(i).getCheckpoint(), deliveriesList.get(i+1).getCheckpoint(),
						deliveriesList.get(i-1).getCheckpoint(), round, map, writer);
			}
			
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
	public static String routeToDelivery(List<Integer> routeWithWaypointsID, boolean beginsFromWarehouse, Map map) {
		String text = "";
		Section nextSection = map.getSection(routeWithWaypointsID.get(0), routeWithWaypointsID.get(1));
    	
		// Pour le premier tronçon, on indique s'il faut tourner à droite, à gauche... sauf si on part de l'entrepôt
		if (beginsFromWarehouse) {
			text += "Prendre le tronçon " + nextSection.getStreetName() + " entre " + nextSection.getOrigin().getId()
					+ " et " + nextSection.getDestination().getId() + "\r\n";
		}
		
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
		try {
		int xVectorPrevSection = previousSection.getDestination().getxCoord() - previousSection.getOrigin().getxCoord();
		int yVectorPrevSection = previousSection.getDestination().getyCoord() - previousSection.getOrigin().getyCoord();
		int xVectorNextSection = nextSection.getDestination().getxCoord() - nextSection.getOrigin().getxCoord();
		int yVectorNextSection = nextSection.getDestination().getyCoord() - nextSection.getOrigin().getyCoord();
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
		} catch(Exception e) {
			return "";
		}
	}
	
	private static void writeRouteToDelivery(Checkpoint origin, Checkpoint destination, Checkpoint previousOrigin,
											 Round round, Map map, PrintWriter writer) {
		// Itinéraire pour aller de la dernière livraison effectuée à la prochaine
		List<Integer> routeWithWaypointsID = new ArrayList<Integer>();
		
		// Itinéraire pour aller à la dernière livraison effectuée (itinéraire qui vient d'être parcouru)
		List<Integer> previousRoute;
		
		writer.println("De l'adresse " + origin.getId()
					   + " à l'adresse " + destination.getId() + " :");
		
		routeWithWaypointsID.clear();
		
		// Si on ne vient pas de sortir de l'entrepôt, on ajoute le noeud avant la dernière livraison. Ceci permet
		// de savoir d'où l'on vient et d'indiquer où tourner pour parcourir le premier tronçon de la prochaine livraison
		boolean beginsFromWarehouse = (origin == round.getRequest().getDeliveryPoint(0));
		if (!beginsFromWarehouse && previousOrigin != null) {
			previousRoute = round.getPath(previousOrigin.getId(), origin.getId());
			routeWithWaypointsID.add(previousRoute.get(previousRoute.size() - 2));
		}
		routeWithWaypointsID.addAll(round.getPath(origin.getId(), destination.getId()));
		
		writer.println(Roadmap.routeToDelivery(routeWithWaypointsID, beginsFromWarehouse, map));
	}
}
