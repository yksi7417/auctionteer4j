package com.yksi7417.simulator;

import org.junit.Test;

import com.yksi7417.simulator.clock.MuggleWatch;
import com.yksi7417.simulator.common.LimitOrder;
import com.yksi7417.simulator.common.Side;
import com.yksi7417.simulator.limitorderbook.AuctionLimitOrderBook;
import com.yksi7417.simulator.limitorderbook.ILimitOrderBook;

public class TestLimitOrderBook {
	MuggleWatch watch = new MuggleWatch(); 
	
	@Test
	public void expectEmptyBook() {
		ILimitOrderBook lob = new AuctionLimitOrderBook(watch, "0700.HK"); 
		lob.placeOrder(new LimitOrder(Side.BUY, 1000, 10, 0));
		lob.placeOrder(new LimitOrder(Side.SELL, 1000, 10.1, 0));
	}
	
}
