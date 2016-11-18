package model;

import java.util.List;

public class DeliveryRequest {
	
	private Warehouse warehouse;
	private List<Delivery> deliveryPointList;
	
	public DeliveryRequest(Warehouse warehouse, List<Delivery> deliveryPointList) {
		super();
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

	public void addDeliveryPointList(List<Delivery> deliveryPointList) {
		this.deliveryPointList.addAll(deliveryPointList);
	}
	
}
