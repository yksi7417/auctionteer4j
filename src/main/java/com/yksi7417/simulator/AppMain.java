package com.yksi7417.simulator;

import com.yksi7417.simulator.clock.AcronixWatch;
import com.yksi7417.simulator.clock.IWatch;

public class AppMain implements Runnable {

	private ILimitOrderBook limitOrderBook; 
	private IRandomOrderGenerator randomOrderGenerator; 
	private IWatch watch; 

	public AppMain(IWatch watch, ILimitOrderBook limitOrderBook, IRandomOrderGenerator randomOrderGenerator) {
		super();
		this.limitOrderBook = limitOrderBook;
		this.randomOrderGenerator = randomOrderGenerator; 
		this.watch = watch;
	}

	@Override
	public void run() {
		this.randomOrderGenerator.run();
	}

	public static void main(String[] args) {
		ILimitOrderBook lob = new AuctionLimitOrderBook("0700.HK");
		IWatch watch = new AcronixWatch(15);
		IRandomOrderGenerator rog = new RandomOrderGenerator(watch, lob);
		AppMain appMain = new AppMain(watch, lob, rog);
		appMain.run();
	}

}
