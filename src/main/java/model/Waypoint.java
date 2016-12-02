package model;

public class Waypoint 
{

	private int id;
	private int xCoord;
	private int yCoord;
	
	public Waypoint(int pId, int pX, int pY)
	{
		id = pId;
		xCoord = pX;
		yCoord = pY;
	}
	
	public int getId() {
		return id;
	}
	public int getXCoord() {
		return xCoord;
	}
	public int getYCoord() {
		return yCoord;
	}

	public String toString() {
		return "Waypoint { id=" + id + ", x=" + xCoord + ", y=" + yCoord + " }";
	}
	
}
