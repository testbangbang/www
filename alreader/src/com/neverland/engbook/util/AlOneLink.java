package com.neverland.engbook.util;

public class AlOneLink {
	public String				name = null;
	public int			positionS = 0;
	public int			positionE = -1;
	public int			iType = 0;
	public int			counter = -1;

	public static AlOneLink add(String name, int posS, int type) {
		AlOneLink a = new AlOneLink();
		a.name = name;
		a.positionS = posS;
		a.iType = type;
		return a;
	}	

	@Override
	public String toString() {
		return name + '/' + positionS + '/' + positionE + '/' + iType;		
	}
}
