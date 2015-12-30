package AirportSim;

import AirportServ.AirportScenarioId;
import AirportServ.LoggerUtil;
import SimSys.SimEngine;
import enstabretagne.base.math.MoreRandom;
import enstabretagne.base.time.LogicalDateTime;
import enstabretagne.base.time.LogicalDuration;
/**This class contain the airport simulation with the ControlTower 
 * and the engine. The simulation is done here in {@link #loop()}.
 * 
 * @author Elouan Autret
 *
 */
public class WorldMain {

	public SimEngine engine;
	public ControlTower controlTower;

	/**Parent Constructor ofWorldMain class 
	 * 
	 * @param nbGates number of gates the aiport have
	 * @param freqPlanes frequency of planes arriving on airport (in minutes between each planes)
	 * is doubled on week end and divised by two on rush hours (7-10 17-20)
	 * @param rand MoreRandom object that will provide randomness with or without specifying the seed
	 * @param debut start of simulation
	 * @param fin end of simulation
	 * @param ouverture openning of airport
	 * @param fermeture closing of airport
	 */
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
		
		this(nbGates,freqPlanes,new MoreRandom(),debut,fin,ouverture,fermeture);
	
	}
	

	public WorldMain(int nbGates,int freqPlanes,long germe,LogicalDateTime debut,LogicalDateTime fin){
		
		this(nbGates,freqPlanes,germe,debut,fin,7,22);
	
	}
	
	public WorldMain(int nbGates,int freqPlanes,LogicalDateTime debut,LogicalDateTime fin){
		this(nbGates,freqPlanes,debut,fin,7,22);
	
	}

	/**get estimation of plane to be created dependent of time elasped and the rate of
	 * plane creation
	 * 
	 * @param delta time since last event
	 * @param hourlyRate hourly rate of planes to be created
	 * @return random number of plane following poisson law
	 */
	public int getNumberEvent(LogicalDuration delta,double hourlyRate){
		return getPoisson((delta.getMinutes()/60.0)*hourlyRate);
	}

	/**Generation of Poisson-distributed random variables by Donald Knuth
	 * @param lambda Poisson law parameters (can be seen as average to be expected)
	 * @return random number Following Poisson Law
	 */
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
	
	/**This function is where the simulation run.
	 * 
	 * @return location of the log data file
	 */
	public String loop(){
		while (this.engine.triggerNextEvent()) {
        	LogicalDuration delta = this.engine.getLastDuration();
        	if (delta.compareTo(LogicalDuration.ofHours(1))>=0)
        		delta = LogicalDuration.ofMinutes(30);//to avoid big number of planes at openning of airport
        	double freq = this.controlTower.getHourlyRate();
        	int res =this.getNumberEvent(delta, freq);
            while (res >0){
            	(new Plane(this.engine,this.controlTower)).activate();
            	res--;
            }
        }
		LoggerUtil.Terminate();
		return LoggerUtil.locFile;
	}
	
	/*public static void main(String[] args) {
		LogicalDateTime debut = new LogicalDateTime("20/12/1991 04:45:00.5000");
		LogicalDateTime fin = new LogicalDateTime("20/03/1992 04:45:00.5000");
        WorldMain world = new WorldMain(5, 5, debut, fin);
        System.out.println(world.engine.getRand().getSeed());
        System.out.println(world.getPoisson(3));
       // world = new WorldMain(5, 5,12345, debut, fin);
        System.out.println(world.engine.getRand().getSeed());
        
        world.engine.resume();
        //Plane plane = new Plane(world.engine,world.controlTower);
        while (world.engine.triggerNextEvent()) {
        	LogicalDuration delta = world.engine.getLastDuration();
        	System.out.println(delta);
        	int res =world.getNumberEvent(delta, 10);
            System.out.println(res);
        }
        //world.loop();
        System.out.println("end");
	}*/

}
