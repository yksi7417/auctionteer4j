package com.yksi7417.simulator;

public class Trade {
	long size; 
	double price;
	public Trade(long size, double price) {
		super();
		this.size = size;
		this.price = price;
	}
	public long getSize() {
		return size;
	}
	public double getPrice() {
		return price;
	} 
	
}
