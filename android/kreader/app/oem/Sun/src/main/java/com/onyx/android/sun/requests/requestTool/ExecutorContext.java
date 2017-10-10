package com.onyx.android.sun.requests.requestTool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by zhuzeng on 7/22/16.
 */
public class ExecutorContext {

    private ExecutorService singleThreadPool = null;
    private ExecutorService multiThreadPool = null;
    private List<BaseRequest> requestList;
    private int threadPriority = Thread.MAX_PRIORITY;

    public ExecutorContext() {
        initRequestList();
    }

    public ExecutorContext(int priority) {
        threadPriority = priority;
        initRequestList();
    }

    private void initRequestList() {
        requestList = Collections.synchronizedList(new ArrayList<BaseRequest>());
    }

    public void removeRequest(final BaseRequest request) {
        synchronized (requestList) {
            requestList.remove(request);
        }
    }

    public void addRequest(final BaseRequest request) {
        synchronized (requestList) {
            requestList.add(request);
        }
    }

    public void abortAllRequests() {
        synchronized (requestList) {
            for(BaseRequest request : requestList) {
                request.setAbort();
            }
        }
    }

    public boolean hasPendingRequests() {
        synchronized (requestList) {
            return requestList.size() > 0;
        }
    }

    public ExecutorService getSingleThreadPool()   {
        if (singleThreadPool == null) {
            singleThreadPool = Executors.newSingleThreadExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setPriority(threadPriority);
                    return t;
                }
            });
        }
        return singleThreadPool;
    }

    public void submitToSingleThreadPool(final Runnable runnable) {
        getSingleThreadPool().submit(runnable);
    }

    public void submitToMultiThreadPool(final Runnable runnable) {
        getMultiThreadPool().submit(runnable);
    }

    public ExecutorService getMultiThreadPool() {
        if (multiThreadPool == null) {
            multiThreadPool = Executors.newScheduledThreadPool(3, new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setPriority(threadPriority);
                    return t;
                }
            });
        }
        return multiThreadPool;
    }
}
