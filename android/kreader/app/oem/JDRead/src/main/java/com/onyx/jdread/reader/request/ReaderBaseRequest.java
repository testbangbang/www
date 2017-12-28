package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.common.request.ExecutorContext;
import com.onyx.android.sdk.rx.RxRequest;

import java.util.concurrent.ExecutorService;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by huxiaomao on 2017/12/26.
 */

public abstract class ReaderBaseRequest extends RxRequest {
    static final ExecutorService executorService = new ExecutorContext().getSingleThreadPool();

    @Override
    public Scheduler subscribeScheduler() {
        return Schedulers.from(executorService);
    }
}
