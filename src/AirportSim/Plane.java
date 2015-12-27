package AirportSim;

import enstabretagne.base.time.LogicalDateTime;
import enstabretagne.base.time.LogicalDuration;
import SimSys.SimEngine;
import SimSys.SimEntity;
import SimuCoiffeur.SimEvent;

public class Plane extends SimEntity {
	
	private int idNumber = 9999;
	private PlaneState pstate =PlaneState.FLYING;
	private PlaneResutState prstate =PlaneResutState.FLYING;
	private int gateId = 9999;
	private ControlTower controlTower;
	private LogicalDateTime creationTime;
    private LogicalDateTime approachingTime;
    private LogicalDateTime landingTime;
    private LogicalDateTime tw1Time;
    private LogicalDateTime exitPrepTime;
    private LogicalDateTime endExitPrepTime;
    private LogicalDateTime boardingTime;
    private LogicalDateTime leavingTime;
    private LogicalDateTime tw2Time;
    private LogicalDateTime takeoffTime;
    private LogicalDateTime endTime;
	public LogicalDateTime endBoardingTime;
	

	public Plane(SimEngine engine,ControlTower controlTower) {
		super(engine);
		this.creationTime = this.engine.SimulationDate();
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
		ROULINGENDED,
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
		
		if ((o instanceof Plane)){
			return ((Plane) o).getIdNumber()==this.idNumber;
		}
		else 
			return false;
	}


	public void approach() {
		this.approachingTime = this.engine.SimulationDate();
		int minutes = (int) this.engine.getRand().nextUniform(2, 5.999)*this.controlTower.getWeatherIndice();
		this.addEvent(new ApproachingEnd(this.approachingTime.add(LogicalDuration.ofMinutes(minutes))));
	}
	
	public void landing(){
		this.landingTime = this.engine.SimulationDate();
		LandingEnd landing = new LandingEnd(Plane.this.landingTime.add(LogicalDuration.ofMinutes(2)));
		this.addEvent(landing);
	}
	
	public void tw1ing() {
		this.tw1Time = this.engine.SimulationDate();
		int minutes = (int) this.engine.getRand().nextUniform(2, 6.999);
		tw1End tw1ing = new tw1End(Plane.this.tw1Time.add(LogicalDuration.ofMinutes(minutes)));
		this.addEvent(tw1ing);
	}

	public void takeoff() {
		this.takeoffTime = this.engine.SimulationDate();
		TakeOffEnd landing = new TakeOffEnd(Plane.this.takeoffTime.add(LogicalDuration.ofMinutes(3)));
		this.addEvent(landing);
	}
	
	private class ApproachingEnd extends SimEvent{

		public ApproachingEnd(LogicalDateTime scheduledDate) {
			super(scheduledDate);
		}

		@Override
		public void process() {
			Plane.this.prstate=PlaneResutState.APPROACHED;
			Plane.this.controlTower.approached();
		}

		@Override
		public String[] getTitles() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String[] getRecords() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getClassement() {
			// TODO Auto-generated method stub
			return null;
		}
		   
	   }
	
	private class LandingEnd extends SimEvent{

		public LandingEnd(LogicalDateTime scheduledDate) {
			super(scheduledDate);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void process() {
			Plane.this.prstate=PlaneResutState.LANDED;
			Plane.this.controlTower.landed();
		}

		@Override
		public String[] getTitles() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String[] getRecords() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getClassement() {
			// TODO Auto-generated method stub
			return null;
		}
		   
	}

	private class tw1End extends SimEvent{

		public tw1End(LogicalDateTime scheduledDate) {
			super(scheduledDate);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void process() {
			Plane.this.prstate=PlaneResutState.ROULINGENDED;
			Plane.this.controlTower.endfly();
		}

		@Override
		public String[] getTitles() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String[] getRecords() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getClassement() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private class tw2End extends SimEvent{

		public tw2End(LogicalDateTime scheduledDate) {
			super(scheduledDate);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void process() {
			Plane.this.prstate=PlaneResutState.ARRIVEDRUNWAY;
			Plane.this.controlTower.readyfly();
		}

		@Override
		public String[] getTitles() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String[] getRecords() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getClassement() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	private class deboardingEnd extends SimEvent{

		public deboardingEnd(LogicalDateTime scheduledDate) {
			super(scheduledDate);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void process() {
			Plane.this.endExitPrepTime = Plane.this.engine.SimulationDate();
			Plane.this.controlTower.deboardEnded(Plane.this);
		}

		@Override
		public String[] getTitles() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String[] getRecords() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getClassement() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	private class boardingEnd extends SimEvent{

		public boardingEnd(LogicalDateTime scheduledDate) {
			super(scheduledDate);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void process() {
			Plane.this.setPrstate(PlaneResutState.BOARDED);
			Plane.this.endBoardingTime = Plane.this.engine.SimulationDate();
			Plane.this.controlTower.leaving(Plane.this);
		}

		@Override
		public String[] getTitles() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String[] getRecords() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getClassement() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	private class TakeOffEnd extends SimEvent{

		public TakeOffEnd(LogicalDateTime scheduledDate) {
			super(scheduledDate);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void process() {
			Plane.this.endTime = Plane.this.engine.SimulationDate();
			Plane.this.prstate=PlaneResutState.OUTOFREACH;
			Plane.this.controlTower.outOfReach(Plane.this);
		}

		@Override
		public String[] getTitles() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String[] getRecords() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getClassement() {
			// TODO Auto-generated method stub
			return null;
		}
		   
	}
	
	public void gating(int i) {
		this.setGateId(i);
		this.exitPrepTime = this.engine.SimulationDate();
		this.prstate = PlaneResutState.GATED;
		this.pstate = PlaneState.GATE;
		this.controlTower.gated(this);
	}
	
	public void deboard() {
		deboardingEnd event  = new deboardingEnd(Plane.this.exitPrepTime.add(LogicalDuration.ofMinutes(40)));
		this.addEvent(event);
	}

	public void tw2ing() {
		this.tw2Time = this.engine.SimulationDate();
		//TODO change this.prstate = PlaneResutState.BOARDED;
		int minutes = (int) this.engine.getRand().nextUniform(2, 6.999);
		tw2End tw2ing = new tw2End(Plane.this.tw2Time.add(LogicalDuration.ofMinutes(minutes)));
		this.addEvent(tw2ing);
	}

	public int getGateId() {
		return gateId;
	}

	public void setGateId(int gateId) {
		this.gateId = gateId;
	}

	public void board() {
		this.boardingTime = this.engine.SimulationDate();
		boardingEnd boarding = new boardingEnd(Plane.this.boardingTime.add(LogicalDuration.ofMinutes(20)));
		this.addEvent(boarding);
	}


}
