package com.yksi7417.simulator.limitorderbook.matchpolicy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.List;

import com.yksi7417.simulator.common.ILimitOrder;
import com.yksi7417.simulator.common.LimitOrder;
import com.yksi7417.simulator.common.PriceUtils;
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
	
	private final List<ILimitOrder> bidQueue;
	private final List<ILimitOrder> askQueue;
	
	public VolumeMaximizerMatchPolicy(List<ILimitOrder> bid, List<ILimitOrder> ask){
		this.askQueue = ask; 
		this.bidQueue = bid; 
	}
	
	public List<Trade> match() {
		double matchPrice = determineMatchPrice();
		return match(matchPrice);
	}
	
	private double determineMatchPrice(){
		for (ILimitOrder bidOrder : bidQueue){
			
		}
//		FindPossibleMatchPricesBetweenOverlapArea(tradeList); 
		return 0.0; 
	}
	
	private List<Trade> match(double matchPrice)
	{
		List<Trade> result = new ArrayList<Trade>();
		return result; 
	}
	
//	private void FindPossibleMatchPricesBetweenOverlapArea(List<Trade> tradeList) {
//		List<ILimitOrder> askCopy = new LinkedList<>(askQueue);
//		List<ILimitOrder> bidCopy = new LinkedList<>(bidQueue);
//		
//		HashMap<Integer, Long> potentialMatchList = new HashMap<>(bidCopy.size() + askCopy.size());
//		while (askCopy.first().getPrice() >= bidCopy.first().getPrice()){
//			List<Trade> tradeEvents = matchLimitOrder(askCopy, bidCopy);
//			
//		}
//	}
//	
//	private List<Trade> matchLimitOrder(List<ILimitOrder> leftQueue, List<ILimitOrder> rightQueue) {
//		List<Trade> trades = new ArrayList<Trade>();
//		MatchableLimitOrder leftMatchOrder = new MatchableLimitOrder(leftQueue.remove()); 
//		while (leftMatchOrder.getQty() > 0) {
//			ILimitOrder rightOrder = rightQueue.poll();
//			if (rightOrder == null) break; 
//
//			MatchableLimitOrder rightMatchOrder = new MatchableLimitOrder(rightOrder);
//			Trade tradeEvent = matches(leftMatchOrder, rightMatchOrder);
//			if (tradeEvent == null) break;
//			trades.add(tradeEvent);
//			if (rightMatchOrder.getQty() > 0)
//				rightQueue.add(new LimitOrder(rightMatchOrder));
//		}
//		if (leftMatchOrder.getQty() > 0)
//			leftQueue.add(new LimitOrder(leftMatchOrder));
//		return trades;
//	}
//	
//	private Trade matches(MatchableLimitOrder leftOrder, MatchableLimitOrder rightOrder){
//		if (leftOrder.getSide().equals(rightOrder.getSide())) return null; 
//		if (!PriceUtils.isEqualOrBetter(leftOrder.getSide(), leftOrder.getPrice(), rightOrder.getPrice())) return null; 
//		return generateMatchTrade(leftOrder, rightOrder);
//	}
	
	private Trade generateMatchTrade(MatchableLimitOrder leftOrder, MatchableLimitOrder rightOrder) {
		long tradeQty = 0;
		
		if (leftOrder.qty >= rightOrder.qty) {
			tradeQty = rightOrder.qty;
			leftOrder.qty -= rightOrder.qty;
			rightOrder.qty = 0;
		}
		else if (leftOrder.qty < rightOrder.qty) {
			tradeQty = leftOrder.qty;
			leftOrder.qty = 0;
			rightOrder.qty -= tradeQty;
		}
		return new Trade(leftOrder.getPrice(), tradeQty);
	}
	
	
}
