package AirportServ;

import AirportSim.WorldMain;
import enstabretagne.base.time.LogicalDateTime;

public class SimuParam {
	int nbGates = 6;
	int freqPlanes = 20;
	long seed =99999999;
	LogicalDateTime debut = null;
	LogicalDateTime fin = null;
	int ouverture = 7;
	int fermeture = 22;
	
	
	public SimuParam(){
		
	}
	
	public void procces(String field,String value){
		if ("nbGates".equals(field )) {
			nbGates = Integer.parseInt(value);
		}
		if ("freqPlanes".equals(field)) {
			freqPlanes = Integer.parseInt(value);
		}
		if ("debut".equals(field )) {
			debut =new LogicalDateTime(value);
		}
		if ("fin".equals(field )) {
			fin =new LogicalDateTime(value);
		}
		if ("ouverture".equals(field )) {
			ouverture = Integer.parseInt(value);
		}
		if ("fermeture".equals(field )) {
			fermeture = Integer.parseInt(value);
		}
		if ("seed".equals(field )) {
			seed = Long.parseLong(value);
		}
	}
	
	public void processIhm(int nbGates,int freq,String seed,String debut,String fin,int ouv,int ferm){
		if (seed!=null){
			this.seed = Long.parseLong(seed);
		}
		this.debut =new LogicalDateTime(debut);
		this.fin =new LogicalDateTime(fin);
		this.ouverture = ouv;
		this.fermeture = ferm;
		this.nbGates = nbGates;
		this.freqPlanes = freq;
	}

	public String startSimu() {
		// TODO Auto-generated method stub
		if (debut == null || fin == null){
			System.err.println("You need to indicate a date for start and end of simulation");
			return "You need to indicate a date for start and end of simulation";
		}
		else{
			WorldMain world = null ;
			if (seed==99999999)
				world= new WorldMain(nbGates, freqPlanes, debut, fin,ouverture,fermeture);
			else
				world= new WorldMain(nbGates, freqPlanes,seed ,debut, fin,ouverture,fermeture);
	        System.out.println(world.engine.getRand().getSeed());
	        world.engine.initialize();
	        world.engine.resume();
	        String res = world.loop();
	        System.out.println("end");
	        return res;
		}
	}
}
