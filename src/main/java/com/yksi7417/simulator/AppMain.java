package com.yksi7417.simulator;

import java.util.logging.Logger;

import com.yksi7417.simulator.clock.AcronixWatch;
import com.yksi7417.simulator.clock.IWatch;
import com.yksi7417.simulator.incomingorders.IIncomingOrdersSource;
import com.yksi7417.simulator.incomingorders.RandomOrderGenerator;
import com.yksi7417.simulator.limitorderbook.AuctionLimitOrderBook;
import com.yksi7417.simulator.limitorderbook.ILimitOrderBook;

public class AppMain implements Runnable {

	static {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
	}
	
	private ILimitOrderBook limitOrderBook; 
	private IIncomingOrdersSource randomOrderGenerator; 
	private IWatch watch; 

	public AppMain(IWatch watch, ILimitOrderBook limitOrderBook, IIncomingOrdersSource randomOrderGenerator) {
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
		IWatch watch = new AcronixWatch(30);
		ILimitOrderBook lob = new AuctionLimitOrderBook(watch , "0700.HK");
		IIncomingOrdersSource rog = new RandomOrderGenerator(watch, lob);
		AppMain appMain = new AppMain(watch, lob, rog);
		appMain.run();
	}

}
