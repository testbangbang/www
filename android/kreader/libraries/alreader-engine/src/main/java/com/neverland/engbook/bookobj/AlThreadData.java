package com.neverland.engbook.bookobj;


import android.app.Activity;

import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.EngBookListener;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_NOTIFY_ID;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_NOTIFY_RESULT;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_THREAD_TASK;
import com.neverland.engbook.level1.AlFiles;


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

	public volatile boolean						_isObjOpen = false;
	
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

	public void setObjOpen() {
		synchronized (lock) {
			_isObjOpen = true;
		}
	}

	public void clearObjOpen() {
		synchronized (lock) {
			_isObjOpen = false;
		}
	}

	public boolean getObjOpen() {
		synchronized (lock) {
			return _isObjOpen;
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


	public static void startThread(AlThreadData param, TAL_THREAD_TASK task, boolean oneThread) {
		if (oneThread) {
			param.clearWork1();
			param.setWork0();
			param.task = task;
			param.realRun(param);
		} else {
			while (param.getWork0()) ;

			stopThread(param);

			param.clearWork1();
			param.setWork0();
			param.id = new AlThread(param, task);

			while (!param.getWork1()) ;
		}
	}
	
	public static void stopThread(AlThreadData param) {
		while (param.getWork0()) ;
		param.id = null;		
	}


	protected void realRun(AlThreadData param) {
		try {

			TAL_NOTIFY_RESULT res = TAL_NOTIFY_RESULT.OK;
			TAL_NOTIFY_ID id;

			this.setWork1();

			this.sendNotifyForUIThread(TAL_NOTIFY_ID.NEEDREDRAW, TAL_NOTIFY_RESULT.OK);
			this.sendNotifyForUIThread(TAL_NOTIFY_ID.STARTTHREAD, TAL_NOTIFY_RESULT.OK);

			id = TAL_NOTIFY_ID.NEEDREDRAW;
			switch (this.task) {
				case OPENBOOK_FULLAFTERPARTIAL:
					id = TAL_NOTIFY_ID.OPENBOOK_FULLAFTERPARTIAL;
					try {
						this.param_void1.formatOptions |= AlFiles.LEVEL1_BOOKOPTIONS_NEED_MULTIFILE_FULL;

						res = this.book_object.openBookInThread(this.param_char1, this.param_void1);

						/*if (res == TAL_NOTIFY_RESULT.OK) {
							synchronized (this.book_object) {
								this.book_object.replaceDelay2Real();
							}
						}*/

						this.param_void1.clearBlocked();

					} catch (Exception e) {
						e.printStackTrace();
						res = TAL_NOTIFY_RESULT.EXCEPT;
						this.book_object.closeBookReal();
					}
					break;
				case OPENBOOK:
					id = TAL_NOTIFY_ID.OPENBOOK;
					try {
						this.setObjOpen();
						this.param_void1.setBlocked();
						res = this.book_object.openBookInThread(this.param_char1, this.param_void1);
					} catch (Exception e) {
						this.clearObjOpen();
						e.printStackTrace();
						res = TAL_NOTIFY_RESULT.EXCEPT;
						this.book_object.closeBookReal();
					}
					break;
				case CREATEDEBUG:
					id = TAL_NOTIFY_ID.CREATEDEBUG;
					try {
						res = this.book_object.createDebugFileInThread(this.param_char1);
					} catch (Exception e) {
						e.printStackTrace();
						res = TAL_NOTIFY_RESULT.EXCEPT;
					}
					break;
				case FIND:
					id = TAL_NOTIFY_ID.FIND;
					try {
						res = this.book_object.findTextInThread(this.param_char1);
					} catch (Exception e) {
						e.printStackTrace();
						res = TAL_NOTIFY_RESULT.EXCEPT;
					}
					break;
				case NEWCALCPAGES:
					id = TAL_NOTIFY_ID.NEWCALCPAGES;
					try {
						res = this.book_object.calcPagesInThread();
					} catch (Exception e) {
						e.printStackTrace();
						res = TAL_NOTIFY_RESULT.EXCEPT;
					}
					break;
			}
			this.sendNotifyForUIThread(TAL_NOTIFY_ID.STOPTHREAD, TAL_NOTIFY_RESULT.OK);
			this.sendNotifyForUIThread(id, res);

			if (this.task == TAL_THREAD_TASK.OPENBOOK_FULLAFTERPARTIAL && res == TAL_NOTIFY_RESULT.OK) {
				this.book_object.openState.decState();
				this.book_object.openState.decState();
				this.book_object.openState.decState();
				this.book_object.openState.decState();

				this.clearWork0();
			} else
			if (this.task == TAL_THREAD_TASK.OPENBOOK && res == TAL_NOTIFY_RESULT.OK) {
				if (this.book_object != null &&
						this.book_object.format.isTextFormat &&
						this.book_object.format.multiFiles.modePart) {

					this.book_object.openState.incState();
					this.book_object.openState.incState();

					synchronized (this.book_object) {
						this.clearWork0();
						startThread(param, TAL_THREAD_TASK.OPENBOOK_FULLAFTERPARTIAL, false);
					}

				} else {
					this.clearWork0();
					this.param_void1.clearBlocked();
					this.clearObjOpen();
				}
			} else {
				this.clearWork0();
			}

			if (this.task == TAL_THREAD_TASK.OPENBOOK_FULLAFTERPARTIAL)
				this.clearObjOpen();

		} catch (Exception e) {
			e.printStackTrace();
			this.clearObjOpen();
			this.clearWork1();
			this.clearWork0();
		}
	}
}
