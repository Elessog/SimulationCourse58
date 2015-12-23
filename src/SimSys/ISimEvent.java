package SimSys;

import enstabretagne.base.time.LogicalDateTime;

public interface ISimEvent extends Comparable<ISimEvent> {
 
	void process();
	LogicalDateTime scheduleDate();
	void resetProcessDate(LogicalDateTime simulationDate);
	
}
