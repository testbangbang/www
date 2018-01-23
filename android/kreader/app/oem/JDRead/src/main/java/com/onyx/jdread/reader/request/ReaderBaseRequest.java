package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.rx.RxRequest;
import com.onyx.jdread.reader.common.ReaderUserDataInfo;
import com.onyx.jdread.reader.highlight.ReaderSelectionInfo;

import io.reactivex.Scheduler;

/**
 * Created by huxiaomao on 2017/12/26.
 */

public abstract class ReaderBaseRequest extends RxRequest {
    private ReaderViewInfo readerViewInfo;
    private ReaderUserDataInfo readerUserDataInfo;
    private ReaderSelectionInfo selectionInfoManager;
    public boolean isSuccess = true;

    public final ReaderUserDataInfo getReaderUserDataInfo() {
        if (readerUserDataInfo == null) {
            readerUserDataInfo = new ReaderUserDataInfo();
        }
        return readerUserDataInfo;
    }

    public ReaderViewInfo getReaderViewInfo() {
        if (readerViewInfo == null) {
            readerViewInfo = new ReaderViewInfo();
        }
        return readerViewInfo;
    }

    public ReaderSelectionInfo getSelectionInfoManager() {
        if(selectionInfoManager == null){
            selectionInfoManager = new ReaderSelectionInfo();
        }
        return selectionInfoManager;
    }

    @Override
    public Scheduler subscribeScheduler() {
        return ReaderSchedulers.readerScheduler();
    }
}
