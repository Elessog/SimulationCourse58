package AirportSim;

import SimSys.SimEngine;
import SimSys.SimEntity;

/**Taxiways represent a taxiway that planes will use to go to the runway or gates
 * have attribute occupied to indicate if a plane is using it
 * 
 * @author Elouan Autret
 *
 */
public class TaxiWays extends SimEntity {
	
	private boolean occupied;
	
	private Plane plane;
	private ControlTower controlTower;

	public TaxiWays(SimEngine engine, ControlTower controlTower) {
		super(engine);
		this.setControlTower(controlTower);
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

	public ControlTower getControlTower() {
		return controlTower;
	}

	public void setControlTower(ControlTower controlTower) {
		this.controlTower = controlTower;
	}

}
