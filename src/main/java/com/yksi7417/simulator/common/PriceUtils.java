package com.yksi7417.simulator.common;

public class PriceUtils {
	public static double Epsilon = 1e-6; 
	public static boolean isEqual(double d1, double d2){
		return Math.abs(d1 - d2) < Epsilon;
	}
	public static boolean isGreaterThan(double d1, double d2){
		return d1 - d2 > Epsilon;
	}
	public static boolean isLessThan(double d1, double d2){
		return d2 - d1 > Epsilon;
	}
	public static boolean isEqualGreaterThan(double d1, double d2) {
		return isEqual(d1,d2) || isGreaterThan(d1,d2);		
	}
	public static boolean isEqualLessThan(double d1, double d2) {
		return isEqual(d1,d2) || isLessThan(d1,d2);		
	}
	
	public boolean isEqualOrBetter(Side side, double d1, double d2) {
		switch (side) {
		case BUY:
			return isEqualGreaterThan(d1, d2);
		case SELL:
			return isEqualLessThan(d1, d2);
		default:
			throw new RuntimeException("do not expect anything other than BUY/SELL order, please review design if you see this");
		}
	}
	
	
}
