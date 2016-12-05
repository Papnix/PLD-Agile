package controller.tsp;

import java.util.List;

import model.Checkpoint;
import model.DeliveryTime;

public interface TSP {
		
	/**
	 * @return true si chercheSolution() s'est terminee parce que la limite de temps avait ete atteinte, avant d'avoir pu explorer tout l'espace de recherche,
	 */
	public Boolean getTempsLimiteAtteint();
	
	/**
	 * Cherche un circuit de duree minimale passant par chaque sommet (compris entre 0 et nbSommets-1)
	 * @param tpsLimite : limite (en millisecondes) sur le temps d'execution de chercheSolution
	 * @param nbSommets : nombre de sommets du graphe
	 * @param cout : cout[i][j] = duree pour aller de i a j, avec 0 <= i < nbSommets et 0 <= j < nbSommets
	 * @param duree : duree[i] = duree pour visiter le sommet i, avec 0 <= i < nbSommets
	 */
	public int chercheSolution(int tpsLimite, int nbSommets, int[][] cout, int[] duree, List<Checkpoint> checkpointList);
	
	/**
	 * @param i
	 * @return le sommet visite en i-eme position dans la solution calculee par chercheSolution
	 */
	public DeliveryTime getMeilleureSolution(int i, int j);
	
	/** 
	 * @return la duree de la solution calculee par chercheSolution
	 */
	public long getCoutMeilleureSolution();
}
