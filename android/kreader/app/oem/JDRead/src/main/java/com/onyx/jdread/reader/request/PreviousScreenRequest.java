package com.onyx.jdread.reader.request;

import android.graphics.Bitmap;
import android.graphics.RectF;

import com.onyx.jdread.reader.data.ReaderDataHolder;

/**
 * Created by huxiaomao on 2017/12/27.
 */

public class PreviousScreenRequest extends ReaderBaseRequest {
    private ReaderDataHolder readerDataHolder;

    public PreviousScreenRequest(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    public PreviousScreenRequest call() throws Exception {
        readerDataHolder.getReader().getReaderHelper().previousScreen();
        return this;
    }
}
