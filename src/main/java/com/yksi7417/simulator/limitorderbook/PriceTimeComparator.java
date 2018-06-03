package com.yksi7417.simulator.limitorderbook;

import java.util.Comparator;

import com.yksi7417.simulator.common.LimitOrder;
import com.yksi7417.simulator.common.PriceUtils;

class PriceTimeComparator implements Comparator<LimitOrder> {
	final Comparator<LimitOrder> priceComparator;
	public PriceTimeComparator(Comparator<LimitOrder> priceComparator) {
		this.priceComparator = priceComparator; 
	}
	@Override
	public int compare(LimitOrder o1, LimitOrder o2) {
		if (PriceUtils.isEqual(o1.getPrice() , o2.getPrice()))
			return Long.compare(o1.getTimestamp(), o2.getTimestamp());
		else 
			return this.priceComparator.compare(o1,o2);
	}
}

