package main;

import main.objects.NestObject;

public class Clock {
	
	private NestObject userNest;
	private NestObject enemyNest;
	private final long ROUND_DURATION = 30000; // 30 seconds
	private long startTime;
	private long endTime;
	
	public Clock(NestObject userNest, NestObject enemyNest) {
		this.userNest = userNest;
		this.enemyNest = enemyNest;
	}
	
	public void run() {
		while (true) {
			startTime = System.currentTimeMillis();
			userNest.feedAnts();
			userNest.removeDeadAnts();
			userNest.increasePopulation();
			enemyNest.feedAnts();
			enemyNest.removeDeadAnts();
			enemyNest.increasePopulation();
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
