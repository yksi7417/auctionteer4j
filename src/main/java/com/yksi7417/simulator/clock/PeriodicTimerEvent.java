package com.yksi7417.simulator.clock;

import java.util.TimerTask;

public class PeriodicTimerEvent extends TimerTask
{
	private final IWatch clock; 
	private final int whenToStopInMs; 
	private final long startTimeInMs; 
	private final Runnable action; 
	
	public PeriodicTimerEvent(IWatch clock, int whenToStopInMs, Runnable action) {
		this.startTimeInMs = clock.now(); 
		this.clock = clock; 
		this.whenToStopInMs = whenToStopInMs; 
		this.action = action; 
	}
	
	@Override
	public void run() {
		if (clock.now() > startTimeInMs + whenToStopInMs) {
			clock.cancel(); 
			return; 
		}
		action.run(); 
	}
}
