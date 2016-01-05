package com.neverland.engbook.bookobj;


import com.neverland.engbook.forpublic.EngBookListener;

import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_NOTIFY_ID;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_NOTIFY_RESULT;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_THREAD_TASK;

import android.app.Activity;


class AlThreadData {
	private AlThread					id = null;
	public TAL_THREAD_TASK				task;
	public AlBookEng					book_object;
	public EngBookListener				owner_window;
	private boolean					is_work0 = false;
	private boolean					is_work1 = false;

	public AlBookOptions				param_void1;
	public String						param_char1;
	
	public synchronized void clearAll() {
		clearWork0();
		clearWork1();
		param_char1 = null;
		param_void1 = null;
		id = null;		
		owner_window = null;
	}
	
	public synchronized void setWork0() {
		is_work0 = true;
	}
	
	public synchronized void clearWork0() {
		is_work0 = false;
	}
	
	public synchronized boolean getWork0() {
		return is_work0;
	}
	
	public synchronized void setWork1() {
		is_work1 = true;
	}
	
	public synchronized void clearWork1() {
		is_work1 = false;
	}
	
	public synchronized boolean getWork1() {
		return is_work1;
	}
	
	public synchronized void sendNotifyForUIThread(final TAL_NOTIFY_ID id, final TAL_NOTIFY_RESULT result) {
		if (owner_window == null)
			return;
		
		((Activity)owner_window).runOnUiThread(new Runnable(){
		     public void run() {
		    	 owner_window.engBookGetMessage(id, result);
		     }
		});		
	}
	
	public static void startThread(AlThreadData param, TAL_THREAD_TASK task) {
		while (param.getWork0()) ;			

		stopThread(param);

		param.clearWork1();		

		param.setWork0();
		param.id = new AlThread(param, task);

		while (!param.getWork1()) ;
	}
	
	public static void stopThread(AlThreadData param) {
		while (param.getWork0()) ;
		param.id = null;		
	}
}
