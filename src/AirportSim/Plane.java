package AirportSim;

import enstabretagne.base.time.LogicalDateTime;
import enstabretagne.base.time.LogicalDuration;
import AirportServ.LoggerUtil;
import SimSys.SimEngine;
import SimSys.SimEntity;
import SimSys.SimEvent;

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
    private LogicalDateTime runwayTime;
    private LogicalDateTime takeoffTime;
    private LogicalDateTime endTime;
    
    
	public Plane(SimEngine engine,ControlTower controlTower) {
		super(engine);
		this.creationTime = this.engine.SimulationDate().getCopy();
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
	
	/*@Override
	public boolean equals(Object o){
		
		if ((o instanceof Plane)){
			return ((Plane) o).getIdNumber()==this.idNumber;
		}
		else 
			return false;
	}*/


	public void approach() {
		this.approachingTime = this.engine.SimulationDate().getCopy();
		int minutes = (int) this.engine.getRand().nextUniform(2, 5.999)*this.controlTower.getWeatherIndice();
		this.addEvent(new ApproachingEnd(this.approachingTime.getCopy().add(LogicalDuration.ofMinutes(minutes))));
	}
	
	public void landing(){
		this.landingTime = this.engine.SimulationDate().getCopy();
		LandingEnd landing = new LandingEnd(Plane.this.landingTime.getCopy().add(LogicalDuration.ofMinutes(2)));
		this.addEvent(landing);
	}
	
	public void tw1ing() {
		this.tw1Time = this.engine.SimulationDate().getCopy();
		int minutes = (int) this.engine.getRand().nextUniform(2, 6.999);
		tw1End tw1ing = new tw1End(Plane.this.tw1Time.getCopy().add(LogicalDuration.ofMinutes(minutes)));
		this.addEvent(tw1ing);
	}

	public void takeoff() {
		this.takeoffTime = this.engine.SimulationDate().getCopy();
		TakeOffEnd takeoff = new TakeOffEnd(Plane.this.takeoffTime.getCopy().add(LogicalDuration.ofMinutes(3)));
		this.addEvent(takeoff);
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
			Plane.this.runwayTime = Plane.this.engine.SimulationDate().getCopy();
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
			Plane.this.endExitPrepTime = Plane.this.engine.SimulationDate().getCopy();
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
			Plane.this.leavingTime = Plane.this.engine.SimulationDate().getCopy();
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
			Plane.this.endTime = Plane.this.engine.SimulationDate().getCopy();
			Plane.this.prstate=PlaneResutState.OUTOFREACH;
			Plane.this.controlTower.outOfReach(Plane.this);
			LoggerUtil.Data(this);
		}

		@Override
		public String[] getTitles() {
			String[] a = {"HeureCreation","HeureApproche"
					     ,"HeureAterrissage","HeureTW1"
					     ,"HeureCleaning","HeureFinCleaning"
					     ,"HeureBoarding","HeureDepart"
					     ,"HeureTW2","HeureRunway","HeureTakeoff","HorsLimite"
					     ,"retardAtterissage","retardTw2"
					     ,"retardTakeoff","heure","Day","gate","idPlane"};
			return a;
		}

		@Override
		public String[] getRecords() {
			int landingDelay = Plane.this.approachingTime.soustract(Plane.this.creationTime).getMinutes();
			int delayTw2 = Plane.this.tw2Time.soustract(Plane.this.leavingTime).getMinutes();
			int delayTakeoff = Plane.this.takeoffTime.soustract(Plane.this.runwayTime).getMinutes();
			if (delayTakeoff<0){
				System.out.println("ff");
			}
			int hour = Plane.this.creationTime.soustract(Plane.this.creationTime.truncateToDays()).getMinutes()/60;
			LogicalDuration day = Plane.this.creationTime.truncateToDays().soustract(Plane.this.engine.getStartTime());
			int nbDay = day.getMinutes()/(60*24);
			
			String[] a = {Plane.this.creationTime.toString(),Plane.this.approachingTime.toString()
				     ,Plane.this.landingTime.toString(),Plane.this.tw1Time.toString()
				     ,Plane.this.exitPrepTime.toString(),Plane.this.endExitPrepTime.toString()
				     ,Plane.this.boardingTime.toString(),Plane.this.leavingTime.toString()
				     ,Plane.this.tw2Time.toString(),Plane.this.runwayTime.toString(),Plane.this.takeoffTime.toString()
				     ,Plane.this.endTime.toString()
				     ,String.valueOf(landingDelay),String.valueOf(delayTw2),String.valueOf(delayTakeoff)
				     ,String.valueOf(hour),String.valueOf(nbDay),String.valueOf(Plane.this.gateId)
				     ,String.valueOf(Plane.this.idNumber)};
			return a;
		}

		@Override
		public String getClassement() {
			// TODO Auto-generated method stub
			return "planedetail";
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
