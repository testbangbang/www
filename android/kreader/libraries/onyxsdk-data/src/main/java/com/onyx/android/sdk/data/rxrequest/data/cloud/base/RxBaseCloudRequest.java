package com.onyx.android.sdk.data.rxrequest.data.cloud.base;

import com.onyx.android.sdk.common.request.ExecutorContext;
import com.onyx.android.sdk.rx.RxRequest;

import java.util.concurrent.ExecutorService;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by jackdeng on 2017/11/7.
 */

public abstract class RxBaseCloudRequest extends RxRequest {
    protected final String TAG = this.getClass().getSimpleName();
    static final ExecutorService executorService = new ExecutorContext().getMultiThreadPoolWithFiveCorePoolSize();

    public RxBaseCloudRequest() {

    }

    @Override
    public Scheduler subscribeScheduler() {
        return Schedulers.from(executorService);

    }
}