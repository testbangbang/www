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
		param.realRun();
        /*try {

        	TAL_NOTIFY_RESULT	res = TAL_NOTIFY_RESULT.OK;
	    	TAL_NOTIFY_ID		id;

	    	param.setWork1();
	    	
	    	param.sendNotifyForUIThread(TAL_NOTIFY_ID.NEEDREDRAW, TAL_NOTIFY_RESULT.OK);
        	param.sendNotifyForUIThread(TAL_NOTIFY_ID.STARTTHREAD, TAL_NOTIFY_RESULT.OK);
        	
        	id = TAL_NOTIFY_ID.NEEDREDRAW;
	    	switch (param.task) {
	    	case OPENBOOK:
	    		id = TAL_NOTIFY_ID.OPENBOOK;
				try {
					res = param.book_object.openBookInThread(param.param_char1, param.param_void1);
				} catch (Exception e) {
					e.printStackTrace();
					res = TAL_NOTIFY_RESULT.EXCEPT;
				}
	    		break;
	    	*//*case CLOSEBOOK:
	    		id = TAL_NOTIFY_ID.CLOSEBOOK;
	    		res = param.book_object.closeBookInThread();
	    		break;*//*
	    	case CREATEDEBUG:
	    		id = TAL_NOTIFY_ID.CREATEDEBUG;
				try {
	    			res = param.book_object.createDebugFileInThread(param.param_char1);
				} catch (Exception e) {
					e.printStackTrace();
					res = TAL_NOTIFY_RESULT.EXCEPT;
				}
	    		break;
	    	case FIND:
	    		id = TAL_NOTIFY_ID.FIND;
				try {
	    			res = param.book_object.findTextInThread(param.param_char1);
				} catch (Exception e) {
					e.printStackTrace();
					res = TAL_NOTIFY_RESULT.EXCEPT;
				}
	    		break;
	    	case NEWCALCPAGES:
	    		id = TAL_NOTIFY_ID.NEWCALCPAGES;
				try {
	    			res = param.book_object.calcPagesInThread();
				} catch (Exception e) {
					e.printStackTrace();
					res = TAL_NOTIFY_RESULT.EXCEPT;
				}
	    		break;
	    	}
        	param.sendNotifyForUIThread(TAL_NOTIFY_ID.STOPTHREAD, TAL_NOTIFY_RESULT.OK);
	    	param.clearWork0();
	    	param.sendNotifyForUIThread(id, res);
        
        } catch(Exception e) {
            e.printStackTrace();
        }*/
    }	
}
