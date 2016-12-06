package model;

import java.util.Date;

public class Checkpoint implements Cloneable{
	
	private Waypoint associatedWaypoint;
	private int duration;
	private Date timeRangeStart;
	private Date timeRangeEnd;
	
	public Checkpoint(Waypoint associatedWaypoint, int duration, Date timeRangeStart, Date timeRangeEnd) {
		this.associatedWaypoint = associatedWaypoint;
		this.duration = duration;
		this.timeRangeStart = timeRangeStart;
		this.timeRangeEnd = timeRangeEnd;
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
			// On récupère l'instance à renvoyer par l'appel de la 
			// méthode super.clone()
			ck = (Checkpoint) super.clone();
		} catch(CloneNotSupportedException cnse) {
			// Ne devrait jamais arriver car nous implémentons 
			// l'interface Cloneable
			cnse.printStackTrace(System.err);
		}
		
		ck.associatedWaypoint = (Waypoint) associatedWaypoint.clone();
		// on renvoie le clone
		return ck;
	}
	
}
