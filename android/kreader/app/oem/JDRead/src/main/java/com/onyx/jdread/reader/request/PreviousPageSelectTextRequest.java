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

public class PreviousPageSelectTextRequest extends ReaderBaseRequest {
    private Reader reader;
    private ReaderTextStyle style;
    private ReaderSelectionManager readerSelectionManager;
    private String currentPagePosition;
    private String newPagePosition;
    private float width;
    private float height;
    private ReaderSelection currentPageReaderSelect;
    private PointF currentPageTouchPoint;

    public PreviousPageSelectTextRequest(Reader reader, ReaderTextStyle style, ReaderSelectionManager readerSelectionManager) {
        this.reader = reader;
        this.style = style;
        this.readerSelectionManager = readerSelectionManager;
    }

    @Override
    public PreviousPageSelectTextRequest call() throws Exception {
        width = reader.getReaderViewHelper().getPageViewWidth();
        height = reader.getReaderViewHelper().getPageViewHeight();

        currentPagePosition = reader.getReaderHelper().getReaderLayoutManager().getCurrentPagePosition();

        if (!extendCurrentPageUpperLeftSelectTextRegion()) {
            isSuccess = false;
            return this;
        }

        reader.getReaderHelper().getReaderLayoutManager().prevScreen();
        newPagePosition = reader.getReaderHelper().getReaderLayoutManager().getCurrentPagePosition();

        ReaderSelectionInfo readerSelectionInfo = readerSelectionManager.getReaderSelectionInfo(newPagePosition);
        if (readerSelectionInfo == null) {
            PointF start = new PointF(width, height);
            PointF end = new PointF(style.getPageMargin().getLeftMargin().getPercent(), style.getPageMargin().getTopMargin().getPercent());
            readerSelectionInfo = HitTestTextHelper.hitTestTextRegion(start, end, -ReaderConfig.HIT_TEST_TEXT_STEP, reader, getReaderUserDataInfo(), false, newPagePosition);
            if (readerSelectionInfo == null || readerSelectionInfo.getCurrentSelection().getRectangles().size() <= 0) {
                isSuccess = false;
                reader.getReaderHelper().getReaderLayoutManager().nextScreen();
                return this;
            }
            updateReaderSelectInfo(newPagePosition);
            updateCurrentPageReaderSelect();
            reader.getReaderViewHelper().updatePageView(reader, getReaderViewInfo(), readerSelectionManager);
            HitTestTextHelper.saveLastHighLightPosition(newPagePosition, readerSelectionManager, readerSelectionInfo.getHighLightBeginTop(), readerSelectionInfo.getHighLightEndBottom());
        } else {
            cleanCurrentPageInfo();
        }
        return this;
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
                updateReaderSelectInfo(newPagePosition);
                reader.getReaderViewHelper().updatePageView(reader, getReaderViewInfo(), readerSelectionManager);
            }
        }
    }

    private void updateReaderSelectInfo(String pagePosition) {
        readerSelectionManager.update(pagePosition, reader.getReaderHelper().getContext(),
                getReaderUserDataInfo().getHighlightResult(),
                getReaderUserDataInfo().getTouchPoint());
        readerSelectionManager.updateDisplayPosition(pagePosition);
        readerSelectionManager.setEnable(pagePosition, true);
    }

    private boolean extendCurrentPageUpperLeftSelectTextRegion() {
        ReaderSelectionInfo readerSelectionInfo = readerSelectionManager.getReaderSelectionInfo(currentPagePosition);

        PointF start = new PointF(style.getPageMargin().getLeftMargin().getPercent(), style.getPageMargin().getTopMargin().getPercent());
        PointF end = new PointF(width, height);
        ReaderSelectionInfo newReaderSelectionInfo = HitTestTextHelper.hitTestTextRegion(start, end, ReaderConfig.HIT_TEST_TEXT_STEP, reader, getReaderUserDataInfo(), true, currentPagePosition);
        if (newReaderSelectionInfo != null) {
            readerSelectionInfo.setHighLightBeginTop(newReaderSelectionInfo.getHighLightBeginTop());

            start = readerSelectionInfo.getHighLightBeginTop();
            end = readerSelectionInfo.getHighLightEndBottom();

            ReaderHitTestManager hitTestManager = reader.getReaderHelper().getHitTestManager();
            PageInfo pageInfo = reader.getReaderHelper().getReaderLayoutManager().getPageManager().getPageInfo(currentPagePosition);

            newReaderSelectionInfo = HitTestTextHelper.selectOnScreen(start, end, currentPagePosition, pageInfo, hitTestManager, getReaderUserDataInfo());
            if (newReaderSelectionInfo != null && newReaderSelectionInfo.getCurrentSelection() != null) {
                currentPageReaderSelect = getReaderUserDataInfo().getHighlightResult();
                currentPageTouchPoint = getReaderUserDataInfo().getTouchPoint();
                return true;
            }
        }
        return false;
    }

    private void updateCurrentPageReaderSelect() {
        readerSelectionManager.update(currentPagePosition, reader.getReaderHelper().getContext(),
                currentPageReaderSelect,
                currentPageTouchPoint);
        readerSelectionManager.updateDisplayPosition(currentPagePosition);
        readerSelectionManager.setEnable(currentPagePosition, true);
    }
}
