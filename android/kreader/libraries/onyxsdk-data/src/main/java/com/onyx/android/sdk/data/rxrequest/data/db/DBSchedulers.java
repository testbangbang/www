package com.onyx.android.sdk.data.rxrequest.data.db;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhuzeng on 28/01/2018.
 */

public class DBSchedulers {

    private static Scheduler dbScheduler;

    public static Scheduler dbScheduler() {
        if (dbScheduler == null) {
            ExecutorService singleThreadPool = Executors.newSingleThreadExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setPriority(Thread.MAX_PRIORITY);
                    return t;
                }
            });
            dbScheduler = Schedulers.from(singleThreadPool);
        }
        return dbScheduler;
    }

}
