package controller.tsp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import model.Checkpoint;

public abstract class TemplateTSP implements TSP {

	private Checkpoint[] meilleureSolution;
	private long coutMeilleureSolution = 0;
	private List<Checkpoint[]> roundList;
	private Boolean tempsLimiteAtteint;
	private HashMap<Integer, Integer> indexValues;
	
	public Boolean getTempsLimiteAtteint(){
		return tempsLimiteAtteint;
	}
	
	public void chercheSolution(int tpsLimite, int nbSommets, int[][] cout, int[] duree, List<Checkpoint> checkpointList){
		tempsLimiteAtteint = false;
		coutMeilleureSolution = Long.MAX_VALUE;
		meilleureSolution = new Checkpoint[nbSommets];
		roundList = new ArrayList<Checkpoint[]>();
		buildIndex(checkpointList);
		List<Checkpoint> nonVus = new ArrayList<Checkpoint>();
		for(Checkpoint checkpoint:checkpointList){
			nonVus.add(checkpoint);
		}
		nonVus.remove(0);
		ArrayList<Checkpoint> vus = new ArrayList<Checkpoint>(checkpointList.size());
		vus.add(checkpointList.get(0)); // le premier sommet visite est 0
		branchAndBound(checkpointList.get(0), nonVus, vus, 0, cout, duree, checkpointList.get(0).getTimeRangeStart().getTime(), tpsLimite);
	}
	
	public Checkpoint getMeilleureSolution(int i, int j){
		if ((meilleureSolution == null) || (j<0) || (j>=meilleureSolution.length))
			return null;
		return getBestRound(i)[j];
	}
	
	public Checkpoint[] getBestRound(int i){
		if ((roundList == null) || (i<0) || (i>=roundList.size()))
			return null;
		return roundList.get(i);
	}
	
	public void buildIndex(List<Checkpoint> checkpointList) {
		// assigning indices to every waypoint
		indexValues = new LinkedHashMap<Integer, Integer>();

		for (int index = 0; index < checkpointList.size(); ++index) {
			indexValues.put(checkpointList.get(index).getId(), index);
		}
	}
	
	public long getCoutMeilleureSolution(){
		return coutMeilleureSolution;
	}

	/**
	 * Methode devant etre redefinie par les sous-classes de TemplateTSP
	 * @param sommetcrt
	 * @param nonVus : tableau des sommets restant a visiter
	 * @param cout : cout[i][j] = duree pour aller de i a j, avec 0 <= i < nbSommets et 0 <= j < nbSommets
	 * @param duree : duree[i] = duree pour visiter le sommet i, avec 0 <= i < nbSommets
	 * @return une borne inferieure du cout des permutations commencant par sommetCourant, 
	 * contenant chaque sommet de nonVus exactement une fois et terminant par le sommet 0
	 */
	protected abstract int bound(Checkpoint sommetcrt, List<Checkpoint> nonVus, int[][] cout, int[] duree);
	
	/**
	 * Methode devant etre redefinie par les sous-classes de TemplateTSP
	 * @param sommetcrt
	 * @param nonVus : tableau des sommets restant a visiter
	 * @param cout : cout[i][j] = duree pour aller de i a j, avec 0 <= i < nbSommets et 0 <= j < nbSommets
	 * @param duree : duree[i] = duree pour visiter le sommet i, avec 0 <= i < nbSommets
	 * @return un iterateur permettant d'iterer sur tous les sommets de nonVus
	 */
	protected abstract Iterator<Checkpoint> iterator(Checkpoint sommetcrt, List<Checkpoint> nonVus, int[][] cout, int[] duree);
	
	/**
	 * Methode definissant le patron (template) d'une resolution par separation et evaluation (branch and bound) du TSP
	 * @param sommetCrt le dernier sommet visite
	 * @param nonVus la liste des sommets qui n'ont pas encore ete visites
	 * @param vus la liste des sommets visites (y compris sommetCrt)
	 * @param coutVus la somme des couts des arcs du chemin passant par tous les sommets de vus + la somme des duree des sommets de vus
	 * @param cout : cout[i][j] = duree pour aller de i a j, avec 0 <= i < nbSommets et 0 <= j < nbSommets
	 * @param duree : duree[i] = duree pour visiter le sommet i, avec 0 <= i < nbSommets
	 * @param tpsDebut : moment ou la resolution a commence
	 * @param tpsLimite : limite de temps pour la resolution
	 */	
	 void branchAndBound(Checkpoint sommetCrt, List<Checkpoint> nonVus, ArrayList<Checkpoint> vus, long coutVus, int[][] cout, int[] duree, long tpsDebut, int tpsLimite){
		 /*if (System.currentTimeMillis() - tpsDebut > tpsLimite){
			 tempsLimiteAtteint = true;
			 return;
		 }*/
		 
		Date arrivalDate;
		arrivalDate = new Date(coutVus + tpsDebut);
		long waitingTime = validTimeRange(sommetCrt, arrivalDate);
	    if (nonVus.size() == 0){ // tous les sommets ont ete visites
	    	coutVus += cout[indexValues.get(sommetCrt.getId())][0];
	    	if (coutVus < coutMeilleureSolution){ // on a trouve une solution meilleure que meilleureSolution
	    		roundList.clear();
	    		vus.toArray(meilleureSolution);
	    		roundList.add(meilleureSolution);
	    		coutMeilleureSolution = coutVus;
	    	}else if (coutVus == coutMeilleureSolution){
	    		vus.toArray(meilleureSolution);
	    		roundList.add(meilleureSolution);
	    	}
	    } else if (waitingTime !=-1 && (coutVus + bound(sommetCrt, nonVus, cout, duree) < coutMeilleureSolution)){
	    	Iterator<Checkpoint> it = iterator(sommetCrt, nonVus, cout, duree);
	        while (it.hasNext()){
	        	Checkpoint prochainSommet = it.next();
	        	vus.add(prochainSommet);
	        	nonVus.remove(prochainSommet);
	        	branchAndBound(prochainSommet, nonVus, vus, coutVus + waitingTime + cout[indexValues.get(sommetCrt.getId())][indexValues.get(prochainSommet.getId())] + duree[indexValues.get(prochainSommet.getId())], cout, duree, tpsDebut, tpsLimite);
	        	vus.remove(prochainSommet);
	        	nonVus.add(prochainSommet);
	        }	    
	    }
	}
	 
	private long validTimeRange(Checkpoint sommetcrt, Date arrivalDate){
		/* check if there is a time range to arrive at the checkpoint
		 * 	if yes, check the arrival Date is in the range 
		 * 	else return true
		 */
		if(sommetcrt.getTimeRangeStart() != null && sommetcrt.getTimeRangeEnd() != null){
			if(arrivalDate.getTime() >= sommetcrt.getTimeRangeEnd().getTime()){
				return -1;
			}else if (arrivalDate.getTime() >= sommetcrt.getTimeRangeStart().getTime()){
				return 0;
			}else{
				return (sommetcrt.getTimeRangeStart().getTime() - arrivalDate.getTime());
			}
		}else{
			return 0;
		}
	}

}
