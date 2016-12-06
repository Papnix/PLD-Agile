package controller.tsp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import model.Checkpoint;
import model.DeliveryTime;

public abstract class TemplateTSP implements TSP {

	private DeliveryTime[] bestSolution;
	private long BestSolutionCost = 0;
	private List<DeliveryTime[]> roundList;
	private Boolean limiteTimeReached;
	public static HashMap<Integer, Integer> indexValues;

	private DeliveryTime[] copyOf(DeliveryTime[] dt) {
		DeliveryTime[] newDt = new DeliveryTime[dt.length];
		for (int i = 0; i < dt.length - 1; i++) {
			newDt[i] = dt[i].clone();
		}

		return newDt;
	}

	private long validTimeRange(Checkpoint sommetcrt, Date arrivalDate) {
		/*
		 * check if there is a time range to arrive at the checkpoint if yes,
		 * check that the arrival Date is in the range else return true
		 */
		if (sommetcrt.getTimeRangeStart() != null && sommetcrt.getTimeRangeEnd() != null) {
			if (arrivalDate.getTime() > sommetcrt.getTimeRangeEnd().getTime()) {
				return -1;
			} else if (arrivalDate.getTime() < sommetcrt.getTimeRangeStart().getTime()) {
				return (sommetcrt.getTimeRangeStart().getTime() - arrivalDate.getTime());
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	private boolean contains(DeliveryTime[] dt) {
		for (DeliveryTime[] deliveryTime : roundList) {
			if (deliveryTime.equals(dt)) {
				return true;
			}
		}
		return false;
	}

	private void completeRound(int[][] cout) {

		for (DeliveryTime[] roundTab : roundList) {
			Date arrivalDate = new Date(roundTab[roundTab.length - 2].getDepartureTime().getTime()
					+ cout[indexValues.get(roundTab[roundTab.length - 2].getCheckpoint().getId())][indexValues
							.get(roundTab[0].getCheckpoint().getId())]);
			roundTab[roundTab.length - 1] = new DeliveryTime(roundTab[0].getCheckpoint(), arrivalDate, null, 0);
		}
	}

	public Boolean getLimitTimeReached() {
		return limiteTimeReached;
	}

	public int findSolution(int tpsLimite, int nbSommets, int[][] cout, int[] duree, List<Checkpoint> checkpointList) {
		limiteTimeReached = false;
		BestSolutionCost = Long.MAX_VALUE;
		bestSolution = new DeliveryTime[nbSommets + 1];
		roundList = new ArrayList<DeliveryTime[]>();
		// buildIndex(checkpointList);
		List<DeliveryTime> unseen = new ArrayList<DeliveryTime>();
		for (Checkpoint checkpoint : checkpointList) {
			unseen.add(new DeliveryTime(checkpoint, null, null, 0));
		}
		buildIndex(unseen);
		unseen.remove(0);
		ArrayList<DeliveryTime> seen = new ArrayList<DeliveryTime>(checkpointList.size());
		seen.add(new DeliveryTime(checkpointList.get(0), null, checkpointList.get(0).getTimeRangeStart(), 0)); 
		branchAndBound(seen.get(0), unseen, seen, 0, cout, duree, checkpointList.get(0).getTimeRangeStart().getTime(),
				tpsLimite);
		completeRound(cout);
		return roundList.size();
	}

	public DeliveryTime getBestSolution(int i, int j) {
		if ((bestSolution == null) || (j < 0) || (j >= bestSolution.length))
			return null;
		return getBestRound(i)[j];
	}

	public DeliveryTime[] getBestRound(int i) {
		if ((roundList == null) || (i < 0) || (i >= roundList.size()))
			return null;
		return roundList.get(i);
	}

	public void buildIndex(List<DeliveryTime> checkpointList) {
		// assigning indices to every waypoint
		indexValues = new LinkedHashMap<Integer, Integer>();

		for (int index = 0; index < checkpointList.size(); ++index) {
			indexValues.put(checkpointList.get(index).getCheckpoint().getId(), index);
		}
	}

	public long getBestSolutionCost() {
		return BestSolutionCost;
	}

	/**
	 * Methode devant etre redefinie par les sous-classes de TemplateTSP
	 * 
	 * @param currentVertice
	 * @param unseen
	 *            : tableau des sommets restant a visiter
	 * @param cost
	 *            : cout[i][j] = duree pour aller de i a j, avec 0 <= i <
	 *            nbSommets et 0 <= j < nbSommets
	 * @param duration
	 *            : duree[i] = duree pour visiter le sommet i, avec 0 <= i <
	 *            nbSommets
	 * @return une borne inferieure du cout des permutations commencant par
	 *         sommetCourant, contenant chaque sommet de nonVus exactement une
	 *         fois et terminant par le sommet 0
	 */
	protected abstract int bound(Checkpoint currentVertice, List<DeliveryTime> unseen, int[][] cost, int[] duration);

	/**
	 * Methode devant etre redefinie par les sous-classes de TemplateTSP
	 * 
	 * @param currentVertice
	 * @param unseen
	 *            : tableau des sommets restant a visiter
	 * @param cost
	 *            : cout[i][j] = duree pour aller de i a j, avec 0 <= i <
	 *            nbSommets et 0 <= j < nbSommets
	 * @param duration
	 *            : duree[i] = duree pour visiter le sommet i, avec 0 <= i <
	 *            nbSommets
	 * @return un iterateur permettant d'iterer sur tous les sommets de nonVus
	 */
	protected abstract Iterator<DeliveryTime> iterator(DeliveryTime currentVertice, List<DeliveryTime> unseen,
			int[][] cost, int[] duration);

	/**
	 * Methode definissant le patron (template) d'une resolution par separation
	 * et evaluation (branch and bound) du TSP
	 * 
	 * @param currentVertice
	 *            le dernier sommet visite
	 * @param unseen
	 *            la liste des sommets qui n'ont pas encore ete visites
	 * @param seen
	 *            la liste des sommets visites (y compris sommetCrt)
	 * @param seenCost
	 *            la somme des couts des arcs du chemin passant par tous les
	 *            sommets de vus + la somme des duree des sommets de vus
	 * @param cost
	 *            : cout[i][j] = duree pour aller de i a j, avec 0 <= i <
	 *            nbSommets et 0 <= j < nbSommets
	 * @param duration
	 *            : duree[i] = duree pour visiter le sommet i, avec 0 <= i <
	 *            nbSommets
	 * @param startTime
	 *            : moment ou la resolution a commence
	 * @param timeLimit
	 *            : limite de temps pour la resolution
	 */
	void branchAndBound(DeliveryTime currentVertice, List<DeliveryTime> unseen, ArrayList<DeliveryTime> seen,
			long seenCost, int[][] cost, int[] duration, long startTime, int timeLimit) {

		Date arrivalDate = new Date(seenCost + startTime);
		long waitingTime = validTimeRange(currentVertice.getCheckpoint(), arrivalDate);
		if (waitingTime != -1) {
			Date departureTime = new Date(arrivalDate.getTime() + waitingTime
					+ duration[indexValues.get(currentVertice.getCheckpoint().getId())]);
			currentVertice.setArrivalTime(arrivalDate);
			currentVertice.setDepartureTime(departureTime);
			currentVertice.setWaitingTime(waitingTime);
			if (unseen.size() == 0) { // tous les sommets ont ete visites
				seenCost += cost[indexValues.get(currentVertice.getCheckpoint().getId())][0];
				if (seenCost < BestSolutionCost) { // on a trouve une
													// solution meilleure
													// que meilleureSolution
					roundList.clear();
					seen.toArray(bestSolution);
					roundList.add(copyOf(bestSolution));
					BestSolutionCost = seenCost;
				} else if (seenCost == BestSolutionCost) {
					seen.toArray(bestSolution);
					if (!contains(bestSolution)) {
						roundList.add(copyOf(bestSolution));
					}
				}
			} else if (seenCost + waitingTime
					+ bound(currentVertice.getCheckpoint(), unseen, cost, duration) < BestSolutionCost) {
				Iterator<DeliveryTime> it = iterator(currentVertice, unseen, cost, duration);
				while (it.hasNext()) {
					DeliveryTime nextVertice = it.next();
					seen.add(nextVertice);
					unseen.remove(nextVertice);
					branchAndBound(nextVertice, unseen, seen,
							departureTime.getTime()
									+ cost[indexValues.get(currentVertice.getCheckpoint().getId())][indexValues
											.get(nextVertice.getCheckpoint().getId())]
									- startTime,
							cost, duration, startTime, timeLimit);
					// coutVus + waitingTime +
					// cout[indexValues.get(sommetCrt.getCheckpoint().getId())][indexValues.get(prochainSommet.getCheckpoint().getId())]
					// +
					// duree[indexValues.get(prochainSommet.getCheckpoint().getId())]
					seen.remove(nextVertice);
					unseen.add(nextVertice);
				}
			}
		}
	}

	public List<DeliveryTime[]> getRoundList() {
		return roundList;
	}

}
