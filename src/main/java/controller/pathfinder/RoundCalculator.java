package controller.pathfinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.collections4.MapUtils;

import model.Delivery;
import model.DeliveryRequest;
import model.Waypoint;
import model.Map;
import model.Section;
import tsp.TSP1;

public class RoundCalculator {

	private DeliveryRequest delReq;
	private int[][] costTab;
	private Dijkstra dj;

	private HashMap<Integer, HashMap<Integer, List<Integer>>> paths;

	private List<Waypoint> waypoints;

	private HashMap<Integer, Integer> indexValues;

	public RoundCalculator(DeliveryRequest delReq) {
		this.delReq = delReq;

		waypoints = new ArrayList<Waypoint>();

		waypoints.add(delReq.getWarehouse().getAssociatedWaypoint());

		for (Delivery d : delReq.getDeliveryPointList()) {
			waypoints.add(d.getAssociatedWaypoint());
		}

		int size = waypoints.size();
		costTab = new int[size][size];
		dj = new Dijkstra();

		indexValues = new LinkedHashMap<Integer, Integer>();

		int index = 0;

		for (Waypoint w : waypoints) {
			indexValues.put(w.getId(), index);
			index++;
		}

		paths = new LinkedHashMap();
	}

	public void computePaths() {
		paths.clear();
		for (Waypoint w1 : waypoints) {
			dj.execute(w1.getId());

			for (Waypoint w2 : waypoints) {
				if (w1.getId() == w2.getId()) {
					costTab[indexValues.get(w1.getId())][indexValues.get(w2.getId())] = 0;
				} else {
					costTab[indexValues.get(w1.getId())][indexValues.get(w2.getId())] = dj
							.getTargetPathCost(w2.getId());
				}

				HashMap<Integer, List<Integer>> path = paths.get(w1.getId());

				if (path == null) {
					path = new LinkedHashMap<Integer, List<Integer>>();
					paths.put(w1.getId(), path);

				}

				path.put(w2.getId(), dj.getPath(w2.getId()));
			}
		}
	}

	public int[] computeRound() {
		int size = delReq.getDeliveryPointList().size() + 1;

		int[] duree = new int[size];

		duree[indexValues.get(delReq.getWarehouse().getAssociatedWaypoint().getId())] = 0;

		for (Delivery d : delReq.getDeliveryPointList()) {
			duree[indexValues.get(d.getAssociatedWaypoint().getId())] = d.getDuration();
		}

		TSP1 t = new TSP1();

		t.chercheSolution(Integer.MAX_VALUE, size, costTab, duree);

		Map map = Map.getInstance();

		int[] round = new int[size + 1];
		
		map.resetPath();
		HashMap<Integer, Integer> mapInversed = (HashMap<Integer, Integer>)MapUtils.invertMap(indexValues);
		for (int i = 0; i < size; i++){
			round[i] = mapInversed.get(t.getMeilleureSolution(i));
			List<Integer> path;

			if (i < size - 1) {
				path = paths.get(round[i]).get(mapInversed.get(t.getMeilleureSolution(i + 1)));
			} else {
				path = paths.get(round[i]).get(round[0]);
			}

			for (int j = 0; j < path.size(); j++) {
				Section s;

				if (j < path.size() - 1) {
					s = map.getSection(path.get(j), path.get(j + 1));

					s.setActive(true);
				}
			}
		}
		round[size] = round[0];
		return round;
	}

	public int getCost(int idOrigin, int idDestination) {
		return costTab[indexValues.get(idOrigin)][indexValues.get(idDestination)];
	}

	public int[][] getCost() {
		return costTab;
	}
}
