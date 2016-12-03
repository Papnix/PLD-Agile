package model;

import java.util.Date;

public class DeliveryTime {
	
	private Checkpoint checkpoint;
	private Date arrivalTime;
	private Date departureTime;
	private Date waitingTime;
	
	public DeliveryTime(Checkpoint checkpoint, Date arrivalTime, Date departureTime, long waitingTime) {
		super();
		this.checkpoint = checkpoint;
		this.arrivalTime = arrivalTime;
		this.departureTime = departureTime;
		this.waitingTime = new Date(waitingTime);
	}

	public Date getArrivalTime() {
		return this.arrivalTime;
	}

	public Date getDepartureTime() {
		return this.departureTime;
	}
	
	public Date getWaitingTime() {
		return this.waitingTime;
	}

	public Checkpoint getCheckpoint() {
		return checkpoint;
	}

	public void setArrivalTime(Date arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public void setDepartureTime(Date departureTime) {
		this.departureTime = departureTime;
	}

	public void setWaitingTime(long waitingTime) {
		this.waitingTime = new Date(waitingTime);
	}
	
	
}
