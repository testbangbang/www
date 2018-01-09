package com.onyx.jdread.reader.event;

import com.onyx.android.sdk.reader.common.ReaderViewInfo;

/**
 * Created by huxiaomao on 2018/1/8.
 */

public class UpdateReaderViewInfoEvent {
    private ReaderViewInfo readerViewInfo;

    public ReaderViewInfo getReaderViewInfo() {
        return readerViewInfo;
    }

    public void setReaderViewInfo(ReaderViewInfo readerViewInfo) {
        this.readerViewInfo = readerViewInfo;
    }
}
