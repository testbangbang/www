package com.onyx.jdread.reader.highlight;

import android.graphics.PointF;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.api.ReaderHitTestArgs;
import com.onyx.android.sdk.reader.api.ReaderHitTestManager;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.host.impl.ReaderHitTestOptionsImpl;
import com.onyx.jdread.reader.common.ReaderUserDataInfo;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.request.SelectRequest;

/**
 * Created by huxiaomao on 2018/1/16.
 */

public class HitTestTextHelper {
    public static ReaderSelectionInfo hitTestTextRegion(PointF newPageStartPosition,
                                                    PointF newPageEndPosition,
                                                    int step,
                                                    Reader reader,
                                                    ReaderUserDataInfo readerUserDataInfo,
                                                    boolean isNext,
                                                    String pagePosition) {
        ReaderHitTestManager hitTestManager = reader.getReaderHelper().getHitTestManager();
        PageInfo pageInfo = reader.getReaderHelper().getReaderLayoutManager().getPageManager().getPageInfo(pagePosition);
        ReaderSelectionInfo readerSelectionInfo;
        if (isNext) {
            readerSelectionInfo = nextPageHitTestText(newPageStartPosition, newPageEndPosition, step, pagePosition, pageInfo, hitTestManager, readerUserDataInfo);
        } else {
            readerSelectionInfo = prevPageHitTestText(newPageStartPosition, newPageEndPosition, step, pagePosition, pageInfo, hitTestManager, readerUserDataInfo);
        }
        return readerSelectionInfo;
    }

    private static ReaderSelectionInfo nextPageHitTestText(PointF newPageStartPosition,
                                                       PointF newPageEndPosition,
                                                       int step,
                                                       String pagePosition,
                                                       PageInfo pageInfo,
                                                       ReaderHitTestManager hitTestManager,
                                                       ReaderUserDataInfo readerUserDataInfo) {
        ReaderSelectionInfo readerSelectionInfo;
        for (float y = newPageStartPosition.y; y < newPageEndPosition.y; y += step) {
            for (float x = newPageStartPosition.x; x < newPageEndPosition.x; x += step) {
                PointF start = new PointF(x, y);
                PointF end = new PointF(x, y);
                readerSelectionInfo = selectOnScreen(start, end, pagePosition, pageInfo, hitTestManager, readerUserDataInfo);
                if (readerSelectionInfo != null && readerSelectionInfo.getCurrentSelection().getRectangles().size() >= 0) {
                    readerSelectionInfo.setHighLightBeginTop(start);
                    readerSelectionInfo.setHighLightEndBottom(end);
                    readerSelectionInfo.pageInfo = pageInfo;
                    return readerSelectionInfo;
                }
            }
        }
        return null;
    }

    private static ReaderSelectionInfo prevPageHitTestText(PointF newPageStartPosition,
                                                       PointF newPageEndPosition,
                                                       int step,
                                                       String pagePosition,
                                                       PageInfo pageInfo,
                                                       ReaderHitTestManager hitTestManager,
                                                       ReaderUserDataInfo readerUserDataInfo) {
        ReaderSelectionInfo readerSelectionInfo;
        for (float y = newPageStartPosition.y; y > newPageEndPosition.y; y += step) {
            for (float x = newPageStartPosition.x; x > newPageEndPosition.x; x += step) {
                PointF start = new PointF(x, y);
                PointF end = new PointF(x, y);
                readerSelectionInfo = selectOnScreen(start, end, pagePosition, pageInfo, hitTestManager, readerUserDataInfo);
                if (readerSelectionInfo != null && readerSelectionInfo.getCurrentSelection().getRectangles().size() >= 0) {
                    readerSelectionInfo.setHighLightBeginTop(start);
                    readerSelectionInfo.setHighLightEndBottom(end);
                    return readerSelectionInfo;
                }
            }
        }
        return null;
    }

    public static ReaderSelectionInfo selectOnScreen(PointF start, PointF end, String pagePosition, PageInfo pageInfo, ReaderHitTestManager hitTestManager, ReaderUserDataInfo readerUserDataInfo) {
        ReaderHitTestArgs argsStart = new ReaderHitTestArgs(pagePosition, pageInfo.getDisplayRect(), 0, start);
        ReaderHitTestArgs argsEnd = new ReaderHitTestArgs(pagePosition, pageInfo.getDisplayRect(), 0, end);
        ReaderSelection selection = hitTestManager.selectOnScreen(argsStart, argsEnd, ReaderHitTestOptionsImpl.create(false));
        if (selection != null && selection.getRectangles().size() >= 0) {
            PointF touchPoint = new PointF(end.x, end.y);
            readerUserDataInfo.saveHighlightResult(SelectRequest.translateToScreen(pageInfo, selection));
            readerUserDataInfo.setTouchPoint(touchPoint);
            ReaderSelectionInfo readerSelectionInfo = new ReaderSelectionInfo();
            readerSelectionInfo.setCurrentSelection(selection,pageInfo);
            return readerSelectionInfo;
        }
        return null;
    }

    public static void saveLastHighLightPosition(String pagePosition,ReaderSelectionManager readerSelectionManager,PointF start,PointF end){
        ReaderSelectionInfo readerSelectionInfo = readerSelectionManager.getReaderSelectionInfo(pagePosition);
        if(readerSelectionInfo != null){
            readerSelectionInfo.setHighLightBeginTop(start);
            readerSelectionInfo.setHighLightEndBottom(end);
        }
    }
}
