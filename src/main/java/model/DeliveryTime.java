package model;

import java.util.Date;

public class DeliveryTime {
	
	private Checkpoint checkpoint;
	private Date deliveryTime;
	
	public DeliveryTime(Checkpoint checkpoint, Date deliveryTime) {
		super();
		this.checkpoint = checkpoint;
		this.deliveryTime = deliveryTime;
	}

	public Date getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(Date deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public Checkpoint getCheckpoint() {
		return checkpoint;
	}
	
	
}
