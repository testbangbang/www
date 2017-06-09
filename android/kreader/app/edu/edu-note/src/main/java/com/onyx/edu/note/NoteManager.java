package com.onyx.edu.note;

import com.onyx.android.sdk.common.request.RequestManager;

/**
 * Created by solskjaer49 on 2017/2/11 18:44.
 */

public class NoteManager {
    public RequestManager getRequestManager() {
        return requestManager;
    }

    private RequestManager requestManager;
    private static NoteManager instance;

    private NoteManager() {
        requestManager = new RequestManager(Thread.NORM_PRIORITY);
    }

    static public NoteManager sharedInstance() {
        if (instance == null) {
            instance = new NoteManager();
        }
        return instance;
    }
//
//    private Runnable generateRunnable(final BaseNoteRequest request) {
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    request.beforeExecute(NoteManager.this);
//                    request.execute(NoteManager.this);
//                } catch (Throwable tr) {
//                    request.setException(tr);
//                } finally {
//                    request.afterExecute(SettingManager.this);
//                    requestManager.dumpWakelocks();
//                    requestManager.removeRequest(request);
//                }
//            }
//        };
//        return runnable;
//    }
//
//    public boolean submitRequest(final Context context, final BaseSettingRequest request, final BaseCallback callback) {
//        return requestManager.submitRequestToMultiThreadPool(context, request, generateRunnable(request), callback);
//    }
}
