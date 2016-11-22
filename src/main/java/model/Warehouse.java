package model;

import java.util.Date;

public class Warehouse{

	private Waypoint associatedWaypoint;
	private Date startTime;
	
	public Warehouse(Waypoint associatedWaypoint, Date startTime) {
		this.associatedWaypoint = associatedWaypoint;
		this.startTime = startTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Waypoint getAssociatedWaypoint() {
		return associatedWaypoint;
	}
	
}
