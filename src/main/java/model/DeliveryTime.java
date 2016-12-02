package model;

import java.util.Date;

public class DeliveryTime {
	
	private Checkpoint checkpoint;
	private Date arrivalTime;
	private Date departureTime;
	
	public DeliveryTime(Checkpoint checkpoint, Date arrivalTime, Date departureTime) {
		super();
		this.checkpoint = checkpoint;
		this.arrivalTime = arrivalTime;
		this.departureTime = departureTime;
	}

	public Date getArrivalTime() {
		return arrivalTime;
	}

	public Date getDepartureTime() {
		return this.departureTime;
	}

	public Checkpoint getCheckpoint() {
		return checkpoint;
	}
	
	
}
