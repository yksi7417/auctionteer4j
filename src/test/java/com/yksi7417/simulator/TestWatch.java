package com.yksi7417.simulator;

import java.util.TimerTask;

import com.yksi7417.simulator.clock.IWatch;

public class TestWatch implements IWatch {

	@Override
	public long now() {
		return 900;
	}

	@Override
	public long millisecondsSinceStart() {
		return 0;
	}

	@Override
	public void schedule(TimerTask task, long delay) {
	}

	@Override
	public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
	}

	@Override
	public void cancel() {
	}
}
