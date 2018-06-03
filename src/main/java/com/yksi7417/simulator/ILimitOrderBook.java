package com.yksi7417.simulator;

public interface ILimitOrderBook {

	public abstract String getTicker();

	public abstract void placeOrder(LimitOrder limitOrder);

}