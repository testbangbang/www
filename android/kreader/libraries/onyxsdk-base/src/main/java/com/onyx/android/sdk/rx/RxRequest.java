package com.onyx.android.sdk.rx;

import android.content.Context;

import com.onyx.android.sdk.common.request.WakeLockHolder;
import com.onyx.android.sdk.utils.Benchmark;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by john on 29/10/2017.
 */

public abstract class RxRequest<T extends RxRequest> implements Callable<T> {

    private static final String TAG = RxRequest.class.getSimpleName();

    private Benchmark benchmark;
    private WakeLockHolder wakeLockHolder;
    private boolean enableWakeLock = false;
    private AtomicBoolean abort = new AtomicBoolean(false);

    private static boolean enableBenchmarkDebug = false;
    private static Context appContext;

    public Observable createObservable() {
        return Observable.fromCallable(this);
    }

    public void beforeExecute() {}

    public void afterExecute() {}

    public void doFinally() throws Exception {
        afterExecute();
        benchmarkEnd();
        releaseWakeLock();
    }

    public boolean getAbort() {
        return abort.get();
    }

    public void setAbort(boolean abort) {
        this.abort.set(abort);
    }

    public Scheduler observeScheduler() {
        return AndroidSchedulers.mainThread();
    }

    public Scheduler subscribeScheduler() {
        return Schedulers.newThread();
    }

    public void benchmarkStart() {
        if (!isEnableBenchmarkDebug()) {
            return;
        }
        benchmark = new Benchmark();
    }

    public long benchmarkEnd() {
        if (!isEnableBenchmarkDebug() || benchmark == null) {
            return 0;
        }
        return (benchmark.duration());
    }

    public static boolean isEnableBenchmarkDebug() {
        return enableBenchmarkDebug;
    }

    public static void setAppContext(Context appContext) {
        RxRequest.appContext = appContext.getApplicationContext();
    }

    public Context getAppContext() {
        return appContext;
    }

    public void acquireWakeLock() {
        if (enableWakeLock) {
            getWakeLockHolder().acquireWakeLock(getAppContext(), getClass().getSimpleName());
        }
    }

    public void releaseWakeLock() {
        if (enableWakeLock) {
            getWakeLockHolder().releaseWakeLock();
        }
    }

    public WakeLockHolder getWakeLockHolder() {
        if (wakeLockHolder == null) {
            wakeLockHolder = new WakeLockHolder();
        }
        return wakeLockHolder;
    }

    public void setEnableWakeLock(boolean enableWakeLock) {
        this.enableWakeLock = enableWakeLock;
    }

    public void execute(final RxCallback<T> callback) {
        acquireWakeLock();
        benchmarkStart();
        beforeExecute();
        createObservable()
                .observeOn(observeScheduler())
                .subscribeOn(subscribeScheduler())
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        doFinally();
                    }})
                .subscribe(new Consumer<T>() {
                    @Override
                    public void accept(T t) throws Exception {
                        if (callback != null) {
                            callback.onNext(t);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (callback != null) {
                            callback.onError(throwable);
                        }
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        if (callback != null) {
                            callback.onComplete();
                        }
                    }
                });
    }
}
