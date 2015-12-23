package AirportSim;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	private Plane incomingPlane;
	private Plane leavingPlane;
	MoreRandom rand;
	private int freqPlaneBase; //normal is freq busy is freq/2 week-end are freq*4 in minute between planes

	public ControlTower(SimEngine engine,int freqPlane) {
		super(engine);
		this.freqPlaneBase = freqPlane;
		
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
		Iterator<Plane> leavingIt = leavingPlanes.iterator();
		while(isFree && leavingIt.hasNext()){
			isFree &= !(num==leavingIt.next().getIdNumber());
		}
		Iterator<Plane> incomingIt = incomingPlanes.iterator();
		while(isFree && incomingIt.hasNext()){
			isFree &= !(num==incomingIt.next().getIdNumber());
		}
		if (incomingPlane==null && leavingPlane==null)
			return isFree;
		if (incomingPlane!=null && leavingPlane!=null)
			return isFree &=(incomingPlane.getIdNumber()==num)&&(leavingPlane.getIdNumber()==num);
		if (incomingPlane==null)
			return isFree &=(leavingPlane.getIdNumber()==num);
		if (leavingPlane==null)
			return isFree&=(incomingPlane.getIdNumber()==num);
		return isFree;
	}
	
	public void annoucing(Plane newPlane){
		newPlane.setIdNumber(checkNumFree());
		incomingPlanes.add(newPlane);
	}
	
	
	public void open_close(LogicalDateTime ouverture,LogicalDateTime fermeture){
		   this.addEvent(new OpenTower(ouverture));
		   this.addEvent(new CloseTower(fermeture));
	   }

	public class OpenTower extends SimEvent{

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

	public class CloseTower extends SimEvent{

		public CloseTower(LogicalDateTime scheduledDate) {
			super(scheduledDate);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void process() {
			ControlTower.this.isOpen = false;
			//reopen tomorrow
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
