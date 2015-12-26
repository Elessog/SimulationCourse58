package AirportSim;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import AirportSim.Plane.PlaneResutState;
import AirportSim.Plane.PlaneState;
import SimSys.EntityState;
import SimSys.SimEngine;
import SimuCoiffeur.SimEvent;
import enstabretagne.base.math.MoreRandom;
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
	
	
	public ControlTower(SimEngine engine,int freqPlane,int nbGates) {
		super(engine);
		this.freqPlaneBase = freqPlane;
		runway = new Runway(engine,this);
		tw1 = new  TaxiWays(engine,this);
		tw2 = new  TaxiWays(engine,this);
		gates = new Gate[nbGates];
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
		boolean res = groundedPlanes.remove(exitingPlane);
		allMovingPlanes.add(exitingPlane);
		leavingPlanes.add(exitingPlane);
		exitingPlane.setPlaneState(PlaneState.WAITING);
		goTotw2();
	}
	
	public void goTotw2(){
		if (!tw2.isOccupied() && !leavingPlanes.isEmpty()){
			Plane gototw2Plane = leavingPlanes.get(0);
			tw2.setOccupied(true);
			tw2.setPlane(gototw2Plane);
			tw2Plane = gototw2Plane;
			goingtw2();
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
		System.out.println(this.engine.simulationDate());
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
			this.resetProcessDate(this.scheduledDate.add(LogicalDuration.ofDay(1)));
			ControlTower.this.addEvent(this);
			//adding check sky
			for (int i=1;i<22-7;i++){
				ControlTower.this.addEvent(new ScanningSky(this.scheduledDate.add(LogicalDuration.ofHours(i))));
			}
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
			for (Plane iPlane:incomingPlanes){
				allMovingPlanes.remove(iPlane);
			}
			incomingPlanes.clear();

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
	
	public double getHourlyRate(){
		//TODO considere week end
		LogicalDuration day = this.engine.simulationDate().truncateToDays().soustract(this.engine.getStartTime());
		int od = day.getMinutes()/(60*24)-(day.getMinutes()/(60*24*7))*7;
		if (od > 4){
			int seven = compareTo(7);
			int vingtdeux = compareTo(22);
			if (seven<0 || vingtdeux >= 0){
				return 0;
			}
			return 0.5*1/(double)this.freqPlaneBase;
		}
		int seven = compareTo(7);
		int dix = compareTo(10);
		int dixsept = compareTo(17);
		int dixneuf = compareTo(19);
		int vingtdeux = compareTo(22);
		if (seven<0){
			return 0;
		}
		if (dix<0) {
			return 2/(double)this.freqPlaneBase;
		}
		if (dixsept<0){
			return 1/(double)this.freqPlaneBase;
		}
        if (dixneuf<0){
        	return 2/(double)this.freqPlaneBase;
		}
        if (vingtdeux<0){
        	return 1/(double)this.freqPlaneBase;
		}
		return 0;
	}
	
	private int compareTo(int hour){
		LogicalDateTime baseDay = this.engine.simulationDate().truncateToDays();
		return this.engine.simulationDate().compareTo(baseDay.add(LogicalDuration.ofHours(hour)));
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
			free =!gates[size-1].isOccupied();
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
		if (allMovingPlanes.size()<=0 || runway.isOccupied())
			return;
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
				allMovingPlanes.add(0, authorizedPlane);//reput plane in fist place
		}
		else if (!tw1.isOccupied()) {
			this.incomingPlane= authorizedPlane;
			this.incomingPlanes.remove(authorizedPlane);
			authorizedLandingProc();
		}
		else{
		   allMovingPlanes.add(0, authorizedPlane);
		   return;
		}
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
