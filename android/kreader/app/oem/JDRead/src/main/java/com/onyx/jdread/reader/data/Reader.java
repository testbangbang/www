package com.onyx.jdread.reader.data;

import android.content.Context;

import com.onyx.jdread.reader.common.DocumentInfo;
import com.onyx.jdread.reader.epd.ReaderEpdHelper;
import com.onyx.jdread.reader.highlight.ReaderSelectionHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

/**
 * Created by huxiaomao on 2017/12/20.
 */

public class Reader {
    private ReaderHelper readerHelper;
    private ReaderViewHelper readerViewHelper;
    private ReaderSelectionHelper readerSelectionHelper;
    private ReaderEpdHelper readerEpdHelper;
    private long readingTime = 0;

    public Reader(DocumentInfo documentInfo,Context context) {
        this.readerHelper = new ReaderHelper(context,documentInfo);
        this.readerViewHelper = new ReaderViewHelper(context);
        this.readerEpdHelper = new ReaderEpdHelper(context);
    }

    public long getReadingTime() {
        return readingTime;
    }

    public void updateReadingTime() {
        long currentTime = (new Date()).getTime();
        this.readingTime += currentTime - readingTime;
    }

    public ReaderHelper getReaderHelper() {
        return readerHelper;
    }

    public DocumentInfo getDocumentInfo() {
        return readerHelper.getDocumentInfo();
    }

    public ReaderViewHelper getReaderViewHelper() {
        return readerViewHelper;
    }

    public ReaderSelectionHelper getReaderSelectionHelper() {
        if (readerSelectionHelper == null) {
            readerSelectionHelper = new ReaderSelectionHelper();
        }
        return readerSelectionHelper;
    }

    public ReaderEpdHelper getReaderEpdHelper() {
        return readerEpdHelper;
    }
}
