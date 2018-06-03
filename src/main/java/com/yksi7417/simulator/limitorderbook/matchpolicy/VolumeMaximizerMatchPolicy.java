package com.yksi7417.simulator.limitorderbook.matchpolicy;

import java.util.ArrayList;
import java.util.Collections;
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
		if (bidQueue.isEmpty() || askQueue.isEmpty())
			return Collections.emptyList();
		return determineMatchTrades();
	}
	
	private List<Trade> determineMatchTrades() {
		Long lastMatchQty = null; 
		List<Trade> lastMatchTrades = null;
		List<Double> possibleMatchPrices = determinePossibleMatchPrices(); 
		for (Double matchPrice : possibleMatchPrices ) {
			List<Trade> matchTrade = matchLimitOrder(matchPrice);
			long matchQty = matchTrade.stream().mapToLong(t -> t.getQty()).sum();
			if (lastMatchQty == null){
				lastMatchTrades = matchTrade;				
				lastMatchQty = matchQty;
			}
			else if (lastMatchQty > matchQty)
				break;
		}
		return lastMatchTrades;
	}
	
	private List<Double> determinePossibleMatchPrices(){
		double maxMatchingPrice = bidQueue.get(0).getPrice();
		double minMatchingPrice = askQueue.get(0).getPrice();
		
		List<Double> result = new LinkedList<Double>();
		double currentPx = 0.0; 
		
		for (ILimitOrder askOrder : askQueue){
			if (PriceUtils.isLessThan(askOrder.getPrice(), minMatchingPrice) || 
				PriceUtils.isGreaterThan(askOrder.getPrice(), maxMatchingPrice))
				break; 
			
			if (PriceUtils.isEqual(0.0, currentPx) || !PriceUtils.isEqual(currentPx, askOrder.getPrice())){
				result.add(askOrder.getPrice());
				currentPx = askOrder.getPrice();
			}
		}
		
		return result; 
	}
			
	private List<Trade> matchLimitOrder(double matchPrice) {
		List<ILimitOrder> leftQueue = new LinkedList<ILimitOrder>(bidQueue);
		List<ILimitOrder> rightQueue = new LinkedList<ILimitOrder>(askQueue);
		
		List<Trade> trades = new ArrayList<Trade>();
		while (!leftQueue.isEmpty() && !rightQueue.isEmpty()) {
			MatchableLimitOrder leftMatchOrder = new MatchableLimitOrder(leftQueue.remove(0)); 
			if (PriceUtils.isLessThan(leftMatchOrder.getPrice(), matchPrice)) break;
			
			while (leftMatchOrder.getQty() > 0 && !rightQueue.isEmpty() ) {
				ILimitOrder rightOrder = rightQueue.remove(0);
				MatchableLimitOrder rightMatchOrder = new MatchableLimitOrder(rightOrder);
				Trade tradeEvent = matches(leftMatchOrder, rightMatchOrder, matchPrice);
				if (tradeEvent == null) break;
				trades.add(tradeEvent);
				if (rightMatchOrder.getQty() > 0)
					rightQueue.add(new LimitOrder(rightMatchOrder));
			}
			
			if (leftMatchOrder.getQty() > 0)
				leftQueue.add(new LimitOrder(leftMatchOrder));
		}
		
		askQueue.clear();
		askQueue.addAll(rightQueue);
		bidQueue.clear();
		bidQueue.addAll(leftQueue);
		
		return trades;
	}
	
	private Trade matches(MatchableLimitOrder leftOrder, MatchableLimitOrder rightOrder, double matchPrice){
		if (leftOrder.getSide().equals(rightOrder.getSide())) return null; 
		if (!PriceUtils.isEqualOrBetter(leftOrder.getSide(), leftOrder.getPrice(), rightOrder.getPrice())) return null; 
		return generateMatchTrade(leftOrder, rightOrder, matchPrice);
	}
	
	private Trade generateMatchTrade(MatchableLimitOrder leftOrder, MatchableLimitOrder rightOrder, double matchPrice) {
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
		return new Trade(matchPrice, tradeQty);
	}
	
	
}
