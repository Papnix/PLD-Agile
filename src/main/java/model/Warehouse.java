package model;

import java.util.Date;

public class Warehouse{

	private int associatedWaypointId;
	private Date startTime;
	
	public Warehouse(int associatedWaypointId, Date startTime) {
		this.associatedWaypointId = associatedWaypointId;
		this.startTime = startTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public int getAssociatedWaypointId() {
		return associatedWaypointId;
	}
	
}
