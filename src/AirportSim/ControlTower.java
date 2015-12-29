package AirportSim;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import AirportServ.LoggerUtil;
import AirportSim.Plane.PlaneResutState;
import AirportSim.Plane.PlaneState;
import SimSys.EntityState;
import SimSys.SimEngine;
import SimSys.SimEvent;
import enstabretagne.base.time.LogicalDateTime;
import enstabretagne.base.time.LogicalDuration;

public class ControlTower extends SimSys.SimEntity {
	
	public boolean isOpen;
	private List<Plane> groundedPlanes = new ArrayList<Plane>();
	private List<Plane> leavingPlanes = new ArrayList<Plane>();
	private List<Plane> incomingPlanes = new ArrayList<Plane>();
	private List<Plane> allMovingPlanes = new ArrayList<Plane>();
	private Plane incomingPlane;
	private Plane leavingPlane;
	private Plane tw2Plane;
	private int freqPlaneBase; //normal is freq busy is freq/2 week-end are freq*4 in minute between planes
    private int weatherIndice =1;
	public Runway runway;
    public TaxiWays tw1;
    public TaxiWays tw2;
    public Gate[] gates;
	private int ouverture;
	private int fermeture;
	private int hourlyPlaneNb=0;
	
	
	public ControlTower(SimEngine engine,int freqPlane,int nbGates, int ouverture, int fermeture) {
		super(engine);
		this.freqPlaneBase = freqPlane;
		runway = new Runway(engine,this);
		tw1 = new  TaxiWays(engine,this);
		tw2 = new  TaxiWays(engine,this);
		gates = new Gate[nbGates];
		this.ouverture =ouverture;
		this.fermeture = fermeture;
		for (int i=0;i<nbGates;i++){
			gates[i]= new Gate(engine,i);
		}
	}
	
	public int checkNumFree(){
		int i = 0;
		while (!isFree(i)){
			i++;
		}
		return i;
	}
	
	/**
	 * 
	 * @param num number to check if free to assign
	 * @return true if num is free
	 */
	private boolean isFree(int num){
		boolean isFree = true;
		Iterator<Plane> groundedIt = groundedPlanes.iterator();
		while(isFree && groundedIt.hasNext()){
			isFree &= !(num==groundedIt.next().getIdNumber());
		}
		Iterator<Plane> allMovingIt = allMovingPlanes.iterator();
		while(isFree && allMovingIt.hasNext()){
			isFree &= !(num==allMovingIt.next().getIdNumber());
		}
		
		if (incomingPlane==null && leavingPlane==null)
			return isFree;
		if (incomingPlane!=null && leavingPlane!=null)
			return isFree &=(incomingPlane.getIdNumber()!=num)&&(leavingPlane.getIdNumber()!=num);
		if (incomingPlane==null)
			return isFree &=(leavingPlane.getIdNumber()!=num);
		if (leavingPlane==null)
			return isFree&=(incomingPlane.getIdNumber()!=num);
		return isFree;
	}
	
	public void annoucing(Plane newPlane){
		newPlane.setIdNumber(checkNumFree());
		newPlane.setControlTower(this);
		incomingPlanes.add(newPlane);
		allMovingPlanes.add(newPlane);
		this.hourlyPlaneNb++;
		checkNewAuth();
	}
	
	public void approached() {
		incomingPlane.setPlaneState(PlaneState.LANDING);
		incomingPlane.landing();
	}

	public void landed() {
		incomingPlane.setPlaneState(PlaneState.GOINGGATE);
		tw1.setPlane(incomingPlane);
		tw1.setOccupied(true);
		runway.setPlane(null);
		runway.setOccupied(false);
		incomingPlane.tw1ing();
		checkNewAuth();
	}

	public void gated(Plane plane) {
		gates[plane.getGateId()].setOccupied(true);
		gates[plane.getGateId()].setPlane(plane);
		plane.deboard();
	}
	
	public void deboardEnded(Plane plane) {
		//TODO check for closing airport
		//if ()
		board(plane);
	}
    
	public void board(Plane plane){
		plane.setPlaneState(PlaneState.BOARDING);
		plane.board();
	}
	
	public void leaving(Plane exitingPlane){
		groundedPlanes.remove(exitingPlane);
		allMovingPlanes.add(exitingPlane);
		leavingPlanes.add(exitingPlane);
		exitingPlane.setPlaneState(PlaneState.WAITING);
		goTotw2();
	}
	
