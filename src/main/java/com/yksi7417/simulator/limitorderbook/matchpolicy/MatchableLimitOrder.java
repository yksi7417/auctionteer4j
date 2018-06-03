package com.yksi7417.simulator.limitorderbook.matchpolicy;

import com.yksi7417.simulator.common.ILimitOrder;
import com.yksi7417.simulator.common.Side;

/**
 * default visibility - so only classes within this package is mutate this class.   
 * @author asimoneta
 */
class MatchableLimitOrder implements ILimitOrder {
	
	long orderid; 
	Side side; 
	long qty; 
	double price; 	
	long timestamp;
	
	public MatchableLimitOrder(ILimitOrder other) {
		this(other.getOrderid(), other.getSide(), other.getQty(), other.getPrice(), other.getTimestamp());
	}
	
	private MatchableLimitOrder(long orderId, Side side, long qty, double price, long timestamp) {
		super();
		this.orderid = orderId;
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
	
	
	
}
