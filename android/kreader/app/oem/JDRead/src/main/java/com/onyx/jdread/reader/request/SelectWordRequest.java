package com.onyx.jdread.reader.request;

import android.graphics.PointF;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.api.ReaderHitTestArgs;
import com.onyx.android.sdk.reader.api.ReaderHitTestManager;
import com.onyx.android.sdk.reader.api.ReaderHitTestOptions;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.host.math.PageUtils;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.layout.LayoutProviderUtils;

/**
 * Created by huxiaomao on 2018/1/10.
 */

public class SelectWordRequest extends ReaderBaseRequest {
    private PointF start = new PointF();
    private PointF end = new PointF();
    private PointF touchPoint = new PointF();
    private ReaderSelection selection;
    private String pagePosition;
    private ReaderHitTestOptions hitTestOptions;
    private ReaderDataHolder readerDataHolder;

    public SelectWordRequest(ReaderDataHolder readerDataHolder, final String pagePosition, final PointF startPoint, final PointF endPoint, final PointF touchPoint, final ReaderHitTestOptions hitTestOptions) {
        start.set(startPoint.x, startPoint.y);
        end.set(endPoint.x, endPoint.y);
        this.touchPoint.set(touchPoint.x, touchPoint.y);
        this.pagePosition = pagePosition;
        this.hitTestOptions = hitTestOptions;
        this.readerDataHolder = readerDataHolder;
    }

    public PointF getstart() {
        return start;
    }

    public PointF getEnd() {
        return end;
    }

    @Override
    public SelectWordRequest call() throws Exception {
        if (!readerDataHolder.getReader().getReaderHelper().getReaderLayoutManager().getCurrentLayoutProvider().canHitTest()) {
            return this;
        }
        ReaderHitTestManager hitTestManager = readerDataHolder.getReader().getReaderHelper().getHitTestManager();
        PageInfo pageInfo = readerDataHolder.getReader().getReaderHelper().getReaderLayoutManager().getPageManager().getPageInfo(pagePosition);
        ReaderHitTestArgs argsStart = new ReaderHitTestArgs(pagePosition, pageInfo.getDisplayRect(), 0, start);
        ReaderHitTestArgs argsEnd = new ReaderHitTestArgs(pagePosition, pageInfo.getDisplayRect(), 0, end);
        selection = hitTestManager.selectOnScreen(argsStart, argsEnd, hitTestOptions);
        LayoutProviderUtils.updateReaderViewInfo(readerDataHolder.getReader(), getReaderViewInfo(), readerDataHolder.getReader().getReaderHelper().getReaderLayoutManager());
        if (selection != null && selection.getRectangles().size() > 0) {
            getReaderUserDataInfo().saveHighlightResult(translateToScreen(pageInfo, selection));
            getReaderUserDataInfo().setTouchPoint(touchPoint);
            readerDataHolder.getReaderViewHelper().draw(readerDataHolder,
                    readerDataHolder.getReader().getReaderHelper().getViewportBitmap().getBitmap(),
                    getReaderUserDataInfo(),getReaderViewInfo());
        }
        return this;
    }

    private ReaderSelection translateToScreen(PageInfo pageInfo, ReaderSelection selection) {
        int size = selection.getRectangles().size();
        for (int i = 0; i < size; i++) {
            PageUtils.translate(pageInfo.getDisplayRect().left,
                    pageInfo.getDisplayRect().top,
                    pageInfo.getActualScale(),
                    selection.getRectangles().get(i));
        }
        return selection;
    }
}
