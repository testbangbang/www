package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.rx.RxRequest;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by huxiaomao on 2017/12/26.
 */

public abstract class ReaderBaseRequest extends RxRequest {
    @Override
    public Scheduler subscribeScheduler() {
        return Schedulers.io();
    }
}
