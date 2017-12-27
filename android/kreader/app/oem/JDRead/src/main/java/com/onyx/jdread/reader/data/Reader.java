package com.onyx.jdread.reader.data;

import com.onyx.jdread.reader.common.DocumentInfo;

/**
 * Created by huxiaomao on 2017/12/20.
 */

public class Reader {
    private ReaderHelper readerHelper;
    private DocumentInfo documentInfo;
    private ReaderViewHelper readerViewHelper;
    private ReaderTouchHelper readerTouchHelper;

    public Reader(DocumentInfo documentInfo) {
        this.documentInfo = documentInfo;
        this.readerHelper = new ReaderHelper();
        this.readerViewHelper = new ReaderViewHelper();
        this.readerTouchHelper = new ReaderTouchHelper();
    }

    public ReaderHelper getReaderHelper() {
        return readerHelper;
    }

    public DocumentInfo getDocumentInfo() {
        return documentInfo;
    }

    public ReaderViewHelper getReaderViewHelper() {
        return readerViewHelper;
    }

    public ReaderTouchHelper getReaderTouchHelper() {
        return readerTouchHelper;
    }
}
