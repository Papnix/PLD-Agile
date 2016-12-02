package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

//import org.apache.commons.collections4.MapUtils;
import controller.pathfinder.Dijkstra;
import controller.tsp.TSP1;

public class Round {
    // -------------------------------------------------- Attributes
    // ----------------------------------------------------

    private final int MILLIS_TO_SEC = 1000;

    private int duration;

    private List<List<Section>> route;
    private DeliveryRequest request;
    private List<List<DeliveryTime>> roundTimeOrder;

    /**
     * This array contains the cost of the shortest path from cp1 to cp2
     * (costTab[cp1][cp2]).
     */
    private int[][] costTab;
    /**
     * The Dijkstra instance that will compute the paths.
     */
    private Dijkstra dj;
    /**
     * This hashmap contains all the computed paths (represented by lists of
     * waypoint ids). To get the path from cp1 to cp2, use
     * paths.get(cp1).get(cp2) (cp1 and cp2 have to be waypoint ids).
     */
    private HashMap<Integer, HashMap<Integer, List<Integer>>> paths;

    /**
     * This hashmap is used to bind a waypoint id to a unique array index.
     */
    private HashMap<Integer, Integer> indexValues;

    // --------------------------------------------------- Methods
    // ------------------------------------------------------

    // ----------------------------------------- Constructors
    // ---------------------------------------------------

    public Round(DeliveryRequest request) {

        this.request = request;
        roundTimeOrder = new ArrayList<List<DeliveryTime>>();
        route = new ArrayList<List<Section>>();
        buildIndex();
    }

    public Round(Round round) {
        this.duration = round.duration;
        // Copy arrival times : new deliveryTimes must be created
        this.roundTimeOrder = new ArrayList<>();
        this.roundTimeOrder.add(0, new ArrayList<>());
        for (int k = 0; k < round.roundTimeOrder.size(); k++) {
            this.roundTimeOrder.add(k, new ArrayList<>());
            for (int i = 0; i < round.roundTimeOrder.get(k).size(); i++) {
                DeliveryTime deliveryTime = round.roundTimeOrder.get(k).get(i);
                this.roundTimeOrder.get(k).add(i, new DeliveryTime(deliveryTime));
            }
        }

        // Copy route : no need to copy Sections, since they will not change
        this.route = new ArrayList<>();
        for (int j = 0; j < round.route.size(); j++) {
            this.route.add(j, round.route.get(j));
        }

        // Copy deliveryRequest : no need to copy checkpoints
        this.request = new DeliveryRequest();
        List<Checkpoint> oldCheckpointList = round.getRequest().getDeliveryPointList();
        for (Checkpoint checkpoint : oldCheckpointList) {
            this.request.addCheckpoint(checkpoint);
        }

        buildIndex();
        for (int k = 0; k < this.costTab.length; k++) {
            for (int l = 0; l < this.costTab.length; l++) {
                this.costTab[k][l] = round.costTab[k][l];
            }
        }

    }

    // ------------------------------------------- Getters
    // ------------------------------------------------------

    public int getDuration() {
        return duration;
    }

    public List<Section> getRoute(int i) {
        return route.get(i);
    }

    public DeliveryRequest getRequest() {
        return request;
    }

    public List<DeliveryTime> getRoundTimeOrder(int i) {
        return roundTimeOrder.get(i);
    }

    // ------------------------------------------- Setters
    // ------------------------------------------------------

    public void setDuration(int duration) {
        this.duration = duration;
    }

	/*
     * public void setRoute(List<Section> route) { this.route = route; }
	 */

    // ---------------------------------------- Other methods
    // ---------------------------------------------------

    public void buildIndex() {

        // initializing the cost array
        int size = request.getDeliveryPointList().size();
        costTab = new int[size][size];

        // assigning indices to every waypoint
        indexValues = new LinkedHashMap<Integer, Integer>();

        for (int index = 0; index < size; ++index) {
            indexValues.put(request.getDeliveryPoint(index).getId(), index);
        }

        paths = new LinkedHashMap<Integer, HashMap<Integer, List<Integer>>>();
    }

