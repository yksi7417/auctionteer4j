package com.yksi7417.simulator.clock;

import java.util.Timer;
import java.util.TimerTask;

/** 
 * Ordinary wall clock - no super power
 * Muggle - a Muggle (/ˈmʌɡəl/) is a person who lacks any sort of magical ability 
 * and was not born in a magical family.
 * @author asimoneta
 *
 */

public class MuggleWatch implements IWatch {
	private final Timer systemTimer = new Timer();
	public void schedule(TimerTask task, long delay, long period){
		systemTimer.schedule(task, delay, period);
	}
	@Override
	public long now() {
		return System.currentTimeMillis();
	}
	@Override
	public void cancel() {
		systemTimer.cancel();
	}
	
}
