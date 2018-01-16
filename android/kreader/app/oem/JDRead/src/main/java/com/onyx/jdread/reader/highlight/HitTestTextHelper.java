package com.onyx.jdread.reader.highlight;

import android.graphics.PointF;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.api.ReaderHitTestArgs;
import com.onyx.android.sdk.reader.api.ReaderHitTestManager;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.host.impl.ReaderHitTestOptionsImpl;
import com.onyx.jdread.reader.common.ReaderUserDataInfo;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.SelectRequest;

/**
 * Created by huxiaomao on 2018/1/16.
 */

public class HitTestTextHelper {
    public static ReaderSelection hitTestTextRegion(PointF newPageStartPosition,
                                                    PointF newPageEndPosition,
                                                    int step,
                                                    ReaderDataHolder readerDataHolder,
                                                    ReaderUserDataInfo readerUserDataInfo,
                                                    boolean isNext) {
        String pagePosition = readerDataHolder.getReader().getReaderHelper().getReaderLayoutManager().getCurrentPagePosition();
        ReaderHitTestManager hitTestManager = readerDataHolder.getReader().getReaderHelper().getHitTestManager();
        PageInfo pageInfo = readerDataHolder.getReader().getReaderHelper().getReaderLayoutManager().getPageManager().getPageInfo(pagePosition);
        ReaderSelection selection;
        if (isNext) {
            selection = nextPageHitTestText(newPageStartPosition, newPageEndPosition, step, pagePosition, pageInfo, hitTestManager, readerUserDataInfo);
        } else {
            selection = prevPageHitTestText(newPageStartPosition, newPageEndPosition, step, pagePosition, pageInfo, hitTestManager, readerUserDataInfo);
        }
        return selection;
    }

    private static ReaderSelection nextPageHitTestText(PointF newPageStartPosition,
                                                       PointF newPageEndPosition,
                                                       int step,
                                                       String pagePosition,
                                                       PageInfo pageInfo,
                                                       ReaderHitTestManager hitTestManager,
                                                       ReaderUserDataInfo readerUserDataInfo) {
        ReaderSelection selection;
        for (float y = newPageStartPosition.y; y < newPageEndPosition.y; y += step) {
            for (float x = newPageStartPosition.x; x < newPageEndPosition.x; x += step) {
                selection = selectOnScreen(x, y, pagePosition, pageInfo, hitTestManager, readerUserDataInfo);
                if (selection != null && selection.getRectangles().size() >= 0) {
                    return selection;
                }
            }
        }
        return null;
    }

    private static ReaderSelection prevPageHitTestText(PointF newPageStartPosition,
                                                       PointF newPageEndPosition,
                                                       int step,
                                                       String pagePosition,
                                                       PageInfo pageInfo,
                                                       ReaderHitTestManager hitTestManager,
                                                       ReaderUserDataInfo readerUserDataInfo) {
        ReaderSelection selection;
        for (float y = newPageStartPosition.y; y > newPageEndPosition.y; y += step) {
            for (float x = newPageStartPosition.x; x > newPageEndPosition.x; x += step) {
                selection = selectOnScreen(x, y, pagePosition, pageInfo, hitTestManager, readerUserDataInfo);
                if (selection != null && selection.getRectangles().size() >= 0) {
                    return selection;
                }
            }
        }
        return null;
    }

    private static ReaderSelection selectOnScreen(float x, float y, String pagePosition, PageInfo pageInfo, ReaderHitTestManager hitTestManager, ReaderUserDataInfo readerUserDataInfo) {
        PointF start = new PointF(x, y);
        PointF end = new PointF(x, y);

        ReaderHitTestArgs argsStart = new ReaderHitTestArgs(pagePosition, pageInfo.getDisplayRect(), 0, start);
        ReaderHitTestArgs argsEnd = new ReaderHitTestArgs(pagePosition, pageInfo.getDisplayRect(), 0, end);
        ReaderSelection selection = hitTestManager.selectOnScreen(argsStart, argsEnd, ReaderHitTestOptionsImpl.create(false));
        if (selection != null && selection.getRectangles().size() >= 0) {
            PointF touchPoint = new PointF(x, y);
            readerUserDataInfo.saveHighlightResult(SelectRequest.translateToScreen(pageInfo, selection));
            readerUserDataInfo.setTouchPoint(touchPoint);
            return selection;
        }
        return null;
    }
}
