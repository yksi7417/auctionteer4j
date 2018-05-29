package com.yksi7417.simulator;

import java.util.concurrent.atomic.AtomicLong;

/**
 * the method matches is not thread safe.   Designed for thread-confinement environment 
 * @author asimoneta
 *
 */

public class LimitOrder {

	private static final AtomicLong idGenerator = new AtomicLong(0);

	private long orderid; 
	private Side side; 
	private long qty; 
	// price as double, but assume to be 6 decimal precision
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) orderid;
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

	@Override
	public String toString() {
		if (Side.BUY.equals(side))
			return "ts:" + timestamp + " id:" + orderid + " B " + qty + "@" + price;
		return qty + "@" + price + " S " + "ts:" + timestamp + " id:" + orderid;
	}
	
	

}
