package model;

public class Section
{
	//-------------------------------------------------- Attributes ----------------------------------------------------
	
	private String streetName;
	private int speed;
	private int length;
	
	private boolean active;
	
	private Waypoint origin;
	private Waypoint destination;
	
	//--------------------------------------------------- Methods ------------------------------------------------------
	
			//----------------------------------------- Constructors ---------------------------------------------------
	
	public Section(String streetName, int speed, int length, boolean active, Waypoint origin, Waypoint destination) {
		super();
		this.streetName = streetName;
		this.speed = speed;
		this.length = length;
		this.active = active;
		this.origin = origin;
		this.destination = destination;
	}
	
			//------------------------------------------- Getters ------------------------------------------------------
	
	public String getStreetName() {
		return streetName;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public int getLength() {
		return length;
	}
	
	public boolean isActive()
	{
		return active;
	}
	
	public Waypoint getOrigin() {
		return origin;
	}
	
	public Waypoint getDestination() {
		return destination;
	}
	
	public void setActive(boolean active)
	{
		this.active = active;
	}

	public String toString() {
		return "Section { origin=" + origin.getId() +
				", length=" + length +
				", speed=" + speed +
				", destination=" + destination.getId() +
				", streetName=" + streetName + " }";
	}
}
