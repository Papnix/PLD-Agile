package controller.tsp;

import java.util.ArrayList;
import java.util.Iterator;

import tsp.IteratorSeq;
import tsp.TemplateTSP;

public class TSP2 extends TemplateTSP{

	@Override
	protected int bound(Integer sommetCourant, ArrayList<Integer> nonVus, int[][] cout, int[] duree) {
		int minimalBound = 0;
		int cost = 0;
		if(nonVus.size() == 0){
			return 0;
		}else{
			for(Integer i: nonVus){
				cost += duree[i] + cout[sommetCourant][i];
				minimalBound = (cost < minimalBound) ? cost : minimalBound; 
			}
		}
		return minimalBound;
	}

	@Override
	protected Iterator<Integer> iterator(Integer sommetCrt, ArrayList<Integer> nonVus, int[][] cout, int[] duree) {
		return new IteratorSeq(nonVus, sommetCrt);
	}

}
