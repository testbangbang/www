package com.onyx.jdread.reader.request;

import android.graphics.PointF;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.highlight.HitTestTextHelper;
import com.onyx.jdread.reader.menu.common.ReaderConfig;

/**
 * Created by huxiaomao on 2018/1/15.
 */

public class PreviousPageSelectTextRequest extends ReaderBaseRequest {
    private Reader reader;
    private ReaderTextStyle style;

    public PreviousPageSelectTextRequest(Reader reader, ReaderTextStyle style) {
        this.reader = reader;
        this.style = style;
    }

    @Override
    public PreviousPageSelectTextRequest call() throws Exception {
        float width = reader.getReaderViewHelper().getPageViewWidth();
        float height = reader.getReaderViewHelper().getPageViewHeight();
        reader.getReaderHelper().getReaderLayoutManager().prevScreen();

        PointF start = new PointF(width, height);
        PointF end = new PointF(style.getPageMargin().getLeftMargin().getPercent(), style.getPageMargin().getTopMargin().getPercent());

        ReaderSelection selection = HitTestTextHelper.hitTestTextRegion(start, end, -ReaderConfig.HIT_TEST_TEXT_STEP, reader, getReaderUserDataInfo(), false);
        if (selection == null || selection.getRectangles().size() <= 0) {
            reader.getReaderHelper().getReaderLayoutManager().nextScreen();
        } else {
            reader.getReaderViewHelper().updatePageView(reader,
                    getReaderUserDataInfo(), getReaderViewInfo());
        }
        return this;
    }
}
