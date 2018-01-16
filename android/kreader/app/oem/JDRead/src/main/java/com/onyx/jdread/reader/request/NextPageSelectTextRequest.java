package com.onyx.jdread.reader.request;

import android.graphics.PointF;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.highlight.HitTestTextHelper;
import com.onyx.jdread.reader.menu.common.ReaderConfig;

/**
 * Created by huxiaomao on 2018/1/15.
 */

public class NextPageSelectTextRequest extends ReaderBaseRequest {
    private ReaderDataHolder readerDataHolder;
    private ReaderTextStyle style;
    private float x;
    private float y;

    public NextPageSelectTextRequest(ReaderDataHolder readerDataHolder, ReaderTextStyle style, float x, float y) {
        this.readerDataHolder = readerDataHolder;
        this.style = style;
        this.x = x;
        this.y = y;
    }

    @Override
    public NextPageSelectTextRequest call() throws Exception {
        readerDataHolder.getReader().getReaderHelper().getReaderLayoutManager().nextScreen();

        PointF start = new PointF(style.getPageMargin().getLeftMargin().getPercent(), style.getPageMargin().getTopMargin().getPercent());
        PointF end = new PointF(x, y);

        ReaderSelection selection = HitTestTextHelper.hitTestTextRegion(start, end, ReaderConfig.HIT_TEST_TEXT_STEP, readerDataHolder, getReaderUserDataInfo(), true);
        if (selection == null || selection.getRectangles().size() <= 0) {
            readerDataHolder.getReader().getReaderHelper().getReaderLayoutManager().prevScreen();
        } else {
            readerDataHolder.getReaderViewHelper().updatePageView(readerDataHolder, getReaderUserDataInfo(), getReaderViewInfo());
        }
        return this;
    }
}
