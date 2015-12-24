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
	private Plane taxi2Plane;
	private int freqPlaneBase; //normal is freq busy is freq/2 week-end are freq*4 in minute between planes
    public Runway runway;
    public TaxiWays tw1;
    public TaxiWays tw2;
	
	
	public ControlTower(SimEngine engine,int freqPlane) {
		super(engine);
		this.freqPlaneBase = freqPlane;
		runway = new Runway(engine,this);
		tw1 = new  TaxiWays(engine,this);
		tw2 = new  TaxiWays(engine,this);
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
		/*if (incomingPlane == null){
			incomingPlane = incomingPlanes.remove(0);
		}*/
	}
	
	public int findPlaneInList(List<Plane> list,int idPlane){
		int i = 0;
		boolean found=false;
		Iterator<Plane> leavingIt = leavingPlanes.iterator();
		while(!found && leavingIt.hasNext()){
			found = idPlane==leavingIt.next().getIdNumber();
	        if (!found)
	        	i++;
		}
		return i;
	}
	
	public void leaving(Plane exitingPlane){
		groundedPlanes.remove(exitingPlane);
		allMovingPlanes.add(exitingPlane);
		if (!tw2.isOccupied()){
			tw2.setOccupied(true);
			exitingPlane.setPlaneState(PlaneState.GOINGTAKEOFF);
			return;
		}
		leavingPlanes.add(exitingPlane);
		
		exitingPlane.setPlaneState(PlaneState.WAITING);
	}
	
	/**
	 * Plane quit airspace of the airport the runway is free for landing or
	 * @param flyingAwayPlane plane which is leaving
	 */
	public void outOfReach(Plane flyingAwayPlane){
		leavingPlane = null;
		flyingAwayPlane.setState(EntityState.DEAD);
		if (allMovingPlanes.size()<=0)
				return;
		Plane authorizedPlane = allMovingPlanes.remove(0);
		if (authorizedPlane.getPlaneState() == PlaneState.GOINGTAKEOFF){
			// the next plane on takeaway is leaving
			this.leavingPlane = authorizedPlane;
			this.leavingPlanes.remove(authorizedPlane);
			authorizedPlane.setPlaneState(PlaneState.TAKEOFF);
			if (authorizedPlane.getPrstate() == PlaneResutState.ARRIVEDRUNWAY){
				authorizedPlane.takeoff();
			}
		}
		else if (!tw1.isOccupied()) {
			this.incomingPlane= authorizedPlane;
			this.incomingPlanes.remove(authorizedPlane);
			authorizedPlane.setPlaneState(PlaneState.APPROACHING);
			authorizedPlane.approach();
		}
		else{
			//TODO
		}
		
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
			//reopen tomorrow
			this.resetProcessDate(this.scheduledDate.add(LogicalDuration.ofDay(1)));
			ControlTower.this.addEvent(this);
			//System.out.println(this.scheduledDate);
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
			int i;//rerouting all incoming airplanes
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
	
	public double getHourlyRate(){
		//TODO considere week end
		int seven = compareTo( 7);
		int dix = compareTo( 10);
		int dixsept = compareTo( 17);
		int vingt = compareTo( 20);
		if (seven<0){
			return 0;
		}
		if (dix<0) {
			return 2/(double)this.freqPlaneBase;
		}
		if (dixsept<0){
			return 1/(double)this.freqPlaneBase;
		}
        if (vingt<0){
        	return 2/(double)this.freqPlaneBase;
		}
		return 0;
	}
	
	private int compareTo(int hour){
		LogicalDateTime baseDay = this.engine.simulationDate().truncateToDays();
		return this.engine.simulationDate().compareTo(baseDay.add(LogicalDuration.ofHours(hour)));
	}
}
