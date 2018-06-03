package com.yksi7417.simulator.common;

public interface ILimitOrder {

	public abstract long getOrderid();

	public abstract Side getSide();

	public abstract long getQty();

	public abstract double getPrice();

	public abstract long getTimestamp();

}