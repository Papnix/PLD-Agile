package model;

import java.util.ArrayList;
import java.util.List;

public class DeliveryRequest {

    private ArrayList<Checkpoint> deliveryPointList;

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

    public DeliveryRequest addCheckpoint(Checkpoint delivery) {
        this.deliveryPointList.add(delivery);

        return this;
    }

    public DeliveryRequest addDeliveryPointList(List<Checkpoint> deliveryPointList) {
        this.deliveryPointList.addAll(deliveryPointList);

        return this;
    }

    public DeliveryRequest clear() {
        this.deliveryPointList.clear();

        return this;
    }
}
