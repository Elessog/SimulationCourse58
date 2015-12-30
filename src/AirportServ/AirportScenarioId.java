package AirportServ;

import enstabretagne.base.math.MoreRandom;
import enstabretagne.base.time.LogicalDateTime;
import enstabretagne.simulation.components.ScenarioId;


public class AirportScenarioId extends ScenarioId {

	public AirportScenarioId(String scenarioId, long repliqueNumber) {
		super(scenarioId, repliqueNumber);
		// TODO Auto-generated constructor stub
	}
	
	public AirportScenarioId(int nbGates,int freqPlanes,MoreRandom rand,LogicalDateTime debut,LogicalDateTime fin,int ouverture,int fermeture) {
		
		
		super("G"+String.valueOf(nbGates)+
				"F"+String.valueOf(freqPlanes)+
				"O"+String.valueOf(ouverture)+
				"C"+String.valueOf(fermeture)+
				"S"+debut.toString()+
				"E"+fin.toString()
				,
				rand.getSeed());
	}

}
