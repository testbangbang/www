package com.neverland.engbook.forpublic;

/**
 * класс-холдер для передачи инта "по ссылке"
 */
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
