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

public class NextPageSelectTextRequest extends ReaderBaseRequest {
    private Reader reader;
    private ReaderTextStyle style;

    public NextPageSelectTextRequest(Reader reader, ReaderTextStyle style) {
        this.reader = reader;
        this.style = style;
    }

    @Override
    public NextPageSelectTextRequest call() throws Exception {
        float x = reader.getReaderViewHelper().getPageViewWidth();
        float y = reader.getReaderViewHelper().getPageViewHeight();

        reader.getReaderHelper().getReaderLayoutManager().nextScreen();

        PointF start = new PointF(style.getPageMargin().getLeftMargin().getPercent(), style.getPageMargin().getTopMargin().getPercent());
        PointF end = new PointF(x, y);

        ReaderSelection selection = HitTestTextHelper.hitTestTextRegion(start, end, ReaderConfig.HIT_TEST_TEXT_STEP, reader, getReaderUserDataInfo(), true);
        if (selection == null || selection.getRectangles().size() <= 0) {
            reader.getReaderHelper().getReaderLayoutManager().prevScreen();
        } else {
            reader.getReaderViewHelper().updatePageView(reader, getReaderUserDataInfo(), getReaderViewInfo());
        }
        return this;
    }
}
