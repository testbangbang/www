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

public class PreviousPageSelectTextRequest extends ReaderBaseRequest {
    private ReaderDataHolder readerDataHolder;
    private ReaderTextStyle style;
    private float x;
    private float y;

    public PreviousPageSelectTextRequest(ReaderDataHolder readerDataHolder, ReaderTextStyle style, float x, float y) {
        this.readerDataHolder = readerDataHolder;
        this.style = style;
        this.x = x;
        this.y = y;
    }

    @Override
    public PreviousPageSelectTextRequest call() throws Exception {
        readerDataHolder.getReader().getReaderHelper().getReaderLayoutManager().prevScreen();

        PointF start = new PointF(x, y);
        PointF end = new PointF(style.getPageMargin().getLeftMargin().getPercent(), style.getPageMargin().getTopMargin().getPercent());

        ReaderSelection selection = HitTestTextHelper.hitTestTextRegion(start, end, -ReaderConfig.HIT_TEST_TEXT_STEP, readerDataHolder, getReaderUserDataInfo(), false);
        if (selection == null || selection.getRectangles().size() <= 0) {
            readerDataHolder.getReader().getReaderHelper().getReaderLayoutManager().nextScreen();
        } else {
            readerDataHolder.getReaderViewHelper().updatePageView(readerDataHolder,
                    getReaderUserDataInfo(), getReaderViewInfo());
        }
        return this;
    }
}
