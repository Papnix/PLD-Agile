package controller.pathfinder;

import model.Delivery;
import model.DeliveryRequest;
import model.Waypoint;

public class roundCalculator {
	
	private DeliveryRequest delReq;
	private double [][] costTab;
	private Dijkstra dj;
	
	public roundCalculator(DeliveryRequest delReq){
		this.delReq = delReq;
		int size = delReq.getDeliveryPointList().size();
		costTab = new double[size][size];
		dj = new Dijkstra();
	}
	
	public void computeAllPaths(){
		computePathFromWarehouse();
		computePathToWarehouse();
		computePathBetweenDelivery();
	}
	
	public void computePathFromWarehouse(){
		dj.execute(delReq.getWarehouse().getAssociatedWaypoint().getId());
		for(Delivery delivery : delReq.getDeliveryPointList()){
			costTab[delReq.getWarehouse().getAssociatedWaypoint().getId()]
				[delivery.getAssociatedWaypoint().getId()] = 
				dj.getTargetPathCost(delivery.getAssociatedWaypoint().getId());
		}
	}
	
	public void computePathToWarehouse(){
		for(Delivery delivery : delReq.getDeliveryPointList()){
			dj.execute(delivery.getAssociatedWaypoint().getId());
			costTab[delivery.getAssociatedWaypoint().getId()]
				[delReq.getWarehouse().getAssociatedWaypoint().getId()] = 
				dj.getTargetPathCost(delReq.getWarehouse()
						.getAssociatedWaypoint().getId());
		}
	}
	
	public void computePathBetweenDelivery(){
		for(Delivery delivery : delReq.getDeliveryPointList()){
			dj.execute(delivery.getAssociatedWaypoint().getId());
			for(Delivery del:delReq.getDeliveryPointList()){
				costTab[delivery.getAssociatedWaypoint().getId()]
					[del.getAssociatedWaypoint().getId()] = 
					dj.getTargetPathCost(del.getAssociatedWaypoint().getId());
			}
		}
	}
	
}
