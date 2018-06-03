package com.yksi7417.simulator.limitorderbook;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.logging.Logger;

import com.yksi7417.simulator.clock.IWatch;
import com.yksi7417.simulator.common.LimitOrder;
import com.yksi7417.simulator.common.PriceUtils;

public class AuctionLimitOrderBook implements ILimitOrderBook {
	private static Logger LOG = Logger.getLogger(AuctionLimitOrderBook.class.getName());
		
	private final String ticker;
	private final IWatch watch; 
	
	private final Comparator<LimitOrder> bidPxComparator 
		= (LimitOrder o1, LimitOrder o2)-> (int)((o2.getPrice()-o1.getPrice())/PriceUtils.Epsilon); 
	private final Comparator<LimitOrder> askPxComparator 
		= (LimitOrder o1, LimitOrder o2)-> (int)((o1.getPrice()-o2.getPrice())/PriceUtils.Epsilon); 

	PriorityQueue<LimitOrder> bidQueue = new PriorityQueue<>( new PriceTimeComparator(bidPxComparator));
	PriorityQueue<LimitOrder> askQueue = new PriorityQueue<>( new PriceTimeComparator(askPxComparator));
					
	public AuctionLimitOrderBook(IWatch watch, String ticker) {
		super();
		this.watch = watch; 
		this.ticker = ticker;
	}

	/* (non-Javadoc)
	 * @see com.yksi7417.simulator.ILimitOrderBook#getTicker()
	 */
	@Override
	public String getTicker() {
		return ticker;
	} 
		
	/* (non-Javadoc)
	 * @see com.yksi7417.simulator.ILimitOrderBook#placeOrder(com.yksi7417.simulator.LimitOrder)
	 */
	@Override
	public void placeOrder(LimitOrder limitOrder) {
		PriorityQueue<LimitOrder> sameSideQueue = getSameSideQueue(limitOrder);
		sameSideQueue.add(limitOrder);
		printPQ(ticker, bidQueue, askQueue);
	}
	
	public void match() {
		
	}
	
	private void printPQ(String ticker, PriorityQueue<LimitOrder> bid, PriorityQueue<LimitOrder> ask){
		int maxDepth = Math.max(bid.size(), ask.size());
		Iterator<LimitOrder> bidIter = bid.iterator();
		Iterator<LimitOrder> askIter = ask.iterator();
		
		LOG.info("================ New Order Book " + watch.millisecondsSinceStart() / 1000 + "seconds since start " );
		for (int i = 0; i < maxDepth; i++) {
			String bidString = "";
			String askString = "";
			if (bidIter.hasNext()) bidString = bidIter.next().toString();
			if (askIter.hasNext()) askString = askIter.next().toString();
			LOG.info(pad(bidString, askString, 60));
		}
	}
	
	private static String pad(String s1, String s2, int n) {
	     return String.format("%1$" + n + "s", s1) + "  " + String.format("%1$-" + n + "s", s2);  
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
