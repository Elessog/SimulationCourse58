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
		setControlTower(controlTower);
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
	
	/**State of the plane is in during transition
	 * 
	 * @author Elouan Autret
	 *
	 */
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
	
	/**Result state indicate if the plane has finish a task or not but still 
	 * waiting to enter another task
	 * 
	 * @author Elouan Autret
	 *
	 */
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
	
	/** The plane is ordered by the {@link ControlTower} to make his approach.
	 * the time needed to do it is calculed here via an uniform law (between 2 and 6 minutes)
	 * and depend of the wheather {@link ControlTower#getWeatherIndice()} 
	 * then add the event {@link ApproachingEnd} to the engine event queue.
	 */
	public void approach() {
		this.approachingTime = this.engine.SimulationDate().getCopy();
		int minutes = (int) this.engine.getRand().nextUniform(2, 5.999)*this.controlTower.getWeatherIndice();
		this.addEvent(new ApproachingEnd(this.approachingTime.getCopy().add(LogicalDuration.ofMinutes(minutes))));
	}
	
	/**The plane is authorized to land by the {@link ControlTower}
	 * {@link LandingEnd} is added to the event queue after 2 minutes
	 */
	public void landing(){
		this.landingTime = this.engine.SimulationDate().getCopy();
		LandingEnd landing = new LandingEnd(Plane.this.landingTime.getCopy().add(LogicalDuration.ofMinutes(2)));
		this.addEvent(landing);
	}
	
	/**The plane is authorized to go to taxiway 1 by the {@link ControlTower}
	 * {@link Tw1End} is added to the event queue after (2-6 minutes,uniform law)
	 * 
	 */
	public void tw1ing() {
		this.tw1Time = this.engine.SimulationDate().getCopy();
		int minutes = (int) this.engine.getRand().nextUniform(2, 6.999);
		Tw1End tw1ing = new Tw1End(Plane.this.tw1Time.getCopy().add(LogicalDuration.ofMinutes(minutes)));
		this.addEvent(tw1ing);
	}
	
	/**The plane is told to go to gate i by the {@link ControlTower}
	 * then reply {@link ControlTower#gated(Plane)} when gated
	 * 
	 */
	public void gating(int i) {
		this.setGateId(i);
		this.exitPrepTime = this.engine.SimulationDate();
		this.prstate = PlaneResutState.GATED;
		this.pstate = PlaneState.GATE;
		this.controlTower.gated(this);
	}
	
	public int getGateId() {
		return gateId;
	}

	public void setGateId(int gateId) {
		this.gateId = gateId;
	}

	/**The plane is told to deboard  by the {@link ControlTower}
	 * {@link DeboardingEnd} is added to the event queue after 40 minutes
	 * 
	 */
	public void deboard() {
		DeboardingEnd event  = new DeboardingEnd(Plane.this.exitPrepTime.add(LogicalDuration.ofMinutes(40)));
		this.addEvent(event);
	}

	/**The plane is told to board  by the {@link ControlTower}
	 * {@link BoardingEnd} is added to the event queue after 20 minutes
	 * 
	 */
	public void board() {
		this.boardingTime = this.engine.SimulationDate();
		BoardingEnd boarding = new BoardingEnd(Plane.this.boardingTime.add(LogicalDuration.ofMinutes(20)));
		this.addEvent(boarding);
	}
	
	/**The plane is authorized to go to taxiway 2  by the {@link ControlTower}
	 * {@link Tw2End} is added to the event queue after 2-6 minutes (uniform law)
	 * 
	 */
	public void tw2ing() {
		this.tw2Time = this.engine.SimulationDate();
		//TODO change this.prstate = PlaneResutState.BOARDED;
		int minutes = (int) this.engine.getRand().nextUniform(2, 6.999);
		Tw2End tw2ing = new Tw2End(Plane.this.tw2Time.add(LogicalDuration.ofMinutes(minutes)));
		this.addEvent(tw2ing);
	}


	/**The plane is authorized to go to take off by the {@link ControlTower}
	 * {@link TakeOffEnd} is added to the event queue after 3 minutes
	 * 
	 */
	public void takeoff() {
		this.takeoffTime = this.engine.SimulationDate().getCopy();
		TakeOffEnd takeoff = new TakeOffEnd(Plane.this.takeoffTime.getCopy().add(LogicalDuration.ofMinutes(3)));
		this.addEvent(takeoff);
	}
	
	/**Simulation Event that belong to a {@link Plane} and will indicate that it have finish its approach
	 * 
	 * @author Elouan Autret
	 *
	 */
	private class ApproachingEnd extends SimEvent{

		public ApproachingEnd(LogicalDateTime scheduledDate) {
			super(scheduledDate);
		}

		/**Change of result state to {@link PlaneResutState#APPROACHED} and 
		 * tell the the control tower of its final approach {@link ControlTower#approached()}
		 * 
		 */
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
	
	/**Simulation Event that belong to a {@link Plane}
	 * 
	 * @author Elouan Autret
	 *
	 */
	private class LandingEnd extends SimEvent{

		public LandingEnd(LogicalDateTime scheduledDate) {
			super(scheduledDate);
		}

		/**Change of result state to {@link PlaneResutState#LANDED} and 
		 * tell the the control tower that it is landed {@link ControlTower#landed()}
		 * 
		 */
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

	/**Simulation Event that belong to a {@link Plane}
	 * 
	 * @author Elouan Autret
	 *
	 */
	private class Tw1End extends SimEvent{

		public Tw1End(LogicalDateTime scheduledDate) {
			super(scheduledDate);
		}

		/**Change of result state to {@link PlaneResutState#ROULINGENDED} and 
		 * tell the the control tower that it has finish to go to the gates {@link ControlTower#endfly()}
		 * 
		 */
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
	
	/**Simulation Event that belong to a {@link Plane}
	 * 
	 * @author Elouan Autret
	 *
	 */
	private class DeboardingEnd extends SimEvent{

		public DeboardingEnd(LogicalDateTime scheduledDate) {
			super(scheduledDate);
		}

		
		/**Tell the the control tower that it has finish to go clean the plane {@link ControlTower#deboardEnded(Plane)}
		 * 
		 */
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
	
	/**Simulation Event that belong to a {@link Plane}
	 * 
	 * @author Elouan Autret
	 *
	 */
	private class BoardingEnd extends SimEvent{

		public BoardingEnd(LogicalDateTime scheduledDate) {
			super(scheduledDate);
			// TODO Auto-generated constructor stub
		}

		/**Change of result state to {@link PlaneResutState#BOARDED} and 
		 * tell the the control tower that it want to leave the airport now {@link ControlTower#leaving(Plane)}
		 * 
		 */
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

	/**Simulation Event that belong to a {@link Plane}
	 * 
	 * @author Elouan Autret
	 *
	 */
	private class Tw2End extends SimEvent{

		public Tw2End(LogicalDateTime scheduledDate) {
			super(scheduledDate);
			// TODO Auto-generated constructor stub
		}

		/**Change of result state to {@link PlaneResutState#ARRIVEDRUNWAY} and 
		 * tell the the control tower that it is arrived at the runway {@link ControlTower#readyfly()}
		 * 
		 */
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
	

	/**Simulation Event that belong to a {@link Plane} and is only one to log data
	 * of all important Dates of the plane
	 * 
	 * @author Elouan Autret
	 *
	 */
	private class TakeOffEnd extends SimEvent{

		public TakeOffEnd(LogicalDateTime scheduledDate) {
			super(scheduledDate);
		}

		/**Change of result state to {@link PlaneResutState#OUTOFREACH} and 
		 * tell the the control tower that it has finish the take off {@link ControlTower#outOfReach(Plane)}
		 * , then log the data.
		 */
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

		/**return  a string array of all date important to the plane plus the delay of the plane
		 * and its gate number the hour of and day of creation (to facilitate analysis later on)
		 * 
		 */
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
			return "planedetail";
		}
		   
	}
	






}
