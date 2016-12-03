package model;

public class Waypoint implements Cloneable
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
	
	public Waypoint clone() {
		Waypoint wp = null;
		try {
			// On récupère l'instance à renvoyer par l'appel de la 
			// méthode super.clone()
			wp = (Waypoint) super.clone();
		} catch(CloneNotSupportedException cnse) {
			// Ne devrait jamais arriver car nous implémentons 
			// l'interface Cloneable
			cnse.printStackTrace(System.err);
		}
		// on renvoie le clone
		return wp;
	}
	
}
