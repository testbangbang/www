package com.neverland.engbook.bookobj;

import com.neverland.engbook.forpublic.EngBookMyType.TAL_THREAD_TASK;

class AlThread  implements Runnable {
	
	private volatile AlThreadData param;	

	public AlThread(AlThreadData p, TAL_THREAD_TASK t) {
		param = p;
		param.task = t;
		new Thread(this).start();
	}

    public void run() {
		param.realRun(param);
    }
}
