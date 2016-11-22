package model;

import java.util.ArrayList;
import java.util.List;

public class DeliveryRequest {

    private Warehouse warehouse;
    private ArrayList<Delivery> deliveryPointList;

    private static DeliveryRequest instance = null;

    public static DeliveryRequest getInstance() {
        if (instance == null) instance = new DeliveryRequest();
        return instance;
    }

    public DeliveryRequest() {
        this.deliveryPointList = new ArrayList<Delivery>();
    }

    public DeliveryRequest(Warehouse warehouse, ArrayList<Delivery> deliveryPointList) {
        this.warehouse = warehouse;
        this.deliveryPointList = deliveryPointList;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public List<Delivery> getDeliveryPointList() {
        return deliveryPointList;
    }

    public Delivery getDeliveryPoint(int index) {
        return deliveryPointList.get(index);
    }

    public DeliveryRequest addDeliveryPoint(Delivery delivery) {
        this.deliveryPointList.add(delivery);

        return this;
    }

    public DeliveryRequest addDeliveryPointList(List<Delivery> deliveryPointList) {
        this.deliveryPointList.addAll(deliveryPointList);

        return this;
    }

    public DeliveryRequest clear() {
        this.deliveryPointList.clear();

        return this;
    }

}
