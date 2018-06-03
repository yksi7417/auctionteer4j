package com.yksi7417.simulator.incomingorders;

import java.util.Random;
import java.util.TimerTask;

import com.yksi7417.simulator.clock.IWatch;
import com.yksi7417.simulator.clock.PeriodicTimerEvent;
import com.yksi7417.simulator.common.LimitOrder;
import com.yksi7417.simulator.common.Side;
import com.yksi7417.simulator.limitorderbook.ILimitOrderBook;

/*
 * Using the information provided above write a computer program in Java that will do the following 

	For the first 15 minutes of execution of the program, 
	it should randomly generate a buy/sell order every 30 seconds. 
	
	Each of these orders should have a size drawn from a normal distribution with 
	mean of 100,000 and a standard deviation of 20,000. 
	The price on the order should be drawn from a normal distribution with 
	mean 50.0 and standard deviation of 6.0. 
 * 
 * 
 */

public class RandomOrderGenerator implements IIncomingOrdersSource {
	private final Random randomNumberGenerator = new Random(); 
	private ILimitOrderBook limitOrderBook; 
	
	private final int IMMEDIATELY = 0; 
	private final int SECONDS = 1000; 
	private final int MINUTES = 60 * SECONDS; 
	
	private final int meanSize = 100000; 
	private final int stdSize = 20000; 
	
	private final double meanPrice = 50.0; 
	private final double stdPrice = 6.0;
	
	private final IWatch watch; 
	
	public RandomOrderGenerator(IWatch watch, ILimitOrderBook lob) {
		this.limitOrderBook = lob; 
		this.watch = watch; 
	}
	
	public void run() {
		TimerTask task = new PeriodicTimerEvent(watch, 15 * MINUTES, () -> sendRandomOrder());
		watch.schedule(task, IMMEDIATELY, 30 * SECONDS);
	}
	
	void sendRandomOrder(){
		long size = meanSize + (long) (this.randomNumberGenerator.nextGaussian() * stdSize) ; 
		double price = meanPrice + (this.randomNumberGenerator.nextGaussian() * stdPrice);
		price = Math.round(price * 100.0) / 100.0;
		Side side = this.randomNumberGenerator.nextBoolean() ? Side.BUY : Side.SELL; 
		this.limitOrderBook.placeOrder(new LimitOrder(side, size, price, watch.millisecondsSinceStart()));
	}
	
}
