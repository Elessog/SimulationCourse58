package AirportSim;

import AirportServ.AirportScenarioId;
import AirportServ.LoggerUtil;
import SimSys.SimEngine;
import enstabretagne.base.math.MoreRandom;
import enstabretagne.base.time.LogicalDateTime;
import enstabretagne.base.time.LogicalDuration;

public class WorldMain {
	public SimEngine engine;
	public ControlTower controlTower;

	
	public WorldMain(int nbGates,int freqPlanes,MoreRandom rand,LogicalDateTime debut,LogicalDateTime fin,int ouverture,int fermeture){
		AirportScenarioId id= new AirportScenarioId(nbGates, freqPlanes, rand, debut, fin, ouverture, fermeture);
		this.engine = new SimEngine(fin,id);
		debut = debut.truncateToDays();
		System.out.println(debut);
		this.engine.setStartTime(debut);
		this.engine.setCurrentTime(debut);
		
		
		this.engine.setRand(rand);
		this.controlTower = new ControlTower(engine,freqPlanes,nbGates,ouverture,fermeture);
		LogicalDateTime openning = debut.getCopy();
		openning = openning.add(LogicalDuration.ofHours(ouverture));
		LogicalDateTime closing= debut.getCopy();
		closing = closing.add(LogicalDuration.ofHours(fermeture));

		this.controlTower.open_close(openning, closing);
		
		LoggerUtil.getLogger().initialize(engine);
	}
	
	public WorldMain(int nbGates,int freqPlanes,long germe,LogicalDateTime debut,LogicalDateTime fin,int ouverture,int fermeture){
		this(nbGates,freqPlanes,new MoreRandom(germe),debut,fin,ouverture,fermeture);
	
	}
	
	public WorldMain(int nbGates,int freqPlanes,LogicalDateTime debut,LogicalDateTime fin,int ouverture,int fermeture){
		
		this(nbGates,freqPlanes,germeCreate(),debut,fin,ouverture,fermeture);
	
	}
	

	public WorldMain(int nbGates,int freqPlanes,long germe,LogicalDateTime debut,LogicalDateTime fin){
		
		this(nbGates,freqPlanes,germe,debut,fin,7,22);
	
	}
	
	public WorldMain(int nbGates,int freqPlanes,LogicalDateTime debut,LogicalDateTime fin){
		this(nbGates,freqPlanes,debut,fin,7,22);
	
	}

	private static MoreRandom germeCreate() {
    	MoreRandom rand  = new MoreRandom();
		return rand;
	}
	
	public int getNumberEvent(LogicalDuration delta,double hourlyRate){
		return getPoisson((delta.getMinutes()/60.0)*hourlyRate);
	}

	public int getPoisson(double lambda) {
		  double L = Math.exp(-lambda);
		  double p = 1.0;
		  int k = 0;

		  do {
		    k++;
		    p *= this.engine.getRand().nextUniform();
		  } while (p > L);

		  return k - 1;
	}
	
	public String loop(){
		while (this.engine.triggerNextEvent()) {
        	LogicalDuration delta = this.engine.getLastDuration();
        	if (delta.compareTo(LogicalDuration.ofHours(1))>=0)
        		delta = LogicalDuration.ofMinutes(30);//to avoid big number of plnes at openning of airport
        	//System.out.println(delta);
        	double freq = this.controlTower.getHourlyRate();
        	int res =this.getNumberEvent(delta, freq);
            //System.out.println(res);
            while (res >0){
            	(new Plane(this.engine,this.controlTower)).activate();
            	res--;
            }
        }
		LoggerUtil.Terminate();
		return LoggerUtil.locFile;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stubs
		LogicalDateTime debut = new LogicalDateTime("20/12/1991 04:45:00.5000");
		LogicalDateTime fin = new LogicalDateTime("20/03/1992 04:45:00.5000");
        WorldMain world = new WorldMain(5, 5, debut, fin);
        System.out.println(world.engine.getRand().getSeed());
        System.out.println(world.getPoisson(3));
       // world = new WorldMain(5, 5,12345, debut, fin);
        System.out.println(world.engine.getRand().getSeed());
        
        world.engine.resume();
        //Plane plane = new Plane(world.engine,world.controlTower);
        /*while (world.engine.triggerNextEvent()) {
        	LogicalDuration delta = world.engine.getLastDuration();
        	System.out.println(delta);
        	int res =world.getNumberEvent(delta, 10);
            System.out.println(res);
        }*/
        //world.loop();
        System.out.println("end");
	}

}
