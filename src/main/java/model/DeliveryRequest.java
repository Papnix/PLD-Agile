package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Corresponds to a delivery request loaded from a file
 */
public class DeliveryRequest {

    private ArrayList<Checkpoint> deliveryPointList; // Liste des points à livrer

    public DeliveryRequest() {
        this.deliveryPointList = new ArrayList<Checkpoint>();
    }

    public DeliveryRequest(ArrayList<Checkpoint> deliveryPointList) {
        this.deliveryPointList = deliveryPointList;
    }

    public List<Checkpoint> getDeliveryPointList() {
        return deliveryPointList;
    }

    public Checkpoint getDeliveryPoint(int index) {
        return deliveryPointList.get(index);
    }
    
    public Checkpoint getDeliveryPointId(int checkpointId) {
    	int i = 0;
    	Checkpoint checkpoint = deliveryPointList.get(i);
    	while(checkpoint.getId()!=checkpointId && i<deliveryPointList.size()){
    		checkpoint=deliveryPointList.get(i);
    		i++;
    	}
    	return checkpoint;
    }

    /**
     * Adds a Checkpoint to the delivery request
     * @param delivery
     * 		Checkpoint to add
     * @return
     * 		The DeliveryRequest itself
     */
    public DeliveryRequest addCheckpoint(Checkpoint delivery) {
        this.deliveryPointList.add(delivery);

        return this;
    }

    /**
     * Adds many Checkpoints to the delivery request
     * @param deliveryPointList
     * 		Checkpoints to add
     * @return
     * 		The DeliveryRequest itself
     */
    public DeliveryRequest addDeliveryPointList(List<Checkpoint> deliveryPointList) {
        this.deliveryPointList.addAll(deliveryPointList);

        return this;
    }

    public DeliveryRequest clear() {
        this.deliveryPointList.clear();

        return this;
    }
}
