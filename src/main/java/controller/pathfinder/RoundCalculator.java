package controller.pathfinder;

import java.util.HashMap;
import java.util.LinkedHashMap;

import model.Delivery;
import model.DeliveryRequest;
import model.Waypoint;
import tsp.TSP1;

public class RoundCalculator {
	
	private DeliveryRequest delReq;
	private int [][] costTab;
	private Dijkstra dj;
	
	private HashMap<Integer, Integer> indexValues;
	
	public RoundCalculator(DeliveryRequest delReq){
		this.delReq = delReq;
		int size = delReq.getDeliveryPointList().size() + 1;
		costTab = new int[size][size];
		dj = new Dijkstra();
		
		indexValues = new LinkedHashMap<Integer, Integer>();
		
		int index = 0;
		
		indexValues.put(delReq.getWarehouse().getAssociatedWaypoint().getId(), index);
		
		for (Delivery d : delReq.getDeliveryPointList()){
			index++;
			
			indexValues.put(d.getAssociatedWaypoint().getId(), index);
		}
	}
	
	public void computeAllPaths(){
		computePathFromWarehouse();
		computePathToWarehouse();
		computePathBetweenDelivery();
	}
	
	public void computePathFromWarehouse(){
		dj.execute(delReq.getWarehouse().getAssociatedWaypoint().getId());
		for(Delivery delivery : delReq.getDeliveryPointList()){
			costTab[indexValues.get(delReq.getWarehouse().getAssociatedWaypoint().getId())]
				[indexValues.get(delivery.getAssociatedWaypoint().getId())] = 
				dj.getTargetPathCost(delivery.getAssociatedWaypoint().getId());
		}
	}
	
	public void computePathToWarehouse(){
		for(Delivery delivery : delReq.getDeliveryPointList()){
			dj.execute(delivery.getAssociatedWaypoint().getId());
			costTab[indexValues.get(delivery.getAssociatedWaypoint().getId())]
				[indexValues.get(delReq.getWarehouse().getAssociatedWaypoint().getId())] = 
				dj.getTargetPathCost(delReq.getWarehouse()
						.getAssociatedWaypoint().getId());
		}
	}
	
	public void computePathBetweenDelivery(){
		for(Delivery delivery : delReq.getDeliveryPointList()){
			dj.execute(delivery.getAssociatedWaypoint().getId());
			for(Delivery del:delReq.getDeliveryPointList()){
				costTab[indexValues.get(delivery.getAssociatedWaypoint().getId())]
					[indexValues.get(del.getAssociatedWaypoint().getId())] = 
					dj.getTargetPathCost(del.getAssociatedWaypoint().getId());
			}
		}
	}
	
	public int[] computeRound(){
		int size = delReq.getDeliveryPointList().size() + 1;
		
		int[] duree = new int[size];
		
		duree[indexValues.get(delReq.getWarehouse().getAssociatedWaypoint().getId())] = 0;
		
		for (Delivery d : delReq.getDeliveryPointList()){
			duree[indexValues.get(d.getAssociatedWaypoint().getId())] = d.getDuration();
		}
		
		TSP1 t = new TSP1();
		
		t.chercheSolution(Integer.MAX_VALUE, size, costTab, duree);
		
		int[] round = new int[size];
		
		for (int i = 0; i < size; i++){
			round[i] = t.getMeilleureSolution(i);
		}
		
		return round;
	}
	
}
