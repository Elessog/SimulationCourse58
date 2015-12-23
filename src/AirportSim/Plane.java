package AirportSim;

import SimSys.SimEngine;
import SimSys.SimEntity;

public class Plane extends SimEntity {
	
	private int idNumber;

	public Plane(SimEngine engine,ControlTower controlTower) {
		super(engine);
		controlTower.annoucing(this);
	}

	public int getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(int idNumber) {
		this.idNumber = idNumber;
	}

	
	
}