	public void goTotw2(){
		if (!tw2.isOccupied() && !leavingPlanes.isEmpty()){
			Plane gototw2Plane = leavingPlanes.get(0);
			gates[gototw2Plane.getGateId()].setOccupied(false);
			gates[gototw2Plane.getGateId()].setPlane(null);
			tw2.setOccupied(true);
			tw2.setPlane(gototw2Plane);
			tw2Plane = gototw2Plane;
			goingtw2();
			if (tw1.getPlane()!=null){//a gate is now free
				if  (tw1.getPlane().getPrstate()==PlaneResutState.ROULINGENDED)
					endfly();
			}
		}
	}
	
	public void goingtw2(){
		tw2Plane.setPlaneState(PlaneState.GOINGTAKEOFF);
		tw2Plane.tw2ing();
	}

	public void readyfly() {
		checkNewAuth();
	}
	
	/**
	 * Plane quit airspace of the airport the runway is free for landing or else
	 * @param flyingAwayPlane plane which is leaving
	 */
	public void outOfReach(Plane flyingAwayPlane){
		leavingPlane = null;
		runway.setOccupied(false);
		runway.setPlane(null);
		flyingAwayPlane.setState(EntityState.DEAD);
		System.out.print("Fly ");
		System.out.print(flyingAwayPlane.getIdNumber());
		System.out.println(" gone");
		System.out.println(this.engine.SimulationDate());
		checkNewAuth();		
	}
	
	
	public void open_close(LogicalDateTime ouverture,LogicalDateTime fermeture){
		   this.addEvent(new OpenTower(ouverture));
		   this.addEvent(new CloseTower(fermeture));
	   }

	private class OpenTower extends SimEvent{

