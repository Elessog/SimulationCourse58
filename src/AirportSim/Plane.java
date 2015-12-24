package AirportSim;

import SimSys.SimEngine;
import SimSys.SimEntity;

public class Plane extends SimEntity {
	
	private int idNumber = 9999;
	private PlaneState pstate =PlaneState.FLYING;
	private PlaneResutState prstate =PlaneResutState.FLYING;
	private ControlTower controlTower;

	

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

	public PlaneState getPlaneState() {
		return pstate;
	}

	public void setPlaneState(PlaneState state) {
		this.pstate = state;
	}
	
	public enum PlaneState {
		FLYING,
		APPROACHING,
		LANDING,
		GOINGGATE,
		GATE,
		BOARDING,
		WAITING,
		GOINGTAKEOFF,
		TAKEOFF,
		LEAVING;		
	}
	
	public PlaneResutState getPrstate() {
		return prstate;
	}

	public void setPrstate(PlaneResutState prstate) {
		this.prstate = prstate;
	}
	
	public enum PlaneResutState{
		FLYING,
		APPROACHED,
		LANDED,
		GATED,
		BOARDED,
		ARRIVEDRUNWAY,
		OUTOFREACH;
	}
	

	public void setControlTower(ControlTower controlTower) {
		this.controlTower = controlTower;	
	}
	
	@Override
	public boolean equals(Object o){
		
		if (!(o instanceof Plane)){
			return ((Plane) o).getIdNumber()==this.idNumber;
		}
		else 
			return false;
	}

	public void takeoff() {
		// TODO Auto-generated method stub
		
	}

	public void approach() {
		// TODO Auto-generated method stub
		
	}

	

}
