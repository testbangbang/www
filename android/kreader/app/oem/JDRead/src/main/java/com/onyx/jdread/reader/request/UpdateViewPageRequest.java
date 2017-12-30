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
