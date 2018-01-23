package com.onyx.jdread.reader.request;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by john on 21/1/2018.
 */

public class ReaderSchedulers {
    private static Scheduler readerScheduler;

    public static Scheduler readerScheduler() {
        if (readerScheduler == null) {
            ExecutorService singleThreadPool = Executors.newSingleThreadExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setPriority(Thread.MAX_PRIORITY);
                    return t;
                }
            });
            readerScheduler = Schedulers.from(singleThreadPool);
        }
        return readerScheduler;
    }
}
