package model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Round
{
	//-------------------------------------------------- Attributes ----------------------------------------------------
	
	private int duration;
	
	private List<Section> route;
	private DeliveryRequest request;
	private HashMap<Delivery, Date> visitTime;
		
	//--------------------------------------------------- Methods ------------------------------------------------------
	
			//----------------------------------------- Constructors ---------------------------------------------------

	public Round(DeliveryRequest request) {
		super();
		this.request = request;
	}

			//------------------------------------------- Getters ------------------------------------------------------
	
	public int getDuration() {
		return duration;
	}
	
	public List<Section> getRoute() {
		return route;
	}
	
	public DeliveryRequest getRequest() {
		return request;
	}

	public HashMap<Delivery, Date> getVisitTime() {
		return visitTime;
	}
	
			//------------------------------------------- Setters ------------------------------------------------------

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public void setRoute(List<Section> route) {
		this.route = route;
	}
	
			//---------------------------------------- Other methods ---------------------------------------------------
	
	public void addStep(Delivery step)
	{
		
	}
	
	public void computeRoute()
	{
		
	}
}
