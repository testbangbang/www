package com.onyx.pinyinime;

import android.content.Context;

/**
 * Created by suicheng on 2018/1/30.
 */
public class CrashExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static CrashExceptionHandler INSTANCE;

    private Context appContext;
    private Thread.UncaughtExceptionHandler defaultHandler;

    private CrashExceptionHandler(Context appContext) {
        this.appContext = appContext;
        setDefaultUncaughtExceptionHandler();
    }

    public static CrashExceptionHandler getInstance(Context appContext) {
        if (INSTANCE == null) {
            synchronized (CrashExceptionHandler.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CrashExceptionHandler(appContext);
                }
            }
        }
        return INSTANCE;
    }

    private void setDefaultUncaughtExceptionHandler() {
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable e) {
        handleException(e);
        defaultHandler.uncaughtException(thread, e);
    }

    private void handleException(Throwable e) {
        if (e == null) {
            return;
        }
        submitFeedback(e);
    }

    private void submitFeedback(Throwable e) {
        BroadcastHelper.sendFeedbackBroadcast(appContext, e);
    }
}
