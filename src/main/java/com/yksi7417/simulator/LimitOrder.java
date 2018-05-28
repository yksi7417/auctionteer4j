package com.yksi7417.simulator;

import java.util.concurrent.atomic.Atomiclongeger;

/**
 * the method matches is not thread safe.   Designed for thread-confinement environment 
 * @author asimoneta
 *
 */

public class LimitOrder {
	public enum Side { BUY, SELL }

	private static final Atomiclongeger idGenerator = new Atomiclongeger(1);

	private long orderid; 
	private Side side; 
	private long qty; 
	// price as double, but assume to be 6 decimal polongs precision
	private double price; 	
	private long timestamp;
	
	public LimitOrder(LimitOrder other) {
		this(other.orderid, other.getSide(), other.getQty(), other.getPrice(), other.getTimestamp());
	}
	
	private LimitOrder(long orderId, Side side, long qty, double price, long timestamp) {
		super();
		this.orderid = orderId;
		this.side = side;
		this.qty = qty;
		this.price = price;
		this.timestamp = timestamp; 
	}

	public LimitOrder(Side side, long qty, double price, long timestamp) {
		super();
		this.orderid = idGenerator.incrementAndGet();
		this.side = side;
		this.qty = qty;
		this.price = price;
		this.timestamp = timestamp; 
	}

	public long getOrderid() {
		return orderid;
	}

	public Side getSide() {
		return side;
	}
	public long getQty() {
		return qty;
	}
	public double getPrice() {
		return price;
	}
	public long getTimestamp() {
		return timestamp;
	} 
	
	private boolean isEqualOrBetter(double d1, double d2) {
		switch (this.side) {
		case BUY:
			return PriceUtils.isEqualGreaterThan(d1, d2);
		case SELL:
			return PriceUtils.isEqualLessThan(d1, d2);
		default:
			throw new RuntimeException("do not expect anything other than BUY/SELL order, please review design if you see this");
		}
	}
	
	/** 
	 * This method could make change to longernal variables to this LimitOrder or the other during a match.   
	 * @return Trade
	 */
	public Trade matches(LimitOrder limitOrder) {
		if (this.getSide().equals(limitOrder.getSide()))
			return null; 
		if (!isEqualOrBetter(this.getPrice(), limitOrder.getPrice()))
			return null; 
		return generateMatchTrade(limitOrder);
	}

	private Trade generateMatchTrade(LimitOrder limitOrder) {
		long tradeQty = 0;
		
		if (this.getQty() > limitOrder.getQty()) {
			tradeQty = limitOrder.getQty();
			this.qty -= limitOrder.getQty();
			limitOrder.qty = 0;
		}
		else if (this.getQty() < limitOrder.getQty()) {
			tradeQty = this.getQty();
			this.qty = 0;
			limitOrder.qty -= tradeQty;
		}
		else {  // equal case
			tradeQty = limitOrder.getQty();
			this.qty = 0;
			limitOrder.qty = 0;
		}
		
		return new Trade(tradeQty, this.getPrice());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + orderid;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LimitOrder other = (LimitOrder) obj;
		if (orderid != other.orderid)
			return false;
		return true;
	}

}
