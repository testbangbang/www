package com.onyx.jdread.reader.catalog.event;

import com.onyx.jdread.reader.common.ReaderUserDataInfo;

/**
 * Created by huxiaomao on 2018/1/10.
 */

public class GetDocumentInfoResultEvent {
    private ReaderUserDataInfo readerUserDataInfo;

    public GetDocumentInfoResultEvent(ReaderUserDataInfo readerUserDataInfo) {
        this.readerUserDataInfo = readerUserDataInfo;
    }

    public ReaderUserDataInfo getReaderUserDataInfo() {
        return readerUserDataInfo;
    }
}
