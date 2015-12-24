package AirportSim;

import SimSys.SimEngine;
import SimSys.SimEntity;

public class Runway extends SimEntity {
	
	private boolean occupied;
	private Plane plane;
	private ControlTower controlTower;

	public Runway(SimEngine engine, ControlTower controlTower) {
		super(engine);
		this.controlTower = controlTower;
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
