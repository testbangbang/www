package com.onyx.android.sdk.common.request;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by zhuzeng on 10/7/16.
 */

public class SingleThreadExecutor {

    private ExecutorService provider;
    private int threadPriority;

    public SingleThreadExecutor(int threadPriority) {
        this.threadPriority = threadPriority;
    }

    public synchronized ExecutorService get()   {
        if (provider == null) {
            provider = Executors.newSingleThreadExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setPriority(threadPriority);
                    return t;
                }
            });
        }
        return provider;
    }

    public synchronized void shutdown() {
        if (provider == null) {
            return;
        }
        provider.shutdown();
    }

}
