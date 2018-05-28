package com.yksi7417.simulator;

import java.util.Comparator;
import java.util.PriorityQueue;

public class AuctionLimitOrderBook {
	private final String ticker;

	private final Comparator<LimitOrder> bidPxComparator 
		= (LimitOrder o1, LimitOrder o2)-> (int)((o2.getPrice()-o1.getPrice())/PriceUtils.Epsilon); 
	private final Comparator<LimitOrder> askPxComparator 
		= (LimitOrder o1, LimitOrder o2)-> (int)((o1.getPrice()-o2.getPrice())/PriceUtils.Epsilon); 

	PriorityQueue<LimitOrder> bidQueue = new PriorityQueue<>( new PriceTimeComparator(bidPxComparator));
	PriorityQueue<LimitOrder> askQueue = new PriorityQueue<>( new PriceTimeComparator(askPxComparator));
					
	public AuctionLimitOrderBook(String ticker) {
		super();
		this.ticker = ticker;
	}

	public String getTicker() {
		return ticker;
	} 
	
	public void placeOrder(LimitOrder limitOrder) {
		PriorityQueue<LimitOrder> sameSideQueue = getSameSideQueue(limitOrder);
		sameSideQueue.add(limitOrder);
	}
	
	public void match() {
		
	}

	private PriorityQueue<LimitOrder> getSameSideQueue(LimitOrder limitOrder) {
		switch (limitOrder.getSide()) {
		case BUY:
			return bidQueue; 
		case SELL:
			return askQueue; 
		default:
			throw new RuntimeException("Do not expect any side other than BUY/SELL, please review design");
		}
	}
}
