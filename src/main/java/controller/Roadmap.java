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
	 * 		Round to be described on the roadmap
	 * @param map
	 * 		City's map
	 */
	public static void writeRoadmap(Round round, Map map) {
		try {
			PrintWriter writer = new PrintWriter("roadmap.txt", "UTF-8");
			List<DeliveryTime> deliveriesList = round.getRoundTimeOrder(0); // Liste des livraisons dans l'ordre chronologique
			
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
	 * The route goes from one delivery point to another.
	 * @param routeWithWaypointsID
	 * 		Route to follow. This consists in a list of Waypoints' ID
	 * @param startsFromWarehouse
	 * 		Indicates whether the route starts from the warehouse or not
	 * @param map
	 * 		City's map
	 * @return
	 * 		Description of the route
	 */
	public static String routeToDelivery(List<Integer> routeWithWaypointsID, boolean startsFromWarehouse, Map map) {
		String text = "";
		Section nextSection = map.getSection(routeWithWaypointsID.get(0), routeWithWaypointsID.get(1));
    	
		// Pour le premier tronçon, on indique s'il faut tourner à droite, à gauche... sauf si on part de l'entrepôt
		if (startsFromWarehouse) {
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
	
	/**
	 * Writes the full route from a delivery point to another on a text file
	 * @param origin
	 * 		Start of the route, last delivery point reached
	 * @param destination
	 * 		End of the route, next delivery point to reach
	 * @param previousOrigin
	 * 		Previous delivery point reached (before the current one)
	 * @param round
	 * 		Round to be described on the roadmap
	 * @param map
	 * 		City's map
	 * @param writer
	 * 		File writer
	 */
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
		boolean startsFromWarehouse = (origin == round.getRequest().getDeliveryPoint(0));
		if (!startsFromWarehouse && previousOrigin != null) {
			previousRoute = round.getPath(previousOrigin.getId(), origin.getId());
			routeWithWaypointsID.add(previousRoute.get(previousRoute.size() - 2));
		}
		routeWithWaypointsID.addAll(round.getPath(origin.getId(), destination.getId()));
		
		writer.println(Roadmap.routeToDelivery(routeWithWaypointsID, startsFromWarehouse, map));
	}
}
