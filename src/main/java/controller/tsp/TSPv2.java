package controller.tsp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import model.Checkpoint;
import tsp.TSP;

public abstract class TSPv2 implements TSP {

	private Integer[] meilleureSolution;
	private int coutMeilleureSolution = 0;
	private Boolean tempsLimiteAtteint;
	private List<Checkpoint> checkpointList;
	private HashMap<Integer, Integer> indexValues;
	
	public Boolean getTempsLimiteAtteint(){
		return tempsLimiteAtteint;
	}
	
	public void chercheSolution(int tpsLimite, int nbSommets, int[][] cout, int[] duree, List<Checkpoint> checkpointList){
		tempsLimiteAtteint = false;
		coutMeilleureSolution = Integer.MAX_VALUE;
		meilleureSolution = new Integer[nbSommets];
		this.checkpointList = checkpointList;
		buildIndex(checkpointList);
		List<Checkpoint> nonVus = checkpointList;
		ArrayList<Checkpoint> vus = new ArrayList<Checkpoint>(checkpointList.size());
		vus.add(checkpointList.get(0)); // le premier sommet visite est 0
		branchAndBound(checkpointList.get(0), nonVus, vus, 0, cout, duree, System.currentTimeMillis(), tpsLimite);
	}
	
	public Integer getMeilleureSolution(int i){
		if ((meilleureSolution == null) || (i<0) || (i>=meilleureSolution.length))
			return null;
		return meilleureSolution[i];
	}
	
	public void buildIndex(List<Checkpoint> checkpointList) {
		// assigning indices to every waypoint
		indexValues = new LinkedHashMap<Integer, Integer>();

		for (int index = 0; index < checkpointList.size(); ++index) {
			indexValues.put(checkpointList.get(index).getId(), index);
		}
	}
	
	public int getCoutMeilleureSolution(){
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
	 * @param sommetcrt le dernier sommet visite
	 * @param nonVus la liste des sommets qui n'ont pas encore ete visites
	 * @param vus la liste des sommets visites (y compris sommetCrt)
	 * @param coutVus la somme des couts des arcs du chemin passant par tous les sommets de vus + la somme des duree des sommets de vus
	 * @param cout : cout[i][j] = duree pour aller de i a j, avec 0 <= i < nbSommets et 0 <= j < nbSommets
	 * @param duree : duree[i] = duree pour visiter le sommet i, avec 0 <= i < nbSommets
	 * @param tpsDebut : moment ou la resolution a commence
	 * @param tpsLimite : limite de temps pour la resolution
	 */	
	 void branchAndBound(Checkpoint sommetcrt, List<Checkpoint> nonVus, ArrayList<Checkpoint> vus, int coutVus, int[][] cout, int[] duree, long tpsDebut, int tpsLimite){
		 if (System.currentTimeMillis() - tpsDebut > tpsLimite){
			 tempsLimiteAtteint = true;
			 return;
		 }
		 
		Date arrivalDate = new Date(coutVus + tpsDebut);
	    if (nonVus.size() == 0){ // tous les sommets ont ete visites
	    	coutVus += cout[indexValues.get(sommetcrt.getId())][0];
	    	if (coutVus < coutMeilleureSolution){ // on a trouve une solution meilleure que meilleureSolution
	    		vus.toArray(meilleureSolution);
	    		coutMeilleureSolution = coutVus;
	    	}
	    } else if (arrivalDate.getTime() > sommetcrt.getTimeRangeStart().getTime() && arrivalDate.getTime() < sommetcrt.getTimeRangeEnd().getTime() && coutVus + bound(sommetcrt, nonVus, cout, duree) < coutMeilleureSolution){
	        Iterator<Checkpoint> it = iterator(sommetcrt, nonVus, cout, duree);
	        while (it.hasNext()){
	        	Checkpoint prochainSommet = it.next();
	        	vus.add(prochainSommet);
	        	nonVus.remove(prochainSommet);
	        	branchAndBound(prochainSommet, nonVus, vus, coutVus + cout[indexValues.get(sommetcrt.getId())][indexValues.get(prochainSommet.getId())] + duree[indexValues.get(prochainSommet.getId())], cout, duree, tpsDebut, tpsLimite);
	        	vus.remove(prochainSommet);
	        	nonVus.add(prochainSommet);
	        }	    
	    }
	}

}
