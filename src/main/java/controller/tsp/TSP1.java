package controller.tsp;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import model.Checkpoint;

public class TSP1 extends TemplateTSP{
	
	//private HashMap<Integer, Integer> indexValues;

	protected int bound(Checkpoint sommetCourant, List<Checkpoint> nonVus, int[][] cout, int[] duree) {
		return 0;
	}

	protected Iterator<Checkpoint> iterator(Checkpoint sommetCourant, List<Checkpoint> nonVus, int[][] cout, int[] duree) {
		return new IteratorSeq(nonVus, sommetCourant);
	}

}
