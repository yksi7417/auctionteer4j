package com.yksi7417.simulator.limitorderbook;

import com.yksi7417.simulator.common.LimitOrder;

public interface ILimitOrderBook {

	public abstract String getTicker();

	public abstract void placeOrder(LimitOrder limitOrder);

}