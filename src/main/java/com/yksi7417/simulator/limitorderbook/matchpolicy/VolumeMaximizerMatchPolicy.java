package com.yksi7417.simulator.limitorderbook.matchpolicy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.List;
import java.util.Set;

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
		Long maxMatchQty = null; 
		Double maxMatchPrice = null; 
		Set<Double> possibleMatchPrices = determinePossiblePricesThatTriggerMatch(); 
		for (Double matchPrice : possibleMatchPrices ) {
			List<Trade> matchTrade = matchLimitOrder(matchPrice, false);
			long matchQty = matchTrade.stream().mapToLong(t -> t.getQty()).sum();
			if (maxMatchQty == null || matchQty > maxMatchQty || (matchQty == maxMatchQty && matchPrice > maxMatchPrice)){
				maxMatchPrice = matchPrice;				
				maxMatchQty = matchQty;
			}
		}
		if (maxMatchPrice == null) return Collections.emptyList();
		return matchLimitOrder(maxMatchPrice, true);
	}
	
	private Set<Double> determinePossiblePricesThatTriggerMatch(){
		HashSet<Double> prices = new HashSet<>();
		
		double maxMatchingPrice = bidQueue.get(0).getPrice();
		double minMatchingPrice = askQueue.get(0).getPrice();
		
		for (ILimitOrder askOrder : askQueue){
			if (PriceUtils.isLessThan(askOrder.getPrice(), minMatchingPrice) || 
				PriceUtils.isGreaterThan(askOrder.getPrice(), maxMatchingPrice))
				continue; 
			prices.add(askOrder.getPrice());
		}
		
		for (ILimitOrder order : bidQueue){
			if (PriceUtils.isLessThan(order.getPrice(), minMatchingPrice) || 
				PriceUtils.isGreaterThan(order.getPrice(), maxMatchingPrice))
				continue; 
			prices.add(order.getPrice());
		}

		return prices;
	}
			
	private List<Trade> matchLimitOrder(double matchPrice, boolean updateOriginalQueues) {
		List<ILimitOrder> leftQueue = new LinkedList<ILimitOrder>(bidQueue);
		List<ILimitOrder> rightQueue = new LinkedList<ILimitOrder>(askQueue);
		
		List<Trade> trades = new ArrayList<Trade>();
		while (!leftQueue.isEmpty() && !rightQueue.isEmpty()) {
			if (noMoreMatch(leftQueue.get(0), rightQueue.get(0), matchPrice)) 
				break; 
			MatchableLimitOrder leftMatchOrder = new MatchableLimitOrder(leftQueue.remove(0)); 
			while (leftMatchOrder.getQty() > 0 && !rightQueue.isEmpty() ) {
				if (noMoreMatch(leftMatchOrder, rightQueue.get(0), matchPrice)) 
					break; 
				ILimitOrder rightOrder = rightQueue.remove(0);
				MatchableLimitOrder rightMatchOrder = new MatchableLimitOrder(rightOrder);
				Trade tradeEvent = matches(leftMatchOrder, rightMatchOrder, matchPrice);
				if (tradeEvent != null) 
					trades.add(tradeEvent);
				if (rightMatchOrder.getQty() > 0)
					rightQueue.add(0, new LimitOrder(rightMatchOrder));
				if (tradeEvent == null) break;
			}
			
			if (leftMatchOrder.getQty() > 0)
				leftQueue.add(0, new LimitOrder(leftMatchOrder));
		}
		
		if (updateOriginalQueues) {
			askQueue.clear();
			askQueue.addAll(rightQueue);
			bidQueue.clear();
			bidQueue.addAll(leftQueue);
		}
		
		if (trades.isEmpty()) 
			return trades; 
		
		Trade lastTradeItem = trades.get(trades.size()-1);
		List<Trade> priceAdjustedTrades = new ArrayList<>(trades.size());
		
		for (Trade t : trades)
			priceAdjustedTrades.add(new Trade(lastTradeItem.getPrice(), t.getQty()));
		
		return priceAdjustedTrades;
	}
	
	private Trade matches(MatchableLimitOrder leftOrder, MatchableLimitOrder rightOrder, double matchPrice){
		if (noMoreMatch(leftOrder, rightOrder, matchPrice)) return null; 
		return generateMatchTrade(leftOrder, rightOrder);
	}

	private boolean noMoreMatch(ILimitOrder leftOrder, ILimitOrder rightOrder, double matchPrice) {
		if (!PriceUtils.isEqualOrBetter(leftOrder.getSide(), leftOrder.getPrice(), rightOrder.getPrice())) return true; 
		if (!PriceUtils.isEqualOrBetter(leftOrder.getSide(), leftOrder.getPrice(), matchPrice)) return true; 
		if (!PriceUtils.isEqualOrBetter(rightOrder.getSide(), rightOrder.getPrice(), matchPrice)) return true;
		return false; 
	}
	
	private Trade generateMatchTrade(MatchableLimitOrder leftOrder, MatchableLimitOrder rightOrder) {
		long tradeQty = 0;
		double matchPrice = 0.0; 
		
		if (leftOrder.qty >= rightOrder.qty) {
			tradeQty = rightOrder.qty;
			leftOrder.qty -= rightOrder.qty;
			rightOrder.qty = 0;
			matchPrice = leftOrder.price;
		}
		else if (leftOrder.qty < rightOrder.qty) {
			tradeQty = leftOrder.qty;
			leftOrder.qty = 0;
			rightOrder.qty -= tradeQty;
			matchPrice = rightOrder.price;
		}
		return new Trade(matchPrice, tradeQty);
	}
	
	
}
