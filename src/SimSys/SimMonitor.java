package SimSys;

import enstabretagne.base.time.LogicalDateTime;

public class SimMonitor {
		
	private static LogicalDateTime t(int n) {
		String year = Integer.toString(n);
		while (year.length() < 4)
			year = "0" + year;
		return new LogicalDateTime("01/01/" + year + " 00:00:00.0000");
	}
	
	public static class Sim extends SimEntity {

		private String name;
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Sim(SimEngine engine) {
			super(engine);
		}
		
		public void sayHelloTo(Sim other) {
			System.out.println("[" + name + "] Hello " + other + " !");
		}

		@Override
		public String toString() {
			return name;
		}
		
		
		
	}
	
	public static abstract class SimEvent implements ISimEvent {

		public LogicalDateTime scheduledDate;
		

		@Override
		public int compareTo(ISimEvent arg0) {
			if (arg0 instanceof SimEvent == false)
				return 0;
			SimEvent other = (SimEvent) arg0;
			return this.scheduledDate.compareTo(other.scheduledDate);
		}

		@Override
		public LogicalDateTime scheduleDate() {
			return scheduledDate;
		}

		@Override
		public void resetProcessDate(LogicalDateTime simulationDate) {
			scheduledDate = simulationDate;
		}
		
	}
	
	public static void main(String [] args) {
		SimEngine engine = new SimEngine(t(9999),null);
		Sim alice = new Sim(engine);
		alice.setName("alice");
		Sim bob = new Sim(engine);
		bob.setName("Bob");
		SimEvent e1 = new SimEvent() {
			@Override
			public void process() {
				alice.sayHelloTo(bob);
			}
		};
		e1.resetProcessDate(t(2));
		SimEvent e2 = new SimEvent() {
			@Override
			public void process() {
				bob.sayHelloTo(alice);
			}
		};
		e2.resetProcessDate(t(1));
		alice.addEvent(e1);
		bob.addEvent(e2);
		engine.initialize();
		engine.setCurrentTime(t(1));
		engine.resume();
		while (engine.triggerNextEvent()) {}
	}
	
}
