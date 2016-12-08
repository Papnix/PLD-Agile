package model;

import java.util.Date;

/**
 * Corresponds to a delivery that is planned
 */
public class DeliveryTime implements Cloneable {

	private Checkpoint checkpoint;
	private Date arrivalTime;
	private Date departureTime;
	private long waitingTime;

	/**
	 * Buids a DeliveryTime, given all the parameters it needs
	 * @param checkpoint
	 * 		Location to deliver
	 * @param arrivalTime
	 * 		Time of arrival of the deliverer
	 * @param departureTime
	 * 		Time of departure of the deliverer
	 * @param waitingTime
	 * 		Waiting time of the deliver before delivering when he has arrived to the location
	 */
	public DeliveryTime(Checkpoint checkpoint, Date arrivalTime, Date departureTime, long waitingTime) {
		super();
		this.checkpoint = checkpoint;
		this.arrivalTime = arrivalTime;
		this.departureTime = departureTime;
		this.waitingTime = waitingTime;
	}

	/**
	 * Copy constructor
	 * @param deliveryTime
	 * 		DeliveryTime to copy
	 */
	public DeliveryTime(DeliveryTime deliveryTime) {
		super();
		this.checkpoint = new Checkpoint(deliveryTime.checkpoint);
		this.arrivalTime = deliveryTime.arrivalTime == null ? null : new Date(deliveryTime.arrivalTime.getTime());
		this.departureTime = deliveryTime.departureTime == null ? null : new Date(deliveryTime.departureTime.getTime());
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
		this.arrivalTime = arrivalTime;
	}

	public void setDepartureTime(Date departureTime) {
		this.departureTime = departureTime;
	}

	public void setWaitingTime(long waitingTime) {
		this.waitingTime = waitingTime;
	}

	public DeliveryTime clone() {
		DeliveryTime dt = null;
		try {
			// On récupère l'instance à renvoyer par l'appel de la méthode super.clone()
			dt = (DeliveryTime) super.clone();
		} catch(CloneNotSupportedException cnse) {
			// Ne devrait jamais arriver car nous implémentons l'interface Cloneable
			cnse.printStackTrace(System.err);
		}
		dt.checkpoint = (Checkpoint) checkpoint.clone();
		// on renvoie le clone
		return dt;
	}

}
