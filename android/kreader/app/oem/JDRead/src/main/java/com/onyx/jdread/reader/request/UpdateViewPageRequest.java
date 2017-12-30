package com.onyx.jdread.reader.request;

import android.graphics.Bitmap;
import android.graphics.RectF;

import com.onyx.jdread.reader.data.ReaderDataHolder;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class UpdateViewPageRequest extends ReaderBaseRequest {
    private ReaderDataHolder readerDataHolder;

    public UpdateViewPageRequest(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    public UpdateViewPageRequest call() throws Exception {
        updatePageView();
        return this;
    }

    public void updatePageView(){
        readerDataHolder.getReaderViewHelper().updatePageView(readerDataHolder);
    }
}