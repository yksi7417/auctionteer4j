package com.yksi7417.simulator.common;

public class Trade {
	private final double price;
	private final long qty;
	private final long timestamp;
	
	public Trade(double price, long qty, long timestamp) {
		super();
		this.price = price;
		this.qty = qty;
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "[" + qty + "@" + price + ", ts=" + timestamp + "]";
	}
	
}
