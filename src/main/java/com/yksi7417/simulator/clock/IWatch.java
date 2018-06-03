package com.yksi7417.simulator.clock;

import java.util.TimerTask;

public interface IWatch {
	long now(); 
	long millisecondsSinceStart();
	void schedule(TimerTask task, long delay, long period);
	void cancel(); 
}
