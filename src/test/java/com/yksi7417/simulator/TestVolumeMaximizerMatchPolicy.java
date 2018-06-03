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
	
	@Test
	public void GivenAskLargerThanBidAtSamePriceLevel_ExpectMatchFullBidSizeWithAskRemaining() {
		bid.add(new LimitOrder(Side.BUY, 1000, 10, 0));
		ask.add(new LimitOrder(Side.SELL, 1500, 10, 0));
		List<Trade> matchResult = policyUnderTest.match();
		assertEquals(1, matchResult.size());
		assertEquals(1000, matchResult.get(0).getQty());
		assertTrue(PriceUtils.isEqual(10.0, matchResult.get(0).getPrice()));
		
		assertTrue(bid.isEmpty());

		assertEquals(1, ask.size());
		assertEquals(500, ask.get(0).getQty());
		assertTrue(PriceUtils.isEqual(10.0, ask.get(0).getPrice()));
	}
	
	@Test
	public void GivenBidLargerThanAskAtSamePriceLevelWithTwoOrders_ExpectMatchFullAskSizeWithBidRemainingAndTwoTrades() {
		bid.add(new LimitOrder(Side.BUY, 500, 10, 0));
		bid.add(new LimitOrder(Side.BUY, 700, 10, 1));
		ask.add(new LimitOrder(Side.SELL, 1000, 10, 0));
		List<Trade> matchResult = policyUnderTest.match();
		
		assertEquals(2, matchResult.size());
		assertEquals(500, matchResult.get(0).getQty());
		assertTrue(PriceUtils.isEqual(10.0, matchResult.get(0).getPrice()));
		assertEquals(500, matchResult.get(1).getQty());
		assertTrue(PriceUtils.isEqual(10.0, matchResult.get(1).getPrice()));
		
		assertTrue(ask.isEmpty());

		assertEquals(1, bid.size());
		assertEquals(200, bid.get(0).getQty());
		assertTrue(PriceUtils.isEqual(10.0, bid.get(0).getPrice()));
	}
	
	@Test
	public void GivenMultiLevelBidOneLargeAsk_ExpectAskCompletelyMatchedAtOnePriceWithBidResidue() {
		bid.add(new LimitOrder(100001, Side.BUY, 700, 10.1, 1));
		bid.add(new LimitOrder(100002, Side.BUY, 500, 10, 0));
		ask.add(new LimitOrder(100003, Side.SELL, 1000, 10, 0));
		List<Trade> matchResult = policyUnderTest.match();
		
		assertEquals(2, matchResult.size());
		assertEquals(700, matchResult.get(0).getQty());
		assertTrue(PriceUtils.isEqual(10.0, matchResult.get(0).getPrice()));
		assertEquals(300, matchResult.get(1).getQty());
		assertTrue(PriceUtils.isEqual(10.0, matchResult.get(1).getPrice()));
		
		assertTrue(ask.isEmpty());

		assertEquals(1, bid.size());
		assertEquals(200, bid.get(0).getQty());
		assertEquals(100002, bid.get(0).getOrderid());
		assertTrue(PriceUtils.isEqual(10.0, bid.get(0).getPrice()));
	}
	
	@Test
	public void GivenBidAskHasOneMatchLevel_ExpectOnlyOneMatchLevel() {
		bid.add(new LimitOrder(100001, Side.BUY, 700, 10.1, 0));
		bid.add(new LimitOrder(100002, Side.BUY, 500, 10.0, 0));
		bid.add(new LimitOrder(100003, Side.BUY, 600,  9.0, 0));
		
		ask.add(new LimitOrder(200001, Side.SELL, 1000, 10.1, 0));
		ask.add(new LimitOrder(200002, Side.SELL, 1000, 10.2, 0));
		ask.add(new LimitOrder(200003, Side.SELL, 1000, 10.3, 0));
		List<Trade> matchResult = policyUnderTest.match();
		
		assertEquals(1, matchResult.size());
		assertEquals(700, matchResult.get(0).getQty());
		assertTrue(PriceUtils.isEqual(10.1, matchResult.get(0).getPrice()));
		
		assertEquals(2, bid.size());
		assertPriceLevel(bid, 0, 100002, 10.0, 500);
		assertPriceLevel(bid, 1, 100003,  9.0, 600);
		
		assertEquals(3, ask.size());
		assertPriceLevel(ask, 0, 200001, 10.1, 300);
		assertPriceLevel(ask, 1, 200002, 10.2, 1000);
		assertPriceLevel(ask, 2, 200003, 10.3, 1000);
	}
	
	private void assertPriceLevel(List<ILimitOrder> queue, int pos, long id, double px, long qty){
		assertEquals(qty, queue.get(pos).getQty());
		assertEquals(id, queue.get(pos).getOrderid());
		assertTrue(PriceUtils.isEqual(px, queue.get(pos).getPrice()));
	}
	
	@Test
	public void GivenBidAskHasTwoMatchLevel_ExpectMatchAtMaxQuantityLevel() {
		bid.add(new LimitOrder(100000, Side.BUY, 800, 10.2, 0));
		bid.add(new LimitOrder(100001, Side.BUY, 700, 10.1, 0));
		bid.add(new LimitOrder(100002, Side.BUY, 500, 10.0, 0));
		bid.add(new LimitOrder(100003, Side.BUY, 600,  9.0, 0));
		
		ask.add(new LimitOrder(200001, Side.SELL, 1000, 10.1, 0));
		ask.add(new LimitOrder(200002, Side.SELL, 1000, 10.2, 0));
		ask.add(new LimitOrder(200003, Side.SELL, 1000, 10.3, 0));
		List<Trade> matchResult = policyUnderTest.match();
		
		assertEquals(2, matchResult.size());
		assertEquals(800, matchResult.get(0).getQty());
		assertTrue(PriceUtils.isEqual(10.1, matchResult.get(0).getPrice()));
		
		assertEquals(200, matchResult.get(1).getQty());
		assertTrue(PriceUtils.isEqual(10.1, matchResult.get(1).getPrice()));
		
		assertEquals(3, bid.size());
		assertPriceLevel(bid, 0, 100001, 10.1, 500);
		assertPriceLevel(bid, 1, 100002, 10.0, 500);
		assertPriceLevel(bid, 2, 100003,  9.0, 600);
		
		assertEquals(2, ask.size());
		assertPriceLevel(ask, 0, 200002, 10.2, 1000);
		assertPriceLevel(ask, 1, 200003, 10.3, 1000);
	}
	
	@Test
	public void GivenBidAskPriceAreCompletelyDifferent_ExpectMatchAtMaxQuantityLevel() {
		bid.add(new LimitOrder(100000, Side.BUY, 1200, 60.24, 0));
		bid.add(new LimitOrder(100001, Side.BUY, 600, 58.09, 0));
		
		ask.add(new LimitOrder(200001, Side.SELL, 800, 39.74, 0));
		ask.add(new LimitOrder(200002, Side.SELL, 500, 40.94, 0));
		List<Trade> matchResult = policyUnderTest.match();
		
		assertEquals(3, matchResult.size());
		assertEquals(800, matchResult.get(0).getQty());
		assertEquals(58.09, matchResult.get(0).getPrice(), PriceUtils.Epsilon);
		
		assertEquals(400, matchResult.get(1).getQty());
		assertEquals(58.09, matchResult.get(1).getPrice(), PriceUtils.Epsilon);

		assertEquals(100, matchResult.get(2).getQty());
		assertTrue(PriceUtils.isEqual(58.09, matchResult.get(2).getPrice()));

		assertEquals(1, bid.size());
		assertPriceLevel(bid, 0, 100001, 58.09, 500);
		
		assertEquals(0, ask.size());
	}
	

	
}
