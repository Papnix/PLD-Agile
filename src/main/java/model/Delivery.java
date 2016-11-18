package model;

import java.util.Date;

public class Delivery
{
	
	private Waypoint associatedWaypoint;
	private int duration;
	private Date timeRangeStart;
	private Date timeRangeEnd;
	
	public Delivery(Waypoint associatedWaypoint, int duration, Date timeRangeStart, Date timeRangeEnd) {
		super();
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

	
	
	
}
