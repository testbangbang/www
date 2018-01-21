package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.common.request.ExecutorContext;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.rx.RxRequest;
import com.onyx.jdread.reader.common.ReaderUserDataInfo;

import java.util.concurrent.ExecutorService;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by huxiaomao on 2017/12/26.
 */

public abstract class ReaderBaseRequest extends RxRequest {
    private ReaderViewInfo readerViewInfo;
    private ReaderUserDataInfo readerUserDataInfo;
    public boolean isSuccess = true;

    public final ReaderUserDataInfo getReaderUserDataInfo() {
        if (readerUserDataInfo == null) {
            readerUserDataInfo = new ReaderUserDataInfo();
        }
        return readerUserDataInfo;
    }

    public ReaderViewInfo getReaderViewInfo() {
        if(readerViewInfo == null) {
            readerViewInfo = new ReaderViewInfo();
        }
        return readerViewInfo;
    }

    @Override
    public Scheduler subscribeScheduler() {
        return ReaderSchedulers.readerScheduler();
    }
}
