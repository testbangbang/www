package com.onyx.jdread.reader.request;

import android.graphics.Bitmap;
import android.graphics.RectF;

import com.onyx.jdread.reader.data.ReaderDataHolder;

/**
 * Created by huxiaomao on 2017/12/27.
 */

public class NextScreenRequest extends ReaderBaseRequest {
    private ReaderDataHolder readerDataHolder;

    public NextScreenRequest(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    public NextScreenRequest call() throws Exception {
        readerDataHolder.getReader().getReaderHelper().nextScreen();
        int width = readerDataHolder.getReaderViewHelper().getPageViewWidth();
        int height = readerDataHolder.getReaderViewHelper().getPageViewHeight();
        RectF displayRect = new RectF(0, 0, width, height);
        RectF pageRect = new RectF(displayRect);
        RectF visibleRect = new RectF(pageRect);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        String position = readerDataHolder.getReader().getReaderHelper().getReaderLayoutManager().getCurrentPagePosition();
        readerDataHolder.getReader().getReaderHelper().getRenderer().draw(position, 0, 0, displayRect, pageRect, visibleRect, bitmap);
        readerDataHolder.getReaderViewHelper().draw(bitmap);
        return this;
    }
}
