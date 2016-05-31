package com.onyx.kreader.common;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by zhuzeng on 5/31/16.
 */
public class RequestManager {

    private static final String TAG = RequestManager.class.getSimpleName();
    private PowerManager.WakeLock wakeLock;
    private int wakeLockCounting = 0;
    private ExecutorService singleThreadPool = null;
    private boolean debugWakelock = false;
    private List<BaseReaderRequest> requestList;
    private Handler handler = new Handler(Looper.getMainLooper());

    public RequestManager() {
        initRequestList();
    }

    private void initRequestList() {
        requestList = Collections.synchronizedList(new ArrayList<BaseReaderRequest>());
    }

    public void removeRequest(final BaseReaderRequest request) {
        synchronized (requestList) {
            requestList.remove(request);
        }
    }

    public void addRequest(final BaseReaderRequest request) {
        synchronized (requestList) {
            requestList.add(request);
        }
    }

    public void abortAllRequests() {
        synchronized (requestList) {
            for(BaseReaderRequest request : requestList) {
                request.setAbort();
            }
        }
    }

    public void acquireWakeLock(Context context) {
        try {
            if (wakeLock == null) {
                PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
                wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Reader: ");
            }
            wakeLock.acquire();
            ++wakeLockCounting;
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }

    public void releaseWakeLock() {
        try {
            if (wakeLock != null) {
                if (wakeLock.isHeld()) {
                    wakeLock.release();
                }
                if (--wakeLockCounting <= 0) {
                    wakeLock = null;
                }
            }
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }

    public void dumpWakelocks() {
        if (debugWakelock) {
            if (wakeLock != null || wakeLockCounting > 0) {
                Log.w(TAG, "wake lock not released. check wake lock." + wakeLock.toString() + " counting: " + wakeLockCounting);
            }
        }
    }

    public ExecutorService getSingleThreadPool()   {
        if (singleThreadPool == null) {
            singleThreadPool = Executors.newSingleThreadExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setPriority(Thread.MAX_PRIORITY);
                    return t;
                }
            });
        }
        return singleThreadPool;
    }

    public Handler getLooperHandler() {
        return handler;
    }

}
