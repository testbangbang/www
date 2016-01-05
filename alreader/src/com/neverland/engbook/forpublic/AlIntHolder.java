package com.neverland.engbook.forpublic;

public class AlIntHolder {
	public volatile int value = 0;
	
	private AlIntHolder() {
		
	}
	
	public AlIntHolder(int v) {
		value = v;
	}
	
	@Override
	public String toString() {
		return Integer.toString(value);
	}
}
