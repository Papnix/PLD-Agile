package model;

import java.util.Date;

/**
 * Represents a place where there is a delivery to be done
 */
public class Checkpoint implements Cloneable{

	private Waypoint associatedWaypoint;
	private int duration;
	private Date timeRangeStart;
	private Date timeRangeEnd;

	/**
	 * Builds a Checkpoint, given all the parameters it needs
	 * @param associatedWaypoint
	 * 		Waypoint that is associated to this Checkpoint
	 * @param duration
	 * 		Duration of the delivery
	 * @param timeRangeStart
	 * 		Start of the time range when the delivery can be done
	 * @param timeRangeEnd
	 * 		End of the time range when the delivery can be done
	 */
	public Checkpoint(Waypoint associatedWaypoint, int duration, Date timeRangeStart, Date timeRangeEnd) {
		this.associatedWaypoint = associatedWaypoint;
		this.duration = duration;
		this.timeRangeStart = timeRangeStart;
		this.timeRangeEnd = timeRangeEnd;
	}

	/**
	 * Copy constructor
	 * @param checkpoint
	 * 		Checkpoint to copy
	 */
	public Checkpoint(Checkpoint checkpoint) {
		this.associatedWaypoint = checkpoint.associatedWaypoint;
		this.duration = checkpoint.duration;
		this.timeRangeStart = checkpoint.timeRangeStart == null ? null : new Date(checkpoint.timeRangeStart.getTime());
		this.timeRangeEnd = checkpoint.timeRangeEnd == null ? null : new Date(checkpoint.timeRangeEnd.getTime());
	}

	public Waypoint getAssociatedWaypoint() {
		return associatedWaypoint;
	}

	public int getDuration() {
		return duration;
	}

	public int getId(){
		return associatedWaypoint.getId();
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public Date getTimeRangeStart() {
		return timeRangeStart;
	}

	public void setTimeRangeStart(Date timeRangeStart) {
		this.timeRangeStart = timeRangeStart;
	}

	public Date getTimeRangeEnd() {
		return timeRangeEnd;
	}

	public void setTimeRangeEnd(Date timeRangeEnd) {
		this.timeRangeEnd = timeRangeEnd;
	}

	public String toString() {
		return "Delivery : { waypoint:" + this.associatedWaypoint.getId() + ", duree:" + this.duration + " }";
	}

	public Checkpoint clone() {
		Checkpoint ck = null;
		try {
			// On récupère l'instance à renvoyer par l'appel de la méthode super.clone()
			ck = (Checkpoint) super.clone();
		} catch(CloneNotSupportedException cnse) {
			// Ne devrait jamais arriver car nous implémentons l'interface Cloneable
			cnse.printStackTrace(System.err);
		}

		ck.associatedWaypoint = (Waypoint) associatedWaypoint.clone();
		// on renvoie le clone
		return ck;
	}

}
