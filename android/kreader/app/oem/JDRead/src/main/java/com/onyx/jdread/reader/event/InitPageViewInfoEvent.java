package com.onyx.jdread.reader.event;

import com.onyx.android.sdk.reader.common.ReaderViewInfo;

/**
 * Created by huxiaomao on 2018/1/8.
 */

public class InitPageViewInfoEvent {
    private ReaderViewInfo readerViewInfo;

    public InitPageViewInfoEvent(ReaderViewInfo readerViewInfo) {
        this.readerViewInfo = readerViewInfo;
    }

    public ReaderViewInfo getReaderViewInfo() {
        return readerViewInfo;
    }
}
