package com.yksi7417.simulator.limitorderbook;

import static com.yksi7417.simulator.common.TimeConstants.MINUTES;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Logger;

import com.yksi7417.simulator.clock.IWatch;
import com.yksi7417.simulator.common.ILimitOrder;
import com.yksi7417.simulator.common.LimitOrder;
import com.yksi7417.simulator.common.PriceUtils;
import com.yksi7417.simulator.common.Trade;
import com.yksi7417.simulator.limitorderbook.matchpolicy.IMatchPolicy;
import com.yksi7417.simulator.limitorderbook.matchpolicy.VolumeMaximizerMatchPolicy;

public class AuctionLimitOrderBook implements ILimitOrderBook {
	private static Logger LOG = Logger.getLogger(AuctionLimitOrderBook.class.getName());
		
	private final String ticker;
	private final IWatch watch; 
	private final IMatchPolicy matchPolicy; 
	
	private final Comparator<ILimitOrder> bidPxComparator 
		= (ILimitOrder o1, ILimitOrder o2)-> (int)((o2.getPrice()-o1.getPrice())/PriceUtils.Epsilon); 
	private final Comparator<ILimitOrder> askPxComparator 
		= (ILimitOrder o1, ILimitOrder o2)-> (int)((o1.getPrice()-o2.getPrice())/PriceUtils.Epsilon); 
	private final PriceTimeComparator bidQueueComparator = new PriceTimeComparator(bidPxComparator); 
	private final PriceTimeComparator askQueueComparator = new PriceTimeComparator(askPxComparator); 
		
	List<ILimitOrder> bidQueue = new LinkedList<>( );
	List<ILimitOrder> askQueue = new LinkedList<>( );
					
	public AuctionLimitOrderBook(IWatch watch, String ticker) {
		super();
		this.watch = watch; 
		this.ticker = ticker;
		TimerTask matchTask = new TimerTask() { public void run() 
			{ 
				match(); 
				watch.cancel();
			} 
		};
		watch.schedule(matchTask, 15 * MINUTES);
		this.matchPolicy = new VolumeMaximizerMatchPolicy(this.bidQueue, this.askQueue);
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
		List<ILimitOrder> sameSideQueue = getSameSideQueue(limitOrder);
		PriceTimeComparator comparitor = getPriceTimeComparator(limitOrder);
        int insertionPoint = Math.abs(Collections.binarySearch(sameSideQueue, limitOrder, comparitor) + 1);
		sameSideQueue.add(insertionPoint, limitOrder);
		printPQ(ticker, bidQueue, askQueue);
	}
	
	public void match() {
		LOG.info("");
		LOG.info("================ Matching Order Book " + watch.millisecondsSinceStart() / 1000 + "seconds since start " );
		List<Trade> tradeList = this.matchPolicy.match(); 
		for (Trade trade : tradeList) {
			LOG.info(trade.toString());
		}
		LOG.info("================ Order Book after match" );
		printPQ(ticker, bidQueue, askQueue);
	}
	
	private void printPQ(String ticker, List<ILimitOrder> bid, List<ILimitOrder> ask){
		int maxDepth = Math.max(bid.size(), ask.size());
		Iterator<ILimitOrder> bidIter = bid.iterator();
		Iterator<ILimitOrder> askIter = ask.iterator();
		
		LOG.info("================ Order Book " + watch.millisecondsSinceStart() / 1000 + "s since start " );
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

	private List<ILimitOrder> getSameSideQueue(ILimitOrder limitOrder) {
		switch (limitOrder.getSide()) {
		case BUY:
			return bidQueue; 
		case SELL:
			return askQueue; 
		default:
			throw new RuntimeException("Do not expect any side other than BUY/SELL, please review design");
		}
	}
	
	private PriceTimeComparator getPriceTimeComparator(ILimitOrder limitOrder) {
		switch (limitOrder.getSide()) {
		case BUY:
			return bidQueueComparator; 
		case SELL:
			return askQueueComparator; 
		default:
			throw new RuntimeException("Do not expect any side other than BUY/SELL, please review design");
		}
	}
	
}
