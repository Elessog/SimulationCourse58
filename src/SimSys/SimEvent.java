package SimSys;

import SimSys.ISimEvent;
import enstabretagne.base.time.LogicalDateTime;
import enstabretagne.base.utility.IRecordable;


public abstract class SimEvent implements ISimEvent,IRecordable{

			protected LogicalDateTime scheduledDate;

			public SimEvent(LogicalDateTime scheduledDate){
				this.scheduledDate = scheduledDate;
			}
			
			@Override
			public int compareTo(ISimEvent arg0) {
				if (arg0 instanceof SimEvent == false)
					return 0;
				SimEvent other = (SimEvent) arg0;
				return (this.scheduledDate).compareTo(other.scheduledDate);
			}

			@Override
			public abstract String[] getTitles();

			@Override
			public abstract String[] getRecords();

			@Override
			public abstract String getClassement();

			@Override
			public abstract void process();

			@Override
			public LogicalDateTime scheduleDate() {
			    return this.scheduledDate;
			}

			@Override
			public void resetProcessDate(LogicalDateTime simulationDate) {
				this.scheduledDate = simulationDate;
			}
}

