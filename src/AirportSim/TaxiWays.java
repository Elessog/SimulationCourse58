package AirportSim;

import SimSys.SimEngine;
import SimSys.SimEntity;

public class TaxiWays extends SimEntity {
	
	private boolean occupied;
	
	private Plane plane;

	public TaxiWays(SimEngine engine) {
		super(engine);
		// TODO Auto-generated constructor stub
	}

	public boolean isOccupied() {
		return occupied;
	}

	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}

	public Plane getPlane() {
		return plane;
	}

	public void setPlane(Plane plane) {
		this.plane = plane;
	}

}
