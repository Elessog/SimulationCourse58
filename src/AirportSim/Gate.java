package AirportSim;

import SimSys.SimEngine;
import SimSys.SimEntity;

public class Gate extends SimEntity {
	
	private boolean occupied;
	private Plane plane;
	private int gateId;

	public Gate(SimEngine engine,int gateid) {
		super(engine);
		setGateId(gateid);
		setOccupied(false);
		setPlane(null);
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

	public int getGateId() {
		return gateId;
	}

	public void setGateId(int gateId) {
		this.gateId = gateId;
	}

}
