package controller.tsp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import model.Checkpoint;
import model.DeliveryTime;

public abstract class TemplateTSP2 implements TSP2 {

	private DeliveryTime[] meilleureSolution;
	private long coutMeilleureSolution = 0;
	private List<DeliveryTime[]> roundList;
	private Boolean tempsLimiteAtteint;
	private HashMap<Integer, Integer> indexValues;

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

	public Boolean getTempsLimiteAtteint() {
		return tempsLimiteAtteint;
	}

	public int chercheSolution(int tpsLimite, int nbSommets, int[][] cout, int[] duree,
			List<Checkpoint> checkpointList) {
		tempsLimiteAtteint = false;
		coutMeilleureSolution = Long.MAX_VALUE;
		meilleureSolution = new DeliveryTime[nbSommets + 1];
		roundList = new ArrayList<DeliveryTime[]>();
		// buildIndex(checkpointList);
		List<DeliveryTime> nonVus = new ArrayList<DeliveryTime>();
		for (Checkpoint checkpoint : checkpointList) {
			nonVus.add(new DeliveryTime(checkpoint, null, null, 0));
		}
		buildIndex(nonVus);
		nonVus.remove(0);
		ArrayList<DeliveryTime> vus = new ArrayList<DeliveryTime>(checkpointList.size());
		vus.add(new DeliveryTime(checkpointList.get(0), null, checkpointList.get(0).getTimeRangeStart(), 0)); // le
																												// premier
																												// sommet
																												// visite
																												// est
																												// 0
		branchAndBound(vus.get(0), nonVus, vus, 0, cout, duree, checkpointList.get(0).getTimeRangeStart().getTime(),
				tpsLimite);
		completeRound(cout);
		return roundList.size();
	}

	public DeliveryTime getMeilleureSolution(int i, int j) {
		if ((meilleureSolution == null) || (j < 0) || (j >= meilleureSolution.length))
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

	public long getCoutMeilleureSolution() {
		return coutMeilleureSolution;
	}

	/**
	 * Methode devant etre redefinie par les sous-classes de TemplateTSP
	 * 
	 * @param sommetcrt
	 * @param nonVus
	 *            : tableau des sommets restant a visiter
	 * @param cout
	 *            : cout[i][j] = duree pour aller de i a j, avec 0 <= i <
	 *            nbSommets et 0 <= j < nbSommets
	 * @param duree
	 *            : duree[i] = duree pour visiter le sommet i, avec 0 <= i <
	 *            nbSommets
	 * @return une borne inferieure du cout des permutations commencant par
	 *         sommetCourant, contenant chaque sommet de nonVus exactement une
	 *         fois et terminant par le sommet 0
	 */
	protected abstract int bound(Checkpoint sommetcrt, List<DeliveryTime> nonVus, int[][] cout, int[] duree);

	/**
	 * Methode devant etre redefinie par les sous-classes de TemplateTSP
	 * 
	 * @param sommetcrt
	 * @param nonVus
	 *            : tableau des sommets restant a visiter
	 * @param cout
	 *            : cout[i][j] = duree pour aller de i a j, avec 0 <= i <
	 *            nbSommets et 0 <= j < nbSommets
	 * @param duree
	 *            : duree[i] = duree pour visiter le sommet i, avec 0 <= i <
	 *            nbSommets
	 * @return un iterateur permettant d'iterer sur tous les sommets de nonVus
	 */
	protected abstract Iterator<DeliveryTime> iterator(DeliveryTime sommetcrt, List<DeliveryTime> nonVus, int[][] cout,
			int[] duree);

	/**
	 * Methode definissant le patron (template) d'une resolution par separation
	 * et evaluation (branch and bound) du TSP
	 * 
	 * @param sommetCourant
	 *            le dernier sommet visite
	 * @param nonVus
	 *            la liste des sommets qui n'ont pas encore ete visites
	 * @param vus
	 *            la liste des sommets visites (y compris sommetCrt)
	 * @param coutVus
	 *            la somme des couts des arcs du chemin passant par tous les
	 *            sommets de vus + la somme des duree des sommets de vus
	 * @param cout
	 *            : cout[i][j] = duree pour aller de i a j, avec 0 <= i <
	 *            nbSommets et 0 <= j < nbSommets
	 * @param duree
	 *            : duree[i] = duree pour visiter le sommet i, avec 0 <= i <
	 *            nbSommets
	 * @param tpsDebut
	 *            : moment ou la resolution a commence
	 * @param tpsLimite
	 *            : limite de temps pour la resolution
	 */
	void branchAndBound(DeliveryTime sommetCourant, List<DeliveryTime> nonVus, ArrayList<DeliveryTime> vus, long coutVus,
			int[][] cout, int[] duree, long tpsDebut, int tpsLimite) {

		Date arrivalDate = new Date(coutVus + tpsDebut);
		long waitingTime = validTimeRange(sommetCourant.getCheckpoint(), arrivalDate);
		if (waitingTime != -1) {
			Date departureTime = new Date(
					arrivalDate.getTime() + waitingTime + duree[indexValues.get(sommetCourant.getCheckpoint().getId())]);
			sommetCourant.setArrivalTime(arrivalDate);
			sommetCourant.setDepartureTime(departureTime);
			sommetCourant.setWaitingTime(waitingTime);
			if (nonVus.size() == 0) { // tous les sommets ont ete visites
				coutVus += cout[indexValues.get(sommetCourant.getCheckpoint().getId())][0];
				if (coutVus < coutMeilleureSolution) { // on a trouve une
														// solution meilleure
														// que meilleureSolution
					roundList.clear();
					vus.toArray(meilleureSolution);
					roundList.add(copyOf(meilleureSolution));
					coutMeilleureSolution = coutVus;
				} else if (coutVus == coutMeilleureSolution) {
					vus.toArray(meilleureSolution);
					if (!contains(meilleureSolution)) {
						roundList.add(copyOf(meilleureSolution));
					}
				}
			} else if (coutVus + waitingTime
					+ bound(sommetCourant.getCheckpoint(), nonVus, cout, duree) < coutMeilleureSolution) {
				Iterator<DeliveryTime> it = iterator(sommetCourant, nonVus, cout, duree);
				while (it.hasNext()) {
					DeliveryTime prochainSommet = it.next();
					vus.add(prochainSommet);
					nonVus.remove(prochainSommet);
					branchAndBound(prochainSommet, nonVus, vus,
							departureTime.getTime()
									+ cout[indexValues.get(sommetCourant.getCheckpoint().getId())][indexValues
											.get(prochainSommet.getCheckpoint().getId())]
									- tpsDebut,
							cout, duree, tpsDebut, tpsLimite);
					// coutVus + waitingTime +
					// cout[indexValues.get(sommetCrt.getCheckpoint().getId())][indexValues.get(prochainSommet.getCheckpoint().getId())]
					// +
					// duree[indexValues.get(prochainSommet.getCheckpoint().getId())]
					vus.remove(prochainSommet);
					nonVus.add(prochainSommet);
				}
			}
		}
	}

	public List<DeliveryTime[]> getRoundList() {
		return roundList;
	}

}
