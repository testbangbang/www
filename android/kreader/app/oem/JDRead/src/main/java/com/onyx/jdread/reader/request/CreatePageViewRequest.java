package com.onyx.jdread.reader.request;

import android.graphics.Bitmap;
import android.graphics.RectF;

import com.onyx.android.sdk.rx.RxRequest;
import com.onyx.jdread.reader.data.ReaderDataHolder;

/**
 * Created by huxiaomao on 2017/12/22.
 */

public class CreatePageViewRequest extends RxRequest {
    private ReaderDataHolder readerDataHolder;

    public CreatePageViewRequest(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    public CreatePageViewRequest call() throws Exception {
        int width = readerDataHolder.getReaderViewHelper().getPageViewWidth();
        int height = readerDataHolder.getReaderViewHelper().getPageViewHeight();
        readerDataHolder.getReader().getReaderHelper().updateViewportSize(width, height);
        RectF displayRect = new RectF(0, 0, width, height);
        RectF pageRect = new RectF(displayRect);
        RectF visibleRect = new RectF(pageRect);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        readerDataHolder.getReader().getReaderHelper().getRenderer().draw("0", 0, 0, displayRect, pageRect, visibleRect, bitmap);
        readerDataHolder.getReaderViewHelper().draw(bitmap);
        return this;
    }
}
