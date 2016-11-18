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
	public int getxCoord() {
		return xCoord;
	}
	public int getyCoord() {
		return yCoord;
	}

	public String toString() {
		return "Waypoint { id=" + id + ", x=" + xCoord + ", y=" + yCoord + " }";
	}
	
}