    /**
     * This method compute the paths from any waypoint (in the delivery request)
     * to any other.
     */
    public void computePaths(Map map) {
        dj = new Dijkstra(map);
        paths.clear();

        // selecting an origin waypoint
        for (Checkpoint checkpoint1 : request.getDeliveryPointList()) {

            // compute all paths from the origin
            dj.execute(checkpoint1.getId());

            // selecting a destination
            for (Checkpoint checkpoint2 : request.getDeliveryPointList()) {

                // if the origin and the destination are equal, the cost of the
                // path is 0.
                // else, the path is replaced with the result from Dijkstra.
                if (checkpoint1.getId() == checkpoint2.getId()) {
                    costTab[indexValues.get(checkpoint1.getId())][indexValues.get(checkpoint2.getId())] = 0;
                } else {
                    costTab[indexValues.get(checkpoint1.getId())][indexValues.get(checkpoint2.getId())] = dj
                            .getTargetPathCost(checkpoint2.getId());
                }

                HashMap<Integer, List<Integer>> path = paths.get(checkpoint1.getId());

                // if no path has been determined for cp1, the structure
                // containing all its paths is created.
                if (path == null) {
                    path = new LinkedHashMap<Integer, List<Integer>>();
                    paths.put(checkpoint1.getId(), path);

                }

                // the path is inserted in the structure
                path.put(checkpoint2.getId(), dj.getPath(checkpoint2.getId()));
            }
        }
    }

    /**
     * This method computes the best possible round. Method computePaths must be
     * called before.
     *
     * @return The ids of the waypoints in the best round, in the right order
     * (the warehouse is both at the beginning and the end of the round.
     */
    public void computeRound(Map map) {

        computePaths(map);

        int[] durations = initializeWaypointTime();

        int numberOfDelivery = request.getDeliveryPointList().size();

        TSP1 tspAlgorithm = new TSP1();

        // The TSP algorithm is used to compute the best round
        int numSolution = tspAlgorithm.findSolution(Integer.MAX_VALUE, numberOfDelivery, costTab, durations,
                request.getDeliveryPointList());

        for (int i = 0; i < numSolution && tspAlgorithm.getBestRound(i) != null; i++) {
            roundTimeOrder.add(Arrays.asList(tspAlgorithm.getBestRound(i)));
            route.add(buildRoute(map, Arrays.asList(tspAlgorithm.getBestRound(i))));
        }
    }

    /**
     * @param idOrigin      the id of the origin
     * @param idDestination the id of the destination
     * @return The cost of the shortests path from the origin to the
     * destination.
     */
    public int getCost(int idOrigin, int idDestination) {
        return costTab[indexValues.get(idOrigin)][indexValues.get(idDestination)];
    }

    public void addStep(Checkpoint step) {

    }

    // ---- Private methods
    // -----------------------------------------------------------------------------------

    private void saveSection(Map map, List<Integer> path, List<Section> ListOfSection) {
        for (int j = 0; j < path.size() - 1; j++) {
            Section section;
            section = map.getSection(path.get(j), path.get((j + 1)));
            ListOfSection.add(section);
        }
    }

    private List<Section> buildRoute(Map map, List<DeliveryTime> dtList) {
        List<Section> sectionList = new ArrayList<Section>();
        for (int i = 0; i < dtList.size() - 1; i++) {
            saveSection(map,
                    paths.get(dtList.get(i).getCheckpoint().getId()).get(dtList.get(i + 1).getCheckpoint().getId()),
                    sectionList);
        }
        return sectionList;
    }

    private int[] initializeWaypointTime() {
        int size = request.getDeliveryPointList().size();

        // the visiting time of every waypoint is initialized
        int[] durations = new int[size];

        for (Checkpoint d : request.getDeliveryPointList()) {
            durations[indexValues.get(d.getAssociatedWaypoint().getId())] = d.getDuration() * MILLIS_TO_SEC;
        }

        return durations;
    }

    public List<Integer> getPath(Integer idOrigin, Integer idDestination) {
        return paths.get(idOrigin).get(idDestination);
    }
}
