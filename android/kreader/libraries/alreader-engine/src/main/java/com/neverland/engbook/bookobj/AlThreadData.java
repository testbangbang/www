package com.neverland.engbook.bookobj;


import com.neverland.engbook.forpublic.EngBookListener;

import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_NOTIFY_ID;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_NOTIFY_RESULT;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_THREAD_TASK;

import android.app.Activity;


class AlThreadData {
	private final static Object lock = new Object();
	
	private AlThread							id = null;
	public TAL_THREAD_TASK						task;
	public volatile AlBookEng					book_object;
	//public volatile WeakReference<EngBookListener> owner_window;
	public volatile EngBookListener				owner_window;
	private volatile boolean					is_work0 = false;
	private volatile boolean					is_work1 = false;

	public volatile AlBookOptions				param_void1;
	public volatile String						param_char1;
	
	public void clearAll() {
		synchronized (lock) { 
		clearWork0();
		clearWork1();
		param_char1 = null;
		param_void1 = null;
		id = null;		
		owner_window = null;
		}
	}
	
	public void setWork0() {
		synchronized (lock) { 
		is_work0 = true;
		}
	}
	
	public void clearWork0() {
		synchronized (lock) { 
		is_work0 = false;
		}
	}
	
	public boolean getWork0() {
		synchronized (lock) { 
		return is_work0;
		}
	}
	
	public void setWork1() {
		synchronized (lock) { 
		is_work1 = true;
		}
	}
	
	public void clearWork1() {
		synchronized (lock) { 
		is_work1 = false;
		}
	}
	
	public boolean getWork1() {
		synchronized (lock) { 
		return is_work1;
		}
	}
	
	public void sendNotifyForUIThread(final TAL_NOTIFY_ID id, final TAL_NOTIFY_RESULT result) {
		synchronized (lock) {
			if (owner_window == null)
				return;

			if (owner_window instanceof Activity) {
				((Activity) owner_window).runOnUiThread(new Runnable() {
					public void run() {
						owner_window.engBookGetMessage(id, result);
					}
				});
			} else {
				owner_window.engBookGetMessage(id, result);

                /*owner_window.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        owner_window.engBookGetMessage(id, result);
                    }
                });*/
			}
		}
	}
	
	public void freeOwner() {
		synchronized (lock) { 
		owner_window = null;
		}
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
