package model;

import java.util.Date;

public class Delivery
{
	
	private int associatedWaypointId;
	private int duration;
	private Date timeRangeStart;
	private Date timeRangeEnd;
	
	public Delivery(int associatedWaypointId, int duration, Date timeRangeStart, Date timeRangeEnd) {
		super();
		this.associatedWaypointId = associatedWaypointId;
		this.duration = duration;
		this.timeRangeStart = timeRangeStart;
		this.timeRangeEnd = timeRangeEnd;
	}
	
	public int getAssociatedWaypointId() {
		return associatedWaypointId;
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
