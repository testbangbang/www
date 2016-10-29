package com.neverland.engbook.util;

public class InternalFunc {
	
	public static Integer str2int(String s, int base) {
		try {
			Integer res = Integer.parseInt(s, base);
			return res;
		} catch (NumberFormatException e) {
			
		}
		return null;
	}
	
	public static Integer str2int(StringBuilder s, int base) {
		return str2int(s.toString(), base);
	}
}
