package com.yksi7417.simulator.clock;

import java.util.Timer;
import java.util.TimerTask;

// http://ninjago.wikia.com/wiki/Acronix
// Acronix in Ninjago who can speed up / slow down time. 

public class AcronixWatch implements IWatch {

	private final int speedupFactor;
	private final long startTime;
	private final Timer systemTimer = new Timer();
	
	public AcronixWatch(int speedupFactor){
		validateSpeedupFactor(speedupFactor);
		this.speedupFactor = speedupFactor; 
		this.startTime = System.currentTimeMillis();
	}
	
	private void validateSpeedupFactor(int factor){
		if (factor < 0) 
			throw new IllegalArgumentException("Acronix can only speed up / slow down time, can't go back in time");
		if (factor == 0) 
			throw new IllegalArgumentException("Acronix can only speed up / slow down time, can't freeze time");
	}
	
	public void schedule(TimerTask task, long delay, long period){
		systemTimer.schedule(task, adjustTime(delay), adjustTime(period));
	}
	
	private long adjustTime(long timeInMilliseconds) {
		long result = timeInMilliseconds / speedupFactor; 
		return result; 
	}
	
	@Override
	public long now() {
		long timePastInWallClockTime = System.currentTimeMillis() - startTime;
		return (timePastInWallClockTime * speedupFactor);
	}
	
	@Override
	public void cancel() {
		systemTimer.cancel();
	}
	
}
