package com.yksi7417.simulator.limitorderbook.matchpolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import com.yksi7417.simulator.common.LimitOrder;
import com.yksi7417.simulator.common.Trade;

/**
 * 
 * Volume Maximization Algorithm 
		This algorithm tries to find highest price in the order book at 
		which the matched volume is highest i.e. find all the price points 
		at which at least 1 share will match and from amongst those determine 
		the price level that generates highest amount of matched volume. 
		If multiple such price level exists, then highest price is 
		said to be the match price. 
 * 
 * @author asimoneta
 *
 */

public class VolumeMaximizerMatchPolicy implements IMatchPolicy {
	
	private final PriorityQueue<LimitOrder> bid;
	private final PriorityQueue<LimitOrder> ask;
	
	public VolumeMaximizerMatchPolicy(PriorityQueue<LimitOrder> bid, PriorityQueue<LimitOrder> ask){
		this.ask = ask; 
		this.bid = bid; 
	}
	
	public List<Trade> match() {
		List<Trade> tradeList = new ArrayList<Trade>(bid.size() + ask.size());
		return tradeList;
	}
}
