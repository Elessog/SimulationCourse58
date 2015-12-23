package SimSys;

import java.util.HashSet;
import java.util.Set;

import enstabretagne.base.math.MoreRandom;
import enstabretagne.base.time.LogicalDateTime;
import enstabretagne.base.time.LogicalDuration;
import TD0.SortedList;

public class SimEngine implements ISimulationDateProvider, IEventObserver {

	private LogicalDateTime currentTime;
	private LogicalDateTime lastTime;
	private LogicalDateTime maxTime;
	private SortedList<ISimEvent> echeancier = new SortedList<>();
	private Set<SimEntity> entities = new HashSet<>();
	private MoreRandom rand;
	
	public SimEngine(LogicalDateTime maxTime) {
		this.maxTime = maxTime;
	}
	
	@Override
	public LogicalDateTime simulationDate() {
		return currentTime;
	}
	
	@Override
	public void onEventPosted(ISimEvent event) {
		echeancier.add(event);
	}
	
	public void initialize() {
		for (SimEntity entity : entities)
			entity.initialize();
	}
	
	public void pause() {
		for (SimEntity entity : entities)
			entity.pause();
	}
	
	public void resume() {
		for (SimEntity entity : entities)
			entity.activate();
	}
	
	public Set<SimEntity> findType(Class<?> type){
		
		Set<SimEntity> ent= new HashSet<>();
		for (SimEntity entity : entities){
			if (entities.getClass().isAssignableFrom(type)){
				ent.add(entity);
			}
		}
		return ent;
	}
	
	public boolean triggerNextEvent() {
		// TODO add maxTime check
		if (echeancier.size() == 0 || currentTime.compareTo(maxTime)>=0) {
			this.terminate();
			return false;
		}
		ISimEvent nextEvent = echeancier.first();
		echeancier.remove(nextEvent);
		lastTime = currentTime.getCopy();
		currentTime = nextEvent.scheduleDate();
		for (SimEntity entity : entities) {
			if (entity.isAffectedBy(nextEvent))
				entity.lock();
		}
		nextEvent.process();
		for (SimEntity entity : entities) {
			if (entity.isAffectedBy(nextEvent))
				entity.release();
		}
		return true;
	}

	public void setCurrentTime(LogicalDateTime currentTime) {
		this.currentTime = currentTime;
	}

	public void addEntity(SimEntity simEntity) {
		entities.add(simEntity);
	}
	
	public void terminate(){
		for (SimEntity entity : entities)
			entity.terminate();
		echeancier.clear();
	}

	public LogicalDateTime getLastTime() {
		return lastTime;
	}
	
	public LogicalDuration getLastDuration() {
		return currentTime.soustract(lastTime);
	}

	public MoreRandom getRand() {
		return rand;
	}

	public void setRand(MoreRandom rand) {
		this.rand = rand;
	}

	
}
