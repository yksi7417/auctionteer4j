package com.yksi7417.simulator.limitorderbook.matchpolicy;

import java.util.List;

import com.yksi7417.simulator.common.Trade;

public interface IMatchPolicy {
	public List<Trade> match();
}
