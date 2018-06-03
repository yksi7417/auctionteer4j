package com.yksi7417.simulator.limitorderbook;

import java.util.Comparator;

import com.yksi7417.simulator.common.ILimitOrder;
import com.yksi7417.simulator.common.PriceUtils;

class PriceTimeComparator implements Comparator<ILimitOrder> {
	final Comparator<ILimitOrder> priceComparator;
	public PriceTimeComparator(Comparator<ILimitOrder> priceComparator) {
		this.priceComparator = priceComparator; 
	}
	@Override
	public int compare(ILimitOrder o1, ILimitOrder o2) {
		if (PriceUtils.isEqual(o1.getPrice() , o2.getPrice()))
			return Long.compare(o1.getTimestamp(), o2.getTimestamp());
		else 
			return this.priceComparator.compare(o1,o2);
	}
}

