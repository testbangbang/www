package com.neverland.engbook.util;

public class InternalFunc {
	
	public static int str2int(String s, int base) {
		try {
			return Integer.parseInt(s, base);
		} catch (NumberFormatException e) {
			
		}
		return -1;
	}
	
	public static int str2int(StringBuilder s, int base) {
		return str2int(s.toString(), base);
	}
}
