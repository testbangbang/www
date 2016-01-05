package com.neverland.engbook.util;

public class AlBookState {
	public static final int NOLOAD			= 0;
	public static final int LOAD 			= 1;
	public static final int CALC			= 2;
	public static final int OPEN			= 3;	
	public static final int PROCESS			= 4;			
	
	private volatile int state 				= NOLOAD;
	
	public final synchronized int getState() {
		return state;
	}
	
	public final synchronized void clearState() {
		state = NOLOAD;
	}
	
	public final synchronized void incState() {
		state++;
	}
	
	public final synchronized void decState() {
		state--;
	}
}
