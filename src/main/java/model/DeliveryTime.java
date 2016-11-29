package model;

import java.util.Date;

public class DeliveryTime implements Cloneable {
	
	private Checkpoint checkpoint;
	private Date arrivalTime;
	private Date departureTime;
	private long waitingTime;
	
	public DeliveryTime(Checkpoint checkpoint, Date arrivalTime, Date departureTime, long waitingTime) {
		super();
		this.checkpoint = checkpoint;
		this.arrivalTime = arrivalTime;
		this.departureTime = departureTime;
		this.waitingTime = waitingTime;
	}

	public DeliveryTime(DeliveryTime deliveryTime) {
		super();
		this.checkpoint = deliveryTime.checkpoint;
		this.arrivalTime = new Date(deliveryTime.arrivalTime.getTime());
		this.departureTime = new Date(deliveryTime.departureTime.getTime());
		this.waitingTime = deliveryTime.waitingTime;
	}

	public Date getArrivalTime() {
		return this.arrivalTime;
	}

	public Date getDepartureTime() {
		return this.departureTime;
	}
	
	public long getWaitingTime() {
		return this.waitingTime;
	}

	public Checkpoint getCheckpoint() {
		return checkpoint;
	}

	public void setArrivalTime(Date arrivalTime) {
		this.arrivalTime = null;
		this.arrivalTime = arrivalTime;
	}

	public void setDepartureTime(Date departureTime) {
		this.departureTime = null;
		this.departureTime = departureTime;
	}

	public void setWaitingTime(long waitingTime) {
		this.waitingTime = waitingTime;
	}
	
	public DeliveryTime clone() {
		DeliveryTime dt = null;
		try {
			// On r�cup�re l'instance � renvoyer par l'appel de la 
			// m�thode super.clone()
			dt = (DeliveryTime) super.clone();
		} catch(CloneNotSupportedException cnse) {
			// Ne devrait jamais arriver car nous impl�mentons 
			// l'interface Cloneable
			cnse.printStackTrace(System.err);
		}
		dt.checkpoint = (Checkpoint) checkpoint.clone();
		// on renvoie le clone
		return dt;
	}
	
}
