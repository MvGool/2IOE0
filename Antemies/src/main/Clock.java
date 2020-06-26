package main;

import main.objects.NestObject;

public class Clock {
	
	private NestObject nest;
	private final long ROUND_DURATION = 30000; // 30 seconds
	private long startTime;
	private long endTime;
	
	public Clock(NestObject nest) {
		this.nest = nest;
	}
	
	public void run() {
		while (true) {
			startTime = System.currentTimeMillis();
			nest.feedAnts();
			nest.removeDeadAnts();
			nest.increasePopulation();
			endTime = System.currentTimeMillis();
			try {
				Thread.sleep(ROUND_DURATION - (endTime - startTime));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
