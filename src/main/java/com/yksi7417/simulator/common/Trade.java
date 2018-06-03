package com.yksi7417.simulator.common;

public class Trade {
	private final double price;
	private final long qty;
	
	public Trade(double price, long qty) {
		super();
		this.price = price;
		this.qty = qty;
	}

	public double getPrice() {
		return price;
	}

	public long getQty() {
		return qty;
	}

	@Override
	public String toString() {
		return "[" + qty + "@" + price + "]";
	}
	
}
