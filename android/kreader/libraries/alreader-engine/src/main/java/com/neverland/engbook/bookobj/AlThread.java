package com.neverland.engbook.bookobj;

import com.neverland.engbook.forpublic.EngBookMyType.TAL_THREAD_TASK;
import com.onyx.android.sdk.common.request.WakeLockHolder;

class AlThread  implements Runnable {
	
	private volatile AlThreadData param;	

	public AlThread(AlThreadData p, TAL_THREAD_TASK t) {
		param = p;
		param.task = t;
		new Thread(this).start();
	}

    public void run() {
		WakeLockHolder wakeLockHolder = new WakeLockHolder();
	    wakeLockHolder.acquireWakeLock(param.book_object.engOptions.appInstance, getClass().getSimpleName());
	    try {
			param.realRun(param);
		} finally {
	    	wakeLockHolder.releaseWakeLock();
		}
	}
}
