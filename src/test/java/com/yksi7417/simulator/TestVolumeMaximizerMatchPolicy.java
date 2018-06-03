package com.yksi7417.simulator;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.yksi7417.simulator.common.ILimitOrder;
import com.yksi7417.simulator.common.LimitOrder;
import com.yksi7417.simulator.common.PriceUtils;
import com.yksi7417.simulator.common.Side;
import com.yksi7417.simulator.common.Trade;
import com.yksi7417.simulator.limitorderbook.matchpolicy.IMatchPolicy;
import com.yksi7417.simulator.limitorderbook.matchpolicy.VolumeMaximizerMatchPolicy;

public class TestVolumeMaximizerMatchPolicy {
	IMatchPolicy policyUnderTest;
	List<ILimitOrder> bid , ask ;
	
	@Before
	public void setUp() {
		bid = new LinkedList<>();
		ask = new LinkedList<>();
		policyUnderTest = new VolumeMaximizerMatchPolicy(bid, ask);
	}
	
	@Test
	public void GivenSameBidAndAsk_ExpectFullMatchWithEmptyBook() {
		bid.add(new LimitOrder(Side.BUY, 1000, 10, 0));
		ask.add(new LimitOrder(Side.SELL, 1000, 10, 0));
		List<Trade> matchResult = policyUnderTest.match();
		assertEquals(1, matchResult.size());
		assertEquals(1000, matchResult.get(0).getQty());
		assertTrue(PriceUtils.isEqual(10.0, matchResult.get(0).getPrice()));
		
		assertTrue(bid.isEmpty());
		assertTrue(ask.isEmpty());
	}
	
	@Test
	public void GivenBidLargerThanAskAtSamePriceLevel_ExpectMatchFullAskSizeWithBidRemaining() {
		bid.add(new LimitOrder(Side.BUY, 1500, 10, 0));
		ask.add(new LimitOrder(Side.SELL, 1000, 10, 0));
		List<Trade> matchResult = policyUnderTest.match();
		assertEquals(1, matchResult.size());
		assertEquals(1000, matchResult.get(0).getQty());
		assertTrue(PriceUtils.isEqual(10.0, matchResult.get(0).getPrice()));
		
		assertTrue(ask.isEmpty());

		assertEquals(1, bid.size());
		assertEquals(500, bid.get(0).getQty());
		assertTrue(PriceUtils.isEqual(10.0, bid.get(0).getPrice()));
	}
	
	
}
