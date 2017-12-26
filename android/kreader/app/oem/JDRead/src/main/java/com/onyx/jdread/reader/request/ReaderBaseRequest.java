package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.rx.RxRequest;

import java.util.concurrent.ExecutorService;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by huxiaomao on 2017/12/26.
 */

public abstract class ReaderBaseRequest extends RxRequest {

    private RequestManager requestManager;

    public ReaderBaseRequest(RequestManager requestManager) {
        this.requestManager = requestManager;
    }

    @Override
    public Scheduler subscribeScheduler() {
        final ExecutorService executorService = requestManager.getExecutorByIdentifier("reader").getSingleThreadPool();
        return Schedulers.from(executorService);
    }
}
