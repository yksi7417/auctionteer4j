package com.yksi7417.simulator;

import org.junit.Test;

public class TestLimitOrderBook {
	
	@Test
	public void expectEmptyBook() {
		ILimitOrderBook lob = new AuctionLimitOrderBook("0700.HK"); 
		lob.placeOrder(new LimitOrder(Side.BUY, 1000, 10, 0));
		lob.placeOrder(new LimitOrder(Side.SELL, 1000, 10.1, 0));
	}
	
}
