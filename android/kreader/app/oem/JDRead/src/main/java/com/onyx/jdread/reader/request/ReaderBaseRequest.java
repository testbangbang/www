package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.rx.RxRequest;
import com.onyx.jdread.reader.common.ReaderUserDataInfo;
import com.onyx.jdread.reader.highlight.SelectionInfoManager;

import io.reactivex.Scheduler;

/**
 * Created by huxiaomao on 2017/12/26.
 */

public abstract class ReaderBaseRequest extends RxRequest {
    private ReaderViewInfo readerViewInfo;
    private ReaderUserDataInfo readerUserDataInfo;
    private SelectionInfoManager selectionInfoManager;
    public boolean isSuccess = true;

    public ReaderViewInfo createReaderViewInfo() {
        readerViewInfo = new ReaderViewInfo();
        return readerViewInfo;
    }

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

    public SelectionInfoManager getSelectionInfoManager() {
        if(selectionInfoManager == null){
            selectionInfoManager = new SelectionInfoManager();
        }
        return selectionInfoManager;
    }

    @Override
    public Scheduler subscribeScheduler() {
        return ReaderSchedulers.readerScheduler();
    }
}
