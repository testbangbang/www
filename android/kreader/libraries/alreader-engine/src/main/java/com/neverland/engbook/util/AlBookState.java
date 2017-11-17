package com.neverland.engbook.util;

public class AlBookState {
	private final static Object lock = new Object();
	
	public static final int NOLOAD			= 0;
	public static final int LOAD 			= 1;
	public static final int CALC			= 2;
	public static final int OPEN			= 3;	
	public static final int PROCESS0			= 4;
	
	private volatile int state 				= NOLOAD;
	
	public final int getState() {
		synchronized (lock) { 
			return state;
		}
	}
	
	public final synchronized void clearState() {
		synchronized (lock) { 
			state = NOLOAD;
		}
	}
	
	public final synchronized void incState() {
		synchronized (lock) { 
			state++;
		}
	}
	
	public final synchronized void decState() {
		synchronized (lock) { 
			state--;
		}
	}
}
