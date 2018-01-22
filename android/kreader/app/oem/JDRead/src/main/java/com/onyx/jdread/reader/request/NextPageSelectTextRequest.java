package com.onyx.jdread.reader.request;

import android.graphics.PointF;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.api.ReaderHitTestManager;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.highlight.HitTestTextHelper;
import com.onyx.jdread.reader.highlight.ReaderSelectionInfo;
import com.onyx.jdread.reader.highlight.ReaderSelectionManager;
import com.onyx.jdread.reader.menu.common.ReaderConfig;

/**
 * Created by huxiaomao on 2018/1/15.
 */

public class NextPageSelectTextRequest extends ReaderBaseRequest {
    private Reader reader;
    private ReaderTextStyle style;
    private ReaderSelectionManager readerSelectionManager;
    private String currentPagePosition;
    private String newPagePosition;
    private float width;
    private float height;
    private ReaderSelection currentPageReaderSelect;
    private PointF currentPageTouchPoint;
    private PageInfo pageInfo;

    public NextPageSelectTextRequest(Reader reader, ReaderTextStyle style) {
        this.reader = reader;
        this.style = style;
    }

    @Override
    public NextPageSelectTextRequest call() throws Exception {
        width = reader.getReaderViewHelper().getPageViewWidth();
        height = reader.getReaderViewHelper().getPageViewHeight();
        readerSelectionManager = reader.getReaderSelectionHelper();
        currentPagePosition = reader.getReaderHelper().getReaderLayoutManager().getCurrentPagePosition();

        if (!extendCurrentPageLowerRightSelectTextRegion()) {
            isSuccess = false;
            return this;
        }

        reader.getReaderHelper().getReaderLayoutManager().nextScreen();
        newPagePosition = reader.getReaderHelper().getReaderLayoutManager().getCurrentPagePosition();

        ReaderSelectionInfo readerSelectionInfo = readerSelectionManager.getReaderSelectionInfo(newPagePosition);
        if (readerSelectionInfo == null) {
            PointF start = new PointF(style.getPageMargin().getLeftMargin().getPercent(), style.getPageMargin().getTopMargin().getPercent());
            PointF end = new PointF(width, height);
            readerSelectionInfo = HitTestTextHelper.hitTestTextRegion(start, end, ReaderConfig.HIT_TEST_TEXT_STEP, reader, getReaderUserDataInfo(), true, newPagePosition);
            if (readerSelectionInfo == null || readerSelectionInfo.getCurrentSelection().getRectangles().size() <= 0) {
                isSuccess = false;
                reader.getReaderHelper().getReaderLayoutManager().prevScreen();
                return this;
            }
            updateReaderSelectInfo(newPagePosition,readerSelectionInfo.pageInfo);
            updateCursorState(newPagePosition,0,false);
            updateCurrentPageReaderSelect();
            reader.getReaderViewHelper().updatePageView(reader, getReaderUserDataInfo(),getReaderViewInfo(), readerSelectionManager);
            HitTestTextHelper.saveLastHighLightPosition(newPagePosition, readerSelectionManager, readerSelectionInfo.getHighLightBeginTop(), readerSelectionInfo.getHighLightEndBottom());
        } else {
            cleanCurrentPageInfo();
        }
        getSelectionInfoManager().updateSelectInfo(readerSelectionManager.getReaderSelectionInfos());
        return this;
    }

    private void updateReaderSelectInfo(String pagePosition,PageInfo pageInfo) {
        readerSelectionManager.update(pagePosition, reader.getReaderHelper().getContext(),
                getReaderUserDataInfo().getHighlightResult(),
                getReaderUserDataInfo().getTouchPoint(),
                pageInfo);
        readerSelectionManager.updateDisplayPosition(pagePosition);
        readerSelectionManager.setEnable(pagePosition, true);

    }

    private void updateCursorState(String pagePosition, int index, boolean isShow) {
        readerSelectionManager.getHighlightCursor(pagePosition, index).isShow(isShow);
    }

    private void cleanCurrentPageInfo() {
        readerSelectionManager.deletePageSelection(currentPagePosition);

        ReaderSelectionInfo readerSelectionInfo = readerSelectionManager.getReaderSelectionInfo(newPagePosition);
        if (readerSelectionInfo != null) {
            PointF start = readerSelectionInfo.getHighLightBeginTop();
            PointF end = readerSelectionInfo.getHighLightEndBottom();
            ReaderHitTestManager hitTestManager = reader.getReaderHelper().getHitTestManager();
            PageInfo pageInfo = reader.getReaderHelper().getReaderLayoutManager().getPageManager().getPageInfo(newPagePosition);
            readerSelectionInfo = HitTestTextHelper.selectOnScreen(start, end, newPagePosition, pageInfo, hitTestManager, getReaderUserDataInfo());
            if (readerSelectionInfo != null && readerSelectionInfo.getCurrentSelection() != null) {
                updateReaderSelectInfo(newPagePosition,pageInfo);
                updateCursorState(newPagePosition,0,true);
                reader.getReaderViewHelper().updatePageView(reader, getReaderUserDataInfo(),getReaderViewInfo(), readerSelectionManager);
            }
        }
    }

    private boolean extendCurrentPageLowerRightSelectTextRegion() {
        ReaderSelectionInfo readerSelectionInfo = readerSelectionManager.getReaderSelectionInfo(currentPagePosition);

        PointF start = new PointF(width, height);
        PointF end = new PointF(style.getPageMargin().getLeftMargin().getPercent(), style.getPageMargin().getTopMargin().getPercent());
        ReaderSelectionInfo newReaderSelectionInfo = HitTestTextHelper.hitTestTextRegion(start, end, -ReaderConfig.HIT_TEST_TEXT_STEP, reader, getReaderUserDataInfo(), false, currentPagePosition);
        if (newReaderSelectionInfo != null) {
            readerSelectionInfo.setHighLightEndBottom(newReaderSelectionInfo.getHighLightEndBottom());

            start = readerSelectionInfo.getHighLightBeginTop();
            end = readerSelectionInfo.getHighLightEndBottom();

            ReaderHitTestManager hitTestManager = reader.getReaderHelper().getHitTestManager();
            PageInfo pageInfo = reader.getReaderHelper().getReaderLayoutManager().getPageManager().getPageInfo(currentPagePosition);

            newReaderSelectionInfo = HitTestTextHelper.selectOnScreen(start, end, currentPagePosition, pageInfo, hitTestManager, getReaderUserDataInfo());
            if (newReaderSelectionInfo != null && newReaderSelectionInfo.getCurrentSelection() != null) {
                currentPageReaderSelect = getReaderUserDataInfo().getHighlightResult();
                currentPageTouchPoint = getReaderUserDataInfo().getTouchPoint();
                this.pageInfo = pageInfo;
                return true;
            }
        }
        return false;
    }

    private void updateCurrentPageReaderSelect() {
        readerSelectionManager.update(currentPagePosition, reader.getReaderHelper().getContext(),
                currentPageReaderSelect,
                currentPageTouchPoint,
                pageInfo);
        readerSelectionManager.updateDisplayPosition(currentPagePosition);
        readerSelectionManager.setEnable(currentPagePosition, true);
    }
}