		public OpenTower(LogicalDateTime scheduledDate) {
			super(scheduledDate);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void process() {
			ControlTower.this.isOpen = true;
			//check weather
			ControlTower.this.checkWeather();
			//reopen tomorrow
			//adding check sky
			for (int i=0;i<=ControlTower.this.fermeture-ControlTower.this.ouverture;i++){
				ControlTower.this.addEvent(new ScanningSky(this.scheduledDate.add(LogicalDuration.ofHours(i))));
			}
			this.resetProcessDate(this.scheduledDate.add(LogicalDuration.ofDay(1)));
			ControlTower.this.addEvent(this);
			
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

	private class CloseTower extends SimEvent{

		public CloseTower(LogicalDateTime scheduledDate) {
			super(scheduledDate);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void process() {
			ControlTower.this.isOpen = false;
			//rerouting all incoming airplanes
			for (Plane iPlane:ControlTower.this.incomingPlanes){
				ControlTower.this.allMovingPlanes.remove(iPlane);
			}
			ControlTower.this.incomingPlanes.clear();

			this.resetProcessDate(this.scheduledDate.add(LogicalDuration.ofDay(1)));
			ControlTower.this.addEvent(this);
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
	
	private class ScanningSky extends SimEvent{

		public ScanningSky(LogicalDateTime scheduledDate) {
			super(scheduledDate);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void process() {
			//void because its just to help create plane randomly
			LoggerUtil.Data(this);
			ControlTower.this.hourlyPlaneNb = 0;
		}

		@Override
		public String[] getTitles() {
			String[] a = {"hourlyPlane","SimuRate","Day"};
			return a;
		}

		@Override
		public String[] getRecords() {
			LogicalDuration day = ControlTower.this.engine.SimulationDate().truncateToDays().soustract(ControlTower.this.engine.getStartTime());
			int nbDay = day.getMinutes()/(60*24);
			String[] a = {String.valueOf(ControlTower.this.hourlyPlaneNb)
					,String.valueOf(ControlTower.this.getHourlyRate()),String.valueOf(nbDay)};
			return a;
		}

		@Override
		public String getClassement() {
			// TODO Auto-generated method stub
			return "hourlyRate";
		}
		   
	   }
	
	public double getHourlyRate(){
		LogicalDuration day = this.engine.SimulationDate().truncateToDays().soustract(this.engine.getStartTime());
		
		int od = day.getMinutes()/(60*24)-(day.getMinutes()/(60*24*7))*7;
		if (od > 4){//if in week end
			int openningHour = compareTo(this.ouverture);
			int closingHour = compareTo(this.fermeture);
			if (openningHour<0 || closingHour >= 0){
				return 0;
			}
			return 0.5*60/(double)this.freqPlaneBase;
		}
		int openningHour = compareTo(this.ouverture);
		int dix = compareTo(10);
		int dixsept = compareTo(17);
		int dixneuf = compareTo(19);
		int closingHour = compareTo(this.fermeture);
		if (openningHour<0){
			return 0;
		}
		if (dix<0) {
			return 120/(double)this.freqPlaneBase;
		}
		if (dixsept<0){
			return 60/(double)this.freqPlaneBase;
		}
        if (dixneuf<0){
        	return 120/(double)this.freqPlaneBase;
		}
        if (closingHour<0){
        	return 60/(double)this.freqPlaneBase;
		}
		return 0;
	}
	
	private int compareTo(int hour){
		LogicalDateTime baseDay = this.engine.SimulationDate().truncateToDays();
		return this.engine.SimulationDate().compareTo(baseDay.add(LogicalDuration.ofHours(hour)));
	}

	

	private boolean gateFree() {
		int size = gates.length;
		boolean free = false;
		while (size>0 & !free){
			free =!gates[size-1].isOccupied();
			size--;
		}
		return free;
	}
	
	public int getIdGateFree(){
		int size = gates.length;
		boolean free = false;
		int i=0;
		while (i<size & !free){
			free =!gates[i].isOccupied();
			if (!free) 
			  i++;
		}
		return i;
	}

	public void endfly() {
		if (gateFree()){
			incomingPlane.gating(getIdGateFree());
			tw1.setOccupied(false);
			tw1.setPlane(null);
			groundedPlanes.add(incomingPlane);
			incomingPlane = null;
			checkNewAuth();
		}	
	}
	
	public void authorizedLandingProc(){
		runway.setOccupied(true);
		runway.setPlane(incomingPlane);
		incomingPlane.setPlaneState(PlaneState.APPROACHING);
		incomingPlane.approach();
	}


	private void checkNewAuth() {
		// TODO Auto-generated method stub
		if (allMovingPlanes.isEmpty())
			return;
		if (runway.isOccupied()){
			goTotw2();
			return;
		}
		Plane authorizedPlane = allMovingPlanes.remove(0);
		if (authorizedPlane.getPlaneState() == PlaneState.GOINGTAKEOFF){
			// the next plane on takeaway is leaving
			
			if (authorizedPlane.getPrstate() == PlaneResutState.ARRIVEDRUNWAY){
				tw2.setPlane(null);
				tw2.setOccupied(false);
				tw2Plane = null;
				runway.setOccupied(true);
				runway.setPlane(authorizedPlane);
				this.leavingPlane = authorizedPlane;
				this.leavingPlanes.remove(authorizedPlane);
				goTotw2();//authorize plane to go to tw2
				authorizedPlane.setPlaneState(PlaneState.TAKEOFF);
				authorizedPlane.takeoff();
			}
			else
				allMovingPlanes.add(0, authorizedPlane);//reput plane in first place
		}
		else if (authorizedPlane.getPrstate()==PlaneResutState.FLYING) {

			if (!tw1.isOccupied()){ 
				this.incomingPlane= authorizedPlane;
				this.incomingPlanes.remove(authorizedPlane);
				authorizedLandingProc();
			}
			else if (!leavingPlanes.isEmpty()){//if a plane ready to takeoff it takeoff
				allMovingPlanes.add(0, authorizedPlane);
				Plane r =leavingPlanes.get(0);
				goTotw2();
				if (r.getPrstate() == PlaneResutState.ARRIVEDRUNWAY){
					allMovingPlanes.remove(r);
					allMovingPlanes.add(0,r);
					checkNewAuth();
				}
			}
			else
				allMovingPlanes.add(0, authorizedPlane);//reput plane in first place
		}
		else{
		   allMovingPlanes.add(0, authorizedPlane); 
		}
		goTotw2();//authorize plane to go to tw2
	}

	public int getWeatherIndice() {
		return this.weatherIndice;
	}

	public void setWeatherIndice(int weatherIndice) {
		this.weatherIndice = weatherIndice;
	}

	public void checkWeather() {
		int res = this.engine.getRand().nextInt(8);
		if (res == 0){
			setWeatherIndice(2);
		}
		else
			setWeatherIndice(1);
	}
	
	
}
