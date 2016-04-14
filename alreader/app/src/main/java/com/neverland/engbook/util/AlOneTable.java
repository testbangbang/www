package com.neverland.engbook.util;

public class AlOneTable {
	public int			num = -1;
	public int			positionS = 0;
	public int			positionE = 0;
	
	public static AlOneTable add(int num, int posS, int posE) {
		AlOneTable a = new AlOneTable();
		a.num = num;
		a.positionS = posS;
		a.positionE = posE;	
		return a;
	}
	
	@Override
	public String toString() {
		return Integer.toString(num) + '/' + positionS + '/' + positionE;		
	}

}
